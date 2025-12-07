package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "review")
public class Review {

    @Id
    @Column(name = "reviewId", length = 10)
    private String reviewId;

    @Column(name = "rating")
    @NotNull
    @Min(0)
    @Max(5)
    private int rating;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "creationDate", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date creationDate;

    // customers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customerId",
            nullable = false,
            referencedColumnName = "customerId",
            foreignKey = @ForeignKey(name = "FK_Review_Customer")
    )
    private Customers customer;

    // books
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "bookId",
            nullable = false,
            referencedColumnName = "bookId",
            foreignKey = @ForeignKey(name = "FK_Review_Book")
    )
    private Books book;

    public Review() {}

    public Review(String reviewId, int rating, String comment, Date creationDate, Customers customer, Books book) {
        this.reviewId = reviewId;
        this.rating = rating;
        this.comment = comment;
        this.creationDate = creationDate;
        this.customer = customer;
        this.book = book;
    }

    public String getReviewId() {
        return reviewId;
    }
    public void setReviewId(String reviewId) {
        this.reviewId = reviewId;
    }
    public int getRating() {
        return rating;
    }
    public void setRating(int rating) {
        this.rating = rating;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }
    public Date getCreationDate() {
        return creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    //
    public Customers getCustomer() {
        return customer;
    }
    public void setCustomer(Customers customer) {
        this.customer = customer;
    }
    public Books getBook() {
        return book;
    }
    public void setBook(Books book) {
        this.book = book;
    }

}