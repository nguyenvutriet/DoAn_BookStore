package com.example.project_bookstore.Entity;

import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class OrderdetailId implements Serializable {


    private String orderId;

    private String bookId;




}
