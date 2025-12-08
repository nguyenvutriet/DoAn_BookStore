package com.example.project_bookstore.Entity;

public class ViewCartItem {

    private String cartId;     // thêm thuộc tính cartId
    private String bookId;
    private String bookName;
    private String image;
    private double unitPrice;
    private int quantity;

    public ViewCartItem() {}

    public ViewCartItem(String cartId, String bookId, String bookName, String image,
                        double unitPrice, int quantity) {
        this.cartId = cartId;
        this.bookId = bookId;
        this.bookName = bookName;
        this.image = image;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }

    // Getters & Setters
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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
