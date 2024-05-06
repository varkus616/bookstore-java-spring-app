package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.model.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
        Optional<OrderItem> findOrderItemByOrderId(Long orderId);
        Optional<OrderItem> findOrderItemById(Long id);

}
