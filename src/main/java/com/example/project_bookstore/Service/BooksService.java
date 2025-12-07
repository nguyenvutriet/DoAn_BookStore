package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.IBooksRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BooksService {

    @Autowired
    private IBooksRepository repo;
}
