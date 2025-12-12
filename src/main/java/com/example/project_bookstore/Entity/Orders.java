package com.example.project_bookstore.Entity;

import jakarta.persistence.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Column(name = "totalAmount", precision = 12, scale = 2)
    @NotNull
    @DecimalMin(value = "0.00", inclusive = true)
    private BigDecimal totalAmount;

    @Column(name = "address", length = 500)
    @NotNull
    private String address;

    @Column(name = "status", length = 50)
    @NotNull
    private String status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetail_Order = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", referencedColumnName = "customerId", foreignKey = @ForeignKey(name = "FK_Order_Customer"))
    @NotNull
    private Customers customer;

    public Orders() {}

    public Orders(String orderId, String paymentMethod, Date orderDate, BigDecimal totalAmount, String address, String status) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
    }

    public Orders(String orderId, String paymentMethod, Date orderDate, BigDecimal totalAmount, String address, String status, List<OrderDetail> orderDetail_Order) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.address = address;
        this.status = status;
        this.orderDetail_Order = orderDetail_Order;
    }

    public List<OrderDetail> getOrderDetail_Order() {
        return orderDetail_Order;
    }

    public void setOrderDetail_Order(List<OrderDetail> orderDetail_Order) {
        this.orderDetail_Order = orderDetail_Order;
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

    public Customers getCustomer() {
        return customer;
    }

    public void setCustomer(Customers customer) {
        this.customer = customer;
    }

}
