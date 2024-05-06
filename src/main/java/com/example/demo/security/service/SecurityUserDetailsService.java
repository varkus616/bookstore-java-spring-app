package com.example.demo.security.service;

import com.example.demo.model.Cart;
import com.example.demo.model.PurchaseHistory;
import com.example.demo.repository.CartRepository;
import com.example.demo.security.repository.RoleRepository;
import jakarta.persistence.EntityExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;
import com.example.demo.security.model.User;
import com.example.demo.security.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Primary
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    public SecurityUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not present"));


        return user;
    }
    public void registerUser(Map<String, String> body) {
        String username = body.get("username");

        User user = new User();
        user.setUsername(username);
        user.setEmail(body.get("email"));
        user.setPassword(passwordEncoder.encode(body.get("password")));
        user.setAccountNonLocked(true);

        user.setRoles(Arrays.asList(
                roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() ->
                                new RuntimeException("Role not found"))
        ));


        Cart cart = new Cart();
        user.setCart(cart);
        cart.setUser(user);
        cartRepository.save(cart);

        user.setCart(cart);

        PurchaseHistory purchaseHistory = new PurchaseHistory();
        user.setPurchaseHistory(purchaseHistory);
        purchaseHistory.setUser(user);

        System.out.println(user);

        userRepository.save(user);
        //this.createUser(user);
    }

    public void createUser(UserDetails user) {
        userRepository.save((User) user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.getReferenceById(userId);
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    public void updateUser(String userId, User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            // Update other fields as needed
            userRepository.save(user);
        } else {
            // Handle user not found error
            throw new RuntimeException("User not found with id: " + userId);
        }
    }
}
