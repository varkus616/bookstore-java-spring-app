package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.BookRepository;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.security.model.User;


import com.example.demo.security.service.SecurityUserDetailsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BookService bookService;

    @Autowired
    private PurchaseHistoryService purchaseHistoryService;
    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private OrderService orderService;


    public Cart getCartByUser(User user)
    {
        return cartRepository.findByUser(user)
                    .orElseThrow(()->new EntityNotFoundException("Cart not found"));
    }

    @Transactional
    public void addToCart(Long bookId, int quantity, User currentUser) {
        Cart mycart = this.getCartByUser(currentUser);
        Book book = bookService.getBookById(bookId);

        if (book != null) {
            int availableQuantity = book.getStockQuantity();

            if (availableQuantity < quantity) {
                throw new IllegalStateException("Not enough books in the stock.");
            } else {
                book.setStockQuantity(availableQuantity - quantity);

                Optional<CartItem> existingItemOptional = mycart.getCartItems().stream()
                        .filter(item -> item.getBook().getId().equals(bookId))
                        .findFirst();

                if (existingItemOptional.isPresent()) {
                    CartItem existingItem = existingItemOptional.get();
                    existingItem.setQuantity(existingItem.getQuantity() + quantity);
                   // cartItemRepository.save(existingItem);
                } else {
                    CartItem cartItem = new CartItem();
                    cartItem.setBook(book);
                    cartItem.setQuantity(quantity);
                    cartItem.setPrice(book.getPrice());

                    mycart.addCartItem(cartItem);
                }
            }
        }
    }

    public User getCurrentUser(Principal principal) {
        String username = principal.getName();
        return (User) userDetailsService.loadUserByUsername(username);
    }

    public Cart getCurrentUserCart(User currentUser) {
        return currentUser.getCart();
    }

    @Transactional
    public void buyCartProducts(User currentUser) {
        Cart mycart = getCurrentUserCart(currentUser);
        PurchaseHistory userHistory = purchaseHistoryService.findByUser(currentUser);

        PurchaseOrder newOrder = new PurchaseOrder(LocalDateTime.now(), userHistory, currentUser);
        List<CartItem> cartItems = mycart.getCartItems();
        List<OrderItem> orderItems = new ArrayList<>();

        cartItems.forEach(cartItem -> orderItems.add(
                new OrderItem(cartItem.getBook(), cartItem.getQuantity(), cartItem.getPrice(), newOrder)
        ));

        mycart.clearCart();

        newOrder.setOrderItems(orderItems);
        newOrder.calculateTotalAmount();

        userHistory.addNewOrder(newOrder);
        userHistory.calculateTotalAmount();

    }

    @Transactional
    public void removeFromCart(Long cartItemId, User currentUser) {
        Cart mycart = getCurrentUserCart(currentUser);
        CartItem item = cartItemRepository.findCartItemById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));
        if (item.getQuantity() >= 2) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            mycart.removeCartItem(item);
        }
        cartRepository.save(mycart);
    }
    @Transactional
    public void clearCart(User currentUser) {
        Cart mycart = getCurrentUserCart(currentUser);
        mycart.getCartItems().clear();
        cartRepository.save(mycart);
    }


}
