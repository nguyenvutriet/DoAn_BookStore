package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @Column(name = "cartId", length = 10)
    private String cartId;

    @Column(name = "quantity")
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private int quantity;

    @Column(name = "totalAmount", precision = 12, scale = 2)
    @NotNull
    @Min(0)
    private BigDecimal  totalAmount;


    //customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "customerId",
            nullable = false,
            referencedColumnName = "customerId",
            foreignKey = @ForeignKey(name = "FK_Cart_Customer")
    )
    private Customers customer;

    //cartdetail
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartDetail> cartDetails = new ArrayList<>();


    public Cart() {
    }

    public Cart(String cartId, int quantity, BigDecimal totalAmount, Customers customer) {
        this.cartId = cartId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.customer = customer;
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

    public Customers getCustomer() {
        return customer;
    }
    public void setCustomer(Customers customer) {
        this.customer = customer;
    }
//    public List<CartDetail> getCartDetails() {
//        return cartDetails;
//    }
//    public void setCartDetails(List<CartDetail> cartDetails) {
//        this.cartDetails = cartDetails;
//    }

}
