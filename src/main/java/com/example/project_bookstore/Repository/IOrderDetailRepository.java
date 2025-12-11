package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.OrderdetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, OrderdetailId> {

    List<OrderDetail> findByOrder_OrderId(String orderId);
    List<OrderDetail> findByOrderDetailId_OrderId(String orderId);

}
