package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.OrderdetailId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, OrderdetailId> {

    List<OrderDetail> findByOrder_OrderId(String orderId);
    List<OrderDetail> findByOrderDetailId_OrderId(String orderId);

    @Query("""
            SELECT c.categoryName,
                   SUM(od.unitPrice * od.quantity)
            FROM OrderDetail od
            JOIN od.book b
            JOIN b.category c
            JOIN od.order o
            WHERE o.status = 'Delivered'
            GROUP BY c.categoryName
            """)
    List<Object[]> getRevenueByCategory();

    @Query("""
       SELECT od.book 
       FROM OrderDetail od
       JOIN od.order o
       WHERE o.status = 'Delivered'
       ORDER BY o.orderDate DESC
       """)
    List<Books> findRecentSoldBooks(Pageable pageable);

}
