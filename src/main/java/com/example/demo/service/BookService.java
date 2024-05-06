package com.example.demo.service;

import com.example.demo.model.Book;
import com.example.demo.model.BookType;
import com.example.demo.repository.BookRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }


    public List<Book> getBooksByType(BookType type) {
        return bookRepository.findByType(type);
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    public Book getBookByName(String name) {
        return bookRepository.findBookByTitle(name)
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));
    }

    public List<Book> getBooksByPrice(double price){
        return getAllBooks()
                .stream()
                .filter(book -> book.getPrice() >= price)
                .collect(Collectors.toList());
    }

    public List<Book> getBooksByAuthor(String author){
        return getAllBooks()
                .stream()
                .filter(book -> book.getAuthor().equals(author))
                .collect(Collectors.toList());
    }

    public void updateBook(Long bookId, Book updatedBook) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setGenre(updatedBook.getGenre());
            book.setDescription(updatedBook.getDescription());
            book.setCoverUrl(updatedBook.getCoverUrl());
            book.setPrice(updatedBook.getPrice());
            book.setStockQuantity(updatedBook.getStockQuantity());
            book.setReviews(updatedBook.getReviews());
            bookRepository.save(book);
        } else {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
    }

    public void deleteBook(Long bookId) {
        Optional<Book> optionalBook = bookRepository.findById(bookId);
        if (optionalBook.isPresent()) {
            bookRepository.deleteById(bookId);
        } else {
            throw new RuntimeException("Book not found with id: " + bookId);
        }
    }

    public void createBook(Book book) {
        Optional<Book> existingBook = bookRepository.findBookByTitle(book.getTitle());

        if (existingBook.isPresent()) {
            throw new RuntimeException("Book with title " + book.getTitle() + " already exists");
        } else {
            bookRepository.save(book);
        }
    }

}
