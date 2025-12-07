package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.OrderdetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, OrderdetailId> {
}
