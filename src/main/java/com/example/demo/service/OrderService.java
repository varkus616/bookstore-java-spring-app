package com.example.demo.service;

import com.example.demo.model.PurchaseOrder;
import com.example.demo.security.model.User;
import com.example.demo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserDetailsService userService;

    public List<PurchaseOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public PurchaseOrder createOrder(PurchaseOrder order) {
        return orderRepository.save(order);
    }

    public Optional<PurchaseOrder> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    public PurchaseOrder updateOrder(Long orderId, PurchaseOrder updatedOrder) {
        if (orderRepository.existsById(orderId)) {
            updatedOrder.setId(orderId);
            return orderRepository.save(updatedOrder);
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }

    public void deleteOrder(Long orderId) {
        if (orderRepository.existsById(orderId)) {
            orderRepository.deleteById(orderId);
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }
}
