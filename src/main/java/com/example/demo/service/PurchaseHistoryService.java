package com.example.demo.service;

import com.example.demo.model.PurchaseHistory;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.PurchaseHistoryRepository;
import com.example.demo.security.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PurchaseHistoryService {

    private final PurchaseHistoryRepository purchaseHistoryRepository;

    @Autowired
    public PurchaseHistoryService(PurchaseHistoryRepository purchaseHistoryRepository) {
        this.purchaseHistoryRepository = purchaseHistoryRepository;
    }

    public PurchaseHistory save(PurchaseHistory purchaseHistory) {
        return purchaseHistoryRepository.save(purchaseHistory);
    }

    public PurchaseHistory findById(Long id) {
        return purchaseHistoryRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        purchaseHistoryRepository.deleteById(id);
    }

    public PurchaseHistory findByUser(User user) {
        return purchaseHistoryRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Can't find user"));
    }

    public List<PurchaseHistory> findAll() {
        return purchaseHistoryRepository.findAll();
    }

    public void addOrderToHistory(Long purchaseHistoryId, PurchaseOrder purchaseOrder) {
        PurchaseHistory purchaseHistory = findById(purchaseHistoryId);
        if (purchaseHistory != null) {
            purchaseHistory.getPurchaseOrders().add(purchaseOrder);
            purchaseHistory.calculateTotalAmount();
            save(purchaseHistory);
        }
    }
}
