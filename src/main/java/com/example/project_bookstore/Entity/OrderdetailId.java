package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class OrderdetailId implements Serializable {

    @Column(name = "orderId", length = 10)
    private String orderId;

    @Column(name = "bookId", length =  10)
    private String bookId;

    public OrderdetailId() {
    }

    public OrderdetailId(String orderId, String bookId) {
        this.orderId = orderId;
        this.bookId = bookId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
