package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;


import java.math.BigDecimal;

@Entity
@Table(name = "cartdetail")
public class CartDetail {

    @EmbeddedId
    private CartDetailId cartDetailId;

    @Column(name = "quantity")
    @NotNull
    @Min(1)
    private int quantity;

    @Column(name = "unitPrice")
    @NotNull
    @DecimalMin(value = "0.00", inclusive = false)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("cartId")
    @JoinColumn(name = "cartId", referencedColumnName = "cartId", columnDefinition = "VARCHAR(10)")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bookId")
    @JoinColumn(name = "bookId", referencedColumnName = "bookId", columnDefinition = "VARCHAR(10)")
    private Books book;

    public CartDetail() {
    }

    public CartDetail(BigDecimal unitPrice, CartDetailId cartDetailId, int quantity) {
        this.unitPrice = unitPrice;
        this.cartDetailId = cartDetailId;
        this.quantity = quantity;
    }

    public CartDetail(int quantity, BigDecimal unitPrice) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public CartDetailId getCartDetailId() {
        return cartDetailId;
    }

    public void setCartDetailId(CartDetailId cartDetailId) {
        this.cartDetailId = cartDetailId;
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

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }
}
