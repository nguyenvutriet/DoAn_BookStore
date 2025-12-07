package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.IOrderDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailService {

    @Autowired
    private IOrderDetailRepository repo;
}
