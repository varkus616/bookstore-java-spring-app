package com.example.demo.security.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Component;


import com.example.demo.security.model.Attempts;
import com.example.demo.security.model.User;
import com.example.demo.security.repository.AttemptsRepository;
import com.example.demo.security.repository.UserRepository;

@Component public class CustomAuthenticationProvider implements org.springframework.security.authentication.AuthenticationProvider {
    private static final int ATTEMPTS_LIMIT = 3;


    private SecurityUserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;
    @Autowired private AttemptsRepository attemptsRepository;
    @Autowired private UserRepository userRepository;

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setUserDetailsService(SecurityUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();
        UserDetails userDetails;
        try {
            userDetails = userDetailsService.
                    loadUserByUsername(username);
        }catch (UsernameNotFoundException ex){
            throw new UsernameNotFoundException("User not found.");
        }
        Optional<User> optionalUser = userRepository.
                findUserByUsername(username);
        if (optionalUser.isPresent())
        {
            User user = optionalUser.get();

            if(!user.isAccountNonLocked()){
                throw new LockedException("User account is locked.");
            }
            if(!passwordEncoder.matches(password, userDetails.getPassword())){

                processFailedAttempts(username, user);
                throw new BadCredentialsException("Invalid credentials.");
            }
            resetFailedAttempts(username);
        }else{
            System.out.println("not present");
        }
        System.out.println(userDetails.getAuthorities());
        return new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities());
    }
    private void processFailedAttempts(String username, User user) {
        Optional<Attempts>
                userAttempts = attemptsRepository.findAttemptsByUsername(username);
        if (userAttempts.isEmpty()) {
            Attempts attempts = new Attempts();
            attempts.setUsername(username);
            attempts.setAttempts(1);
            attemptsRepository.save(attempts);
        } else {
            Attempts attempts = userAttempts.get();
            attempts.setAttempts(attempts.getAttempts() + 1);
            attemptsRepository.save(attempts);

            if (attempts.getAttempts() + 1 >
                    ATTEMPTS_LIMIT) {
                user.setAccountNonLocked(false);
                userRepository.save(user);
                throw new LockedException("Too many invalid attempts. Account is locked!!");
            }
        }
    }

    private void resetFailedAttempts(String username){
        Optional<Attempts> userAttemps = attemptsRepository
                .findAttemptsByUsername(username);
        if (userAttemps.isPresent()){
            Attempts attempts = userAttemps.get();
            attempts.setAttempts(0);
            attemptsRepository.save(attempts);
        }
    }

    @Override public boolean supports(Class<?> authentication) {
        return true;
    }
}