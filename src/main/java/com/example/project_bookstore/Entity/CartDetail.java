package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "cartdetail")
public class CartDetail {

    @EmbeddedId
    private CartDetalId cartDetailId;

    @Column(name = "quantity")
    @NotNull
    @Min(0)
    private int quantity;

    @Column(name = "unitPrice")
    @NotNull
    @Min(0)
    private BigDecimal unitPrice;

    public CartDetail() {
    }

    public CartDetail(BigDecimal unitPrice, CartDetalId cartDetailId, int quantity) {
        this.unitPrice = unitPrice;
        this.cartDetailId = cartDetailId;
        this.quantity = quantity;
    }

    public CartDetail(int quantity, BigDecimal unitPrice) {
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public CartDetalId getCartDetailId() {
        return cartDetailId;
    }

    public void setCartDetailId(CartDetalId cartDetailId) {
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
}
