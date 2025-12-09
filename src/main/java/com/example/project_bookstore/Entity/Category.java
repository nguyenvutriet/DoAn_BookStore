package com.example.project_bookstore.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "categoryId", length = 10)
    private String categoryId;

    @Column(name = "categoryName", length = 50)
    private String categoryName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Books>  books =  new ArrayList<Books>();

    public Category() {
    }

    public Category(String catagoryId, String categoryName) {
        this.categoryId = catagoryId;
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String catagoryId) {
        this.categoryId = catagoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
