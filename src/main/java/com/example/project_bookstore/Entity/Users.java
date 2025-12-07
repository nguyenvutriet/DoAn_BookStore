package com.example.project_bookstore.Entity;


import jakarta.persistence.*;

import javax.management.relation.Role;
import java.util.Date;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "userName", length = 100)
    private String userName;

    @Column( length = 255)
    @Notnull
    private String password;

    @Column( length = 20)
    @Notnull
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "fullName", length = 50)
    @Notnull
    private String fullName;

    @Column(name = "registrationDate")
    private Date registrationDate;

    public Users() {}

    public Users(String userName, String password, Role role, String fullName, Date registrationDate) {
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
    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
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