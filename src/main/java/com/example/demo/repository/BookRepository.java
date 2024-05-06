package com.example.demo.repository;

import java.util.List;
import java.util.Optional;

import com.example.demo.model.Book;
import com.example.demo.model.BookType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    Optional<Book> findBookByTitle(String title);
    Optional<Book> findBookByAuthor(String author);

    List<Book> findByType(BookType type);
}
