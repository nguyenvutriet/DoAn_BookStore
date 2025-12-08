package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CartDetailId implements Serializable {

    @Column(name = "cartId", length = 10)
    private String cartId;

    @Column(name = "bookId", length = 10)
    private String bookId;

    public CartDetailId() {
    }

    public CartDetailId(String cartId, String bookId) {
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CartDetailId)) return false;

        CartDetailId other = (CartDetailId) obj;

        return cartId.equals(other.cartId) &&
                bookId.equals(other.bookId);
    }

    @Override
    public int hashCode() {
        int result = cartId.hashCode();
        result = 31 * result + bookId.hashCode();
        return result;
    }
}
