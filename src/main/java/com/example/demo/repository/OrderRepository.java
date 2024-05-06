package com.example.demo.repository;

import java.util.Optional;

import com.example.demo.model.PurchaseOrder;
import com.example.demo.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface OrderRepository extends JpaRepository<PurchaseOrder, Long>
{
    Optional<PurchaseOrder> findOrderByUser(User user);
}
