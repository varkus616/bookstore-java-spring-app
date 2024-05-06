package com.example.demo.controller;

import com.example.demo.model.PurchaseHistory;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.repository.PurchaseHistoryRepository;

import java.util.List;

@Controller
@RequestMapping("/purchase")
public class PurchaseController {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    public PurchaseController(PurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    @GetMapping("/{id}/details")
    public String purchaseDetails(@PathVariable Long id, Model model) {

        PurchaseOrder order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found."));

        model.addAttribute("order", order);

        return "purchases/purchase_details";
    }
}
