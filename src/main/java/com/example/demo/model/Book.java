package com.example.demo.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.example.demo.model.BookType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


@Entity
@Table(name = "books")
@Getter
@Setter
public class Book
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;
    private String genre;
    private String description;
    private String coverUrl;
    private double price;
    private int stockQuantity;

    @Enumerated(EnumType.STRING)
    private BookType type;


    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Collection<Review> reviews = new ArrayList<Review>();

    public Book()
    {
        if (BookType.values().length > 0) {
            Random random = new Random();
            int typeIndex = random.nextInt(BookType.values().length);
            this.type = BookType.values()[typeIndex];
        } else {
            this.type = BookType.REGULAR;
        }
    }

    public Book(String title, String author, String genre, String description, double price, int stockQuantity,
                String coverUrl) {
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.coverUrl = coverUrl;
        if (BookType.values().length > 0) {
            Random random = new Random();
            int typeIndex = random.nextInt(BookType.values().length);
            this.type = BookType.values()[typeIndex];
        } else {
            this.type = BookType.REGULAR;
        }
    }

    @Override
    public String toString() {
        return "Book:'"+this.title+"'";
    }

}


