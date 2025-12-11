package com.example.project_bookstore.Entity;

import java.math.BigDecimal;

public class CartSelectedItem {

    private String cartId;
    private String bookId;
    private int quantity;
    private BigDecimal unitPrice;   // <-- QUAN TRỌNG!
    private String bookName;    // nếu cần hiển thị
    private String image;       // tùy bạn

    // GETTER - SETTER

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
