package com.example.project_bookstore.Entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "userName", length = 100)
    private String userName;

    @Column( length = 255)
    @NotNull
    private String password;

    @Column( length = 20)
    @NotNull
    private String role;

    @Column(name = "fullName", length = 50)
    @NotNull
    private String fullName;

    @Column(name = "registrationDate")
    @Temporal(TemporalType.DATE)
    private Date registrationDate;

    public Users() {}

    public Users(String userName, String password, String role, String fullName, Date registrationDate) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.fullName = fullName;
        this.registrationDate = registrationDate;
    }

    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    public Date getRegistrationDate() {
        return registrationDate;
    }
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}