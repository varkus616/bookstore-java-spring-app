package com.example.demo.model;

import com.example.demo.security.model.User;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Getter
@Entity
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "purchaseHistory_id")
    private PurchaseHistory purchaseHistory;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    private double totalAmount;
    private LocalDateTime orderDate;

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getFullNumberOfItems()
    {
        return this.getOrderItems()
                .stream()
                .map(item -> item.getQuantity())
                .reduce(0, (a, b) -> { return a+b; });
    }
    public PurchaseOrder(){}
    public PurchaseOrder(LocalDateTime orderDate, PurchaseHistory purchaseHistory, User user)
    {
        this.orderDate=orderDate;
        this.purchaseHistory=purchaseHistory;
        this.user=user;

    }

    public void calculateTotalAmount() {
        double total = 0.0;
        for (OrderItem orderItem : orderItems) {
            total += orderItem.getPrice() * orderItem.getQuantity();
        }
        this.totalAmount = total;
    }
}
