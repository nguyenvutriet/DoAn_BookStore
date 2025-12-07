package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.ICartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private ICartRepository repo;
}
