package com.example.demo.security.controller;

import java.util.Map;

import com.example.demo.model.Cart;
import com.example.demo.model.PurchaseHistory;
import com.example.demo.repository.CartRepository;
import com.example.demo.security.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.security.model.User;
import com.example.demo.security.service.SecurityUserDetailsService;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthenticationController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired private SecurityUserDetailsService userDetailsManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
    public String index(HttpServletRequest request, HttpSession session) {
        return "index";
    }

    @GetMapping("/about")
    public String about(HttpServletRequest request, HttpSession session) {
        return "about";
    }

    @GetMapping("/contact")
    public String contact(HttpServletRequest request, HttpSession session) {
        return "contact";
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpSession session)
    {
        return "logout";
    }

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpSession session) {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));

        return "authentication/login";
    }

    @GetMapping("/register")
    public String register(HttpServletRequest request, HttpSession session)
    {
        session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "authentication/register";
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE, produces = {
            MediaType.APPLICATION_ATOM_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }
    )
    public String addUser(@RequestParam Map<String, String> body,
                          RedirectAttributes redirectAttributes,
                          HttpSession session,
                          HttpServletRequest request)
    {
        if (userRepository.existsByUsername(body.get("username"))) {
            session.setAttribute("error", "User already exists with this username.");
            System.out.println(session.getAttribute("error"));
            return "redirect:/register";
        }
        userDetailsManager.registerUser(body);
        //session.setAttribute("error", getErrorMessage(request, "SPRING_SECURITY_LAST_EXCEPTION"));
        return "redirect:/login";
    }

    @ExceptionHandler(EntityExistsException.class)
    private String getErrorMessage(HttpServletRequest request, String key) {
        Exception exception = (Exception) request.getSession().getAttribute(key);
        String error = "";
        if (exception instanceof BadCredentialsException) {
            error = "Invalid username and password!";
        } else if (exception instanceof LockedException) {
            error = exception.getMessage();
        }else if (exception instanceof EntityExistsException) {
            error = exception.getMessage();
        }else{
            error = "Invalid username and password!";
        }
        return error;
    }

}