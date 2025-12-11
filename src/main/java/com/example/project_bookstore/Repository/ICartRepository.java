package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICartRepository extends JpaRepository<Cart, String> {
    Cart findByCustomer_CustomerId(String customerId);
    // Tìm cart theo Customers object (nếu bạn đang có object Customer)
    Cart findByCustomer(Customers customer);

}
