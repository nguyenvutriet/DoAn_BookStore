package com.example.project_bookstore.Service;

import com.example.project_bookstore.Repository.ICustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomersService {

    @Autowired
    private ICustomersRepository repo;
}
