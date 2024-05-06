package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private PurchaseOrder order;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    private int quantity;

    private double price;

    public OrderItem()
    {

    }
    public OrderItem(Book book, int quantity, double price, PurchaseOrder order)
    {
        this.book=book;
        this.quantity=quantity;
        this.price=price;
        this.order = order;
    }



}
