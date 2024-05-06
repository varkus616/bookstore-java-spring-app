package com.example.demo.model;

import com.example.demo.security.model.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
public class PurchaseHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "purchaseHistory", cascade = CascadeType.ALL)
    private List<PurchaseOrder> purchaseOrders;

    private double totalAmount;

    public void addNewOrder(PurchaseOrder order)
    {
        purchaseOrders.add(order);
    }

    public void calculateTotalAmount()
    {
        this.totalAmount = 0;
        for (PurchaseOrder order: purchaseOrders)
            this.totalAmount += order.getTotalAmount();
    }

}