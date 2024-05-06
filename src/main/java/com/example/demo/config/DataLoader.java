package com.example.demo.config;

import com.example.demo.model.Book;
import com.example.demo.repository.BookRepository;
import com.example.demo.security.model.Privilege;
import com.example.demo.security.model.Role;
import com.example.demo.security.model.User;
import com.example.demo.security.repository.PrivilegeRepository;
import com.example.demo.security.repository.RoleRepository;
import com.example.demo.security.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder)
    {
        return builder.build();
    }

    @Override
    public void run(String... args) throws Exception {

        this.createAdminUser();

        RestTemplateBuilder builder = new RestTemplateBuilder();

       List<Book> books = getRandomBooks(builder,
               "https://openlibrary.org/search.json?title=*&author=knuth");

        bookRepository.saveAll(books);

    }
    public List<Book> getRandomBooks(RestTemplateBuilder builder, String url)
    {
        List<Map<String, Object>> randomBooksData = getRandomBooksFromApi(restTemplate(builder),
                url);

        List<Book> books = new ArrayList<>();

        for (Map<String, Object> bookData : randomBooksData) {
            String title = (String) bookData.getOrDefault("title", "Title not available");
            List<String> tempAuthors = (List<String>) bookData.getOrDefault("author_name",
                    Collections.singletonList("Author not known"));
            String author = tempAuthors.isEmpty() ? "Author not known" : tempAuthors.get(0);

            String coverUrl = "https://via.placeholder.com/300"; // Default cover URL
            if (bookData.containsKey("cover_edition_key")) {
                String cover_key = (String) bookData.get("cover_edition_key");
                coverUrl = "https://covers.openlibrary.org/b/olid/" + cover_key + "-M.jpg";
            } else if (bookData.containsKey("cover_i")) {
                String cover_key = bookData.get("cover_i").toString();
                coverUrl = "https://covers.openlibrary.org/b/id/" + cover_key + "-M.jpg";
            }

            Book book = new Book(title, author, "G", "", 10, 12, coverUrl);
            books.add(book);
        }
        return books;
    }


    private List<Map<String, Object>> getRandomBooksFromApi(RestTemplate restTemplate,
                                                    String targetUrl)
    {
        String url = targetUrl;
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null && response.containsKey("docs")) {

            List<Map<String, Object>> books = (List<Map<String, Object>>) response.get("docs");
            int totalBooks = books.size();
            if (totalBooks >= 50) {
                return getRandomSelection(books, 50);
            } else {
                return books;
            }
        } else {
            throw new RuntimeException("Couldn't download data.");
        }
    }
    private List<Map<String, Object>> getRandomSelection(List<Map<String, Object>> books, int count) {
        Random random = new Random();
        List<Map<String, Object>> randomBooks = random.ints(0, books.size())
                .distinct()
                .limit(count)
                .mapToObj(books::get)
                .toList();
        return randomBooks;
    }

    @Transactional
    private void createAdminUser()
    {
        Privilege readPrivilege
                = createPrivilegeIfNotFound("READ_PRIVILEGE");
        Privilege writePrivilege
                = createPrivilegeIfNotFound("WRITE_PRIVILEGE");

        List<Privilege> adminPrivileges = Arrays.asList(
                readPrivilege, writePrivilege);
        createRoleIfNotFound("ROLE_ADMIN", adminPrivileges);
        createRoleIfNotFound("ROLE_USER", Arrays.asList(readPrivilege));

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(()->new RuntimeException("Can't find role"));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()->new RuntimeException("Can't find role"));

        User user = new User();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.setEmail("test@test.com");
        user.setRoles(Arrays.asList(adminRole, userRole));
        user.setAccountNonLocked(true);
        userRepository.save(user);
    }
    @Transactional
    Privilege createPrivilegeIfNotFound(String name) {

        Privilege privilege = privilegeRepository.findByName(name)
                .orElse(null);
        if (privilege == null) {
            privilege = new Privilege(name);
            privilegeRepository.save(privilege);
        }
        return privilege;
    }

    @Transactional
    Role createRoleIfNotFound(
            String name, Collection<Privilege> privileges) {

        Role role = roleRepository.findByName(name)
                .orElse(null);
        if (role == null) {
            role = new Role(name);
            role.setPrivileges(privileges);
            roleRepository.save(role);
        }
        return role;
    }
}
