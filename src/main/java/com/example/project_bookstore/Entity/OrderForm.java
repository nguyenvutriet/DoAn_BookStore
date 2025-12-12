package com.example.project_bookstore.Entity;


import java.math.BigDecimal;
import java.util.List;

public class OrderForm {

    private String fullname;
    private String address;
    private String paymentMethod;
    private BigDecimal totalAmount;   // ⭐ THÊM FIELD NÀY


    private List<CartSelectedItem> items;

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public BigDecimal getTotalAmount() { return totalAmount; }    // ⭐ GETTER
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; } // ⭐ SETTER


    public List<CartSelectedItem> getItems() { return items; }
    public void setItems(List<CartSelectedItem> items) { this.items = items; }
}
