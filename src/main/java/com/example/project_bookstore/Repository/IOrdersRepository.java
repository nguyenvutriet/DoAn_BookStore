package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrdersRepository extends JpaRepository<Orders, String> {
    long count();

    @Query("SELECT o FROM Orders o WHERE o.customer.customerId=:id")
    public List<Orders> findByCustomerId(@Param("id") String customerId);

    @Modifying
    @Transactional
    @Query("UPDATE Orders o SET o.status = :sta WHERE o.orderId = :id")
    void updateStatus(@Param("id") String orderId, @Param("sta") String status);

}

