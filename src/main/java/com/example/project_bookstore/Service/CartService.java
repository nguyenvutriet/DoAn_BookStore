package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Repository.ICartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    @Autowired
    private ICartRepository repo;

    public Cart getCartByCustomer(String customerId) {
        return repo.findByCustomer_CustomerId(customerId);
    }
}
