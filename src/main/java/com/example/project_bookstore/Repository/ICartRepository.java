package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
    Cart findByCustomer_CustomerId(String customerId);
}
