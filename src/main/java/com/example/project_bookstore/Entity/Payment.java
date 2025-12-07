package com.example.project_bookstore.Entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @Column(name = "paymentId", length = 10)
    private String paymentId;

    @Column(name = "paymentTime")
    @Notnull
    @Temporal(TemporalType.TIMESTAMP)
    private Date paymentTime;

    public Payment() {}

    public Payment(String paymentId, Date paymentTime) {
        this.paymentId = paymentId;
        this.paymentTime = paymentTime;
    }

    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public Date getPaymentTime() {
        return paymentTime;
    }
    public void setPaymentTime(Date paymentTime) {
        this.paymentTime = paymentTime;
    }

}
