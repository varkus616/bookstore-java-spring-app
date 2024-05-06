package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.security.model.User;
import com.example.demo.security.service.SecurityUserDetailsService;
import com.example.demo.service.BookService;
import com.example.demo.service.CartService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private BookService bookService;

    @Autowired
    private CartService cartService;

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderItemRepository orderItemsRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/")
    public String viewCart(Model model) {
        User currentUser = getCurrentUser();
        Cart mycart = getCurrentUserCart(currentUser);

        model.addAttribute("cart", mycart);
        return "cart/cart_view";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long bookId, @RequestParam int quantity, Principal principal) {
        String username = principal.getName();
        User currentUser = (User) userDetailsService.loadUserByUsername(username);
        cartService.addToCart(bookId, quantity, currentUser);
        return "redirect:/cart/";
    }



    @GetMapping("/buy")
    public String buy(){return "redirect:/cart/";}


    @PostMapping("/buy")
    @Transactional
    public String buyCartProducts(Principal principal) {
        User currentUser = cartService.getCurrentUser(principal);
        cartService.buyCartProducts(currentUser);
        return "redirect:/cart/";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long cartItemId, Principal principal) {
        User currentUser = cartService.getCurrentUser(principal);
        cartService.removeFromCart(cartItemId, currentUser);
        return "redirect:/cart/";
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        return (User) userDetailsService.loadUserByUsername(currentUsername);
    }

    private Cart getCurrentUserCart(User user) {
        return cartService.getCartByUser(user);
    }
}
