package com.example.project_bookstore.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "paymentId", length = 10)
    private String paymentId;

    @Column(name = "paymentTime")
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;

    @Column(name = "paymentMethod", length = 50)
    private String paymentMethod;

    @Column(name = "amount", precision = 12, scale = 0)
    private BigDecimal amount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "orderId",
            nullable = false,
            referencedColumnName = "orderId",
            foreignKey = @ForeignKey(name = "FK_Payment_Order")
    )
    private Orders order;

    public Payment() {}

    public Payment(String paymentId, Date paymentTime, Orders order) {
        this.paymentId = paymentId;
        this.paymentTime = paymentTime;
        this.order = order;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public Date getPaymentTime() { return paymentTime; }
    public void setPaymentTime(Date paymentTime) { this.paymentTime = paymentTime; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Orders getOrder() { return order; }
    public void setOrder(Orders order) { this.order = order; }
}