package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IPaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByOrder_OrderId(String orderId);
}