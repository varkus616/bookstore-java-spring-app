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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        model.addAttribute("user", user);
        model.addAttribute("purchaseHistory", purchaseHistory);
        model.addAttribute("purchaseOrders", purchaseOrders);
        model.addAttribute("purchaseHistory_count", purchaseOrders.stream().count());
        return "profile/my_profile_view";
    }

    @PostMapping("/changePassword")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "New password and confirm password do not match.");
            return "redirect:/my_profile";
        }

        try {
            userService.changePassword(username, currentPassword, newPassword);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/my_profile";
        }

        model.addAttribute("success", "Password changed successfully.");
        return "redirect:/my_profile";
    }

    @PostMapping("/deleteAccount")
    public String deleteAccount(@RequestParam("passwordConfirmation") String passwordConfirmation,
                                Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            userService.deleteUser(username, passwordConfirmation);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/my_profile";
        }

        return "redirect:/logout";
    }

}
