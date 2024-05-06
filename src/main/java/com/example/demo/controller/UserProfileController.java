package com.example.demo.controller;

import com.example.demo.model.PurchaseHistory;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.security.model.User;
import com.example.demo.security.service.SecurityUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class UserProfileController {

    @Autowired
    private SecurityUserDetailsService userService;

    @GetMapping("/my_profile")
    public String myProfile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) userService.loadUserByUsername(auth.getName());
        PurchaseHistory purchaseHistory = user.getPurchaseHistory();

        List<PurchaseOrder> purchaseOrders = purchaseHistory.getPurchaseOrders();
        //purchaseOrders.forEach(order -> order.getOrderItems().stream().map(item -> item.getQuantity()).reduce(0, (a, b) -> {
        //    return a + b;
        //}));
        model.addAttribute("user", user);
        model.addAttribute("purchaseHistory", purchaseHistory);
        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("purchaseHistory_count", purchaseOrders.stream().count());
        return "profile/my_profile_view";
    }
}
