package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Repository.ICartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private ICartRepository repo;

    public Cart getCartByCustomer(String customerId) {
        return repo.findByCustomer_CustomerId(customerId);
    }

    public void createCart(Customers cus){
        List<Cart> dsCart = repo.findAll();
        List<Integer> dsId = new ArrayList<>();
        for(Cart cart : dsCart){
            int id = Integer.parseInt(cart.getCartId().substring(2));
            dsId.add(id);
        }
        int idMax = Collections.max(dsId);
        idMax = idMax+1;
        String id = "CA"+idMax;
        Cart cart = new Cart(id, 0, BigDecimal.ZERO, cus);
        repo.save(cart);
    }
}
