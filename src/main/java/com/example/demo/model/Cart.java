package com.example.demo.model;

import com.example.demo.security.model.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToOne(mappedBy = "cart", cascade = CascadeType.ALL)
    private User user;

    @Transactional
    public void addCartItem(CartItem cartItem) {

        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    @Transactional
    public void removeCartItem(CartItem cartItem) {

        cartItems.remove(cartItem);
        cartItem.setCart(null);
    }

    @Transactional
    public void clearCart()
    {
        cartItems.forEach(item -> item.setCart(null));
        cartItems.clear();
    }
    public boolean hasCartItem(CartItem cartItem){
        return cartItems.contains(cartItem);
    }
}



