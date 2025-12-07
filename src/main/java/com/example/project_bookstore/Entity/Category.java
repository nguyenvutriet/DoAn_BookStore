package com.example.project_bookstore.Entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "categoryId", length = 10)
    private String catagoryId;

    @Column(name = "categoryName", length = 50)
    private String categoryName;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Books>  books =  new ArrayList<Books>();

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
