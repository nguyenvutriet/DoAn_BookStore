package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.ICategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    @Autowired
    private ICategoryRepository repo;
}
