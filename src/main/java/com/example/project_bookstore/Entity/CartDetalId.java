package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CartDetalId implements Serializable {

    @Column(name = "cartId", length = 10)
    private String cartId;

    @Column(name = "bookId", length = 10)
    private String bookId;

    public CartDetalId() {
    }

    public CartDetalId(String cartId, String bookId) {
        this.cartId = cartId;
        this.bookId = bookId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
