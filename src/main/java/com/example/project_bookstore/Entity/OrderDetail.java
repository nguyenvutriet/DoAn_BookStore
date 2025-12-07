package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
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

    public OrderDetail() {
    }

    public OrderDetail(OrderdetailId orderDetailId, int quantity, BigDecimal unitPrice) {
        this.orderDetailId = orderDetailId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
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
