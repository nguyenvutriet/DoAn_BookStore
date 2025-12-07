package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "customers")
public class Customers {

    @Id
    @Column(name = "customerId", length = 10)
    private String customerId;

    @Column(name = "fullName", length = 50)
    @NotNull
    private String fullName;

    @Column(name = "email", length = 50)
    private String email;

    @Column(name = "phone", length = 10)
    @NotNull
    private String phone;

    @Column(name = "address")
    @NotNull
    private String address;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

    public Customers() {
    }

    public Customers(String customerId, String fullName, String email, String phone, String address, Date dateOfBirth) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
}
