package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Entity
@Table(name = "orderdetail")
public class OrderDetail {

    @EmbeddedId
    private OrderdetailId orderDetailId;

    @Column(name = "quantity")
    @NotNull
    @Min(0)
    private int quantity;

    @Column(name = "unitPrice")
    @NotNull
    @Min(0)
    private BigDecimal unitPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orderId", referencedColumnName = "orderId", foreignKey = @ForeignKey(name = "FK_OrderDetail_Order"))
    @NotNull
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bookId", referencedColumnName = "bookId", foreignKey = @ForeignKey(name = "FK_OrderDetail_Book"))
    private Books book;

    public OrderDetail() {
    }

    public OrderDetail(OrderdetailId orderDetailId, int quantity, BigDecimal unitPrice) {
        this.orderDetailId = orderDetailId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public OrderDetail(OrderdetailId orderDetailId, int quantity, BigDecimal unitPrice, Orders order, Books book) {
        this.orderDetailId = orderDetailId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.order = order;
        this.book = book;
    }

    public Orders getOrder() {
        return order;
    }

    public void setOrder(Orders order) {
        this.order = order;
    }

    public Books getBook() {
        return book;
    }

    public void setBook(Books book) {
        this.book = book;
    }

    public OrderdetailId getOrderDetailId() {
        return orderDetailId;
    }

    public void setOrderDetailId(OrderdetailId orderDetailId) {
        this.orderDetailId = orderDetailId;
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
