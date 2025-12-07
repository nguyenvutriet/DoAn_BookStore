package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "orders")
public class Orders {


    @Id
    @Column(name = "orderId", length = 10)
    private String orderId;

    @Column(name = "paymentMethod", length = 50)
    @NotNull
    private String paymentMethod;

    @Column(name = "orderDate")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate;

    @Column(name = "totalAmount")
    @NotNull
    private BigDecimal totalAmount;

    @Column(name = "address", length = 100)
    @NotNull
    private String address;

    @Column(name = "status", length = 50)
    @NotNull
    private String status;

    public Orders() {}

    public Orders(String orderId, String paymentMethod, Date orderDate, BigDecimal totalAmount, String address, String status) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
    }
    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    public String getPaymentMethod() {
        return paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    public Date getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

}
