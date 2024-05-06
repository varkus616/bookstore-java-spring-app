package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long>{

    Optional<CartItem> findCartItemById(Long id);

    Optional<CartItem> findCartItemByBookTitle(String title);
}
