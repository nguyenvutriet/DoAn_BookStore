package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.ICartDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartDetailService {

    @Autowired
    private ICartDetailRepository repo;
}
