package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;


@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @Column(name = "cartId")
    private String cartId;

    @Column(name = "quantity")
    @NotNull
    @Min(0)
    private int quantity;

    @Column(name = "totalAmount", precision = 12, scale = 2)
    @NotNull
    @Min(0)
    private BigDecimal  totalAmount;

    
    private Customers customers;

    public Cart() {
    }

    public Cart(String cartId, int quantity, BigDecimal totalAmount, Customers customers) {
        this.cartId = cartId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.customers = customers;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }
}
