package com.example.demo.security.service;

import com.example.demo.model.Cart;
import com.example.demo.model.PurchaseHistory;
import com.example.demo.repository.CartRepository;
import com.example.demo.security.repository.RoleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
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
import org.springframework.web.bind.annotation.ModelAttribute;

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

    public void registerUser(@ModelAttribute User modelUser) {
        String username = modelUser.getUsername();

        try {
            if (userRepository.findUserByUsername(username).isPresent()) {
                throw new RuntimeException("Użytkownik o podanej nazwie już istnieje");
            }

            User user = new User();
            user.setUsername(username);
            user.setEmail(modelUser.getEmail());
            user.setPassword(passwordEncoder.encode(modelUser.getPassword()));
            user.setAccountNonLocked(true);
            user.setRoles(Arrays.asList(
                    roleRepository.findByName("ROLE_USER")
                            .orElseThrow(() -> new RuntimeException("Rola nie została znaleziona"))
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

            this.createUser(user);

        } catch (RuntimeException e) {
            System.err.println("Błąd podczas rejestracji użytkownika: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Nieoczekiwany błąd: " + e.getMessage());
            throw new RuntimeException("Rejestracja użytkownika nie powiodła się", e);
        }
    }

    @Transactional
    private void createUser(UserDetails user) {
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
            userRepository.save(user);
        } else {
            throw new RuntimeException("User not found with id: " + userId);
        }
    }

    @Transactional
    public void changePassword(String username, String currentPassword, String newPassword) {

        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        String encodedNewPassword = passwordEncoder.encode(newPassword);

        user.setPassword(encodedNewPassword);
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(String username, String passwordConfirmation) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(passwordConfirmation, user.getPassword())) {
            throw new IllegalArgumentException("Password confirmation is incorrect.");
        }

        userRepository.delete(user);
    }

}
