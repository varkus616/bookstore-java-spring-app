package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.model.BookType;
import com.example.demo.model.Review;
import com.example.demo.security.model.User;
import com.example.demo.service.BookService;
import com.example.demo.service.ReviewService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private ReviewService reviewService;


    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/")
    public String showBooks(Model model) {
        List<Book> books = bookService.getAllBooks();
        model.addAttribute("books", books);
        return "books/books_view";
    }

    @GetMapping("/{id}")
    public String showBookDetails(@PathVariable Long id, Model model) {
        Book book = bookService.getBookById(id);
        model.addAttribute("book", book);
        return "books/book_details";
    }

    @PostMapping("/search")
    public String searchBookByName(@RequestParam("bookName") String bookName, Model model) {

        try {
            Book book = bookService.getBookByName(bookName);
            model.addAttribute("book", book);
        } catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }
        return "books/book_details";
    }

    @PostMapping("/filterByPrice")
    public String filterBooksByPrice(@RequestParam("price") int price, Model model) {

        try {
            List<Book> books = bookService.getBooksByPrice(price);
            model.addAttribute("books", books);
        }catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }

        return "books/books_view";
    }

    @PostMapping("/filterByAuthor")
    public String filterBooksByAuthor(@RequestParam("author") String author, Model model) {
        try {
            List<Book> books = bookService.getBooksByAuthor(author);
            model.addAttribute("books", books);
        }catch (EntityNotFoundException e){
            model.addAttribute("error", e.getMessage());
        }
        return "books/books_view";
    }


    @PostMapping("/reviews/add")
    public String addReview(
            @RequestParam("book") Book book,
            @RequestParam("reviewText") String reviewText,
            @RequestParam("rating") int rating,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String username = authentication.getName();

        User user = (User) userDetailsService.loadUserByUsername(username);

        Review review = new Review();
        review.setBook(book);
        review.setReviewText(reviewText);
        review.setRating(rating);
        review.setUser(user);

        reviewService.createReview(review);

        return "redirect:/books/?id=" + book.getId();
    }

    @GetMapping("/bestsellers")
    public String showBestsellers(Model model) {
        List<Book> bestsellers = bookService.getBooksByType(BookType.BESTSELLER);
        model.addAttribute("books", bestsellers);
        return "books/bestsellers_view";
    }

    @GetMapping("/new_arrivals")
    public String showNewArrivals(Model model) {
        List<Book> newArrivals = bookService.getBooksByType(BookType.NEW_ARRIVAL);
        model.addAttribute("books", newArrivals);
        return "books/new_arrivals_view";
    }

    @GetMapping("/special_offers")
    public String showSpecialOffers(Model model) {
        List<Book> specialOffers = bookService.getBooksByType(BookType.SPECIAL_OFFER);
        model.addAttribute("books", specialOffers);
        return "books/special_offers_view";
    }

}
