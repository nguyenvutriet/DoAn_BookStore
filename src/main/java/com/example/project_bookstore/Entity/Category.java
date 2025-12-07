package com.example.project_bookstore.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "categoryId", length = 10)
    private String catagoryId;

    @Column(name = "categoryName", length = 50)
    private String categoryName;

    public Category() {
    }

    public Category(String catagoryId, String categoryName) {
        this.catagoryId = catagoryId;
        this.categoryName = categoryName;
    }

    public String getCatagoryId() {
        return catagoryId;
    }

    public void setCatagoryId(String catagoryId) {
        this.catagoryId = catagoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
