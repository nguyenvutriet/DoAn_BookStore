package com.example.project_bookstore.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "books")
public class Books {

    @Id
    @Column(name = "bookId", length = 10)
    private String bookId;

    @Column(name = "title", length = 100)
    @NotNull
    private String title;

    @Column(name = "author", length = 50)
    @NotNull
    private String author;

    @Column(name = "publisher", length = 50)
    @NotNull
    private String publisher;

    @Column(name = "publicationYear")
    @Min(0)
    @NotNull
    private int publicationYear;

    @Column(name = "description")
    private String description;

    @Column(name = "price", precision = 12, scale = 2)
    @NotNull
    @Min(0)
    private BigDecimal price;

    @Column(name = "quantity")
    @NotNull
    @Min(0)
    private int quantity;

    @Column(name = "picture")
    @NotNull
    private String picture;

    @ManyToOne
    @JoinColumn(name = "categoryId")
    private Category category;

    public Books() {
    }

    public Books(String bookId, String title, String author, String publisher, int publicationYear, String description, BigDecimal price, int quantity, String picture, Category category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.publicationYear = publicationYear;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.picture = picture;
        this.category = category;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(int publicationYear) {
        this.publicationYear = publicationYear;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
