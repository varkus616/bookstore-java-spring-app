package com.example.demo.security.controller;

import com.example.demo.model.Book;
import com.example.demo.model.PurchaseOrder;
import com.example.demo.repository.OrderRepository;
import com.example.demo.security.model.Privilege;
import com.example.demo.security.model.Role;
import com.example.demo.security.model.User;
import com.example.demo.security.service.PrivilegeService;
import com.example.demo.security.service.SecurityUserDetailsService;
import com.example.demo.service.BookService;
import com.example.demo.service.OrderService;
import com.example.demo.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdministrationController {

    @Autowired
    private SecurityUserDetailsService userDetailsService;

    @Autowired
    private BookService bookService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;


    @GetMapping("/")
    public String adminHome() {
        return "admin/home";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        List<User> users = userDetailsService.getAllUsers();

        // Znajdź użytkownika o nazwie "admin" i usuń go z listy
        User adminUser = null;
        for (User user : users) {
            if (user.getUsername().equals("admin")) {
                adminUser = user;
                break;
            }
        }
        if (adminUser != null) {
            users.remove(adminUser);
        }

        model.addAttribute("users", users);

        User user = new User();
        model.addAttribute("user", user);

        return "admin/users";
    }

    @GetMapping("/users/edit/{userId}")
    public String editUserForm(@PathVariable String userId, Model model) {
        User user = userDetailsService.getUserById(userId);
        model.addAttribute("user", user);
        return "admin/editUser";
    }

    @PostMapping("/users/edit/{userId}")
    public String editUser(@PathVariable String userId, @ModelAttribute User updatedUser) {
        userDetailsService.updateUser(userId, updatedUser);
        return "redirect:/admin/users";
    }

    @GetMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable String userId) {
        userDetailsService.deleteUser(userId);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/add/{userId}")
    public String addUser(@ModelAttribute User user) {
        userDetailsService.createUser(user);
        return "redirect:/admin/users";
    }

    @GetMapping("/books")
    public String manageBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);

        Book book = new Book();
        model.addAttribute("book", book);

        return "admin/books";
    }

    @GetMapping("/books/edit/{bookId}")
    public String editBookForm(@PathVariable Long bookId, Model model) {
        Book book = bookService.getBookById(bookId);
        model.addAttribute("book", book);
        return "admin/editBook";
    }

    @PostMapping("/books/edit/{bookId}")
    public String editBook(@PathVariable Long bookId, @ModelAttribute Book updatedBook) {
        bookService.updateBook(bookId, updatedBook);
        return "redirect:/admin/books";
    }

    @GetMapping("/books/delete/{bookId}")
    public String deleteBook(@PathVariable Long bookId) {
        bookService.deleteBook(bookId);
        return "redirect:/admin/books";
    }

    @PostMapping("/books/add/{bookId}")
    public String addBook(@ModelAttribute Book book){
        bookService.createBook(book);

        return "redirect:/admin/books";
    }

    @GetMapping("/orders")
    public String manageOrders(Model model) {
        List<PurchaseOrder> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);

        PurchaseOrder order = new PurchaseOrder();
        model.addAttribute("order", order);
        return "admin/orders";
    }

    @GetMapping("/orders/edit/{orderId}")
    public String editOrderForm(@PathVariable Long orderId, Model model) {
        PurchaseOrder order = orderService.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        model.addAttribute("order", order);
        return "admin/editOrder";
    }

    @PostMapping("/orders/edit/{orderId}")
    public String editOrder(@PathVariable Long orderId, @ModelAttribute PurchaseOrder updatedOrder) {
        orderService.updateOrder(orderId, updatedOrder);
        return "redirect:/admin/orders";
    }

    @GetMapping("/orders/delete/{orderId}")
    public String deleteOrder(@PathVariable Long orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/admin/orders";
    }

    @PostMapping("/orders/add/{orderId}")
    public String addOrder(@ModelAttribute PurchaseOrder order) {
        orderService.createOrder(order);
        return "redirect:/admin/orders";
    }

    @GetMapping("/roles")
    public String manageRoles(Model model) {
        List<Role> roles = roleService.getAllRoles();
        Role role = new Role();

        model.addAttribute("roles", roles);
        model.addAttribute("role", role);

        return "admin/roles";
    }

    @PostMapping("/roles/edit/{roleId}/{privilegeId}")
    public String addPrivilege(@ModelAttribute("privilege") Privilege privilege) {
        privilegeService.addPrivilege(privilege);
        return "redirect:/admin/roles";
    }

    @PostMapping("/roles/add/{roleId}")
    public String addRole(@ModelAttribute Role role) {
        roleService.addRole(role);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/edit/{roleId}")
    public String editRoleForm(@PathVariable Long roleId, Model model) {
        Role role = roleService.getRoleById(roleId);
        List<Privilege> privileges = privilegeService.getAllPrivileges();
        Privilege privilege = new Privilege();

        model.addAttribute("role", role);
        model.addAttribute("privileges", privileges);
        model.addAttribute("privilege", privilege);

        return "admin/editRole";
    }

    @PostMapping("/roles/edit/{roleId}")
    public String editRole(@PathVariable Long roleId, @ModelAttribute Role updatedRole) {
        roleService.updateRole(roleId, updatedRole);
        return "redirect:/admin/roles";
    }

    @GetMapping("/roles/delete/{roleId}")
    public String deleteRole(@PathVariable Long roleId) {
        roleService.deleteRole(roleId);
        return "redirect:/admin/roles";
    }

}