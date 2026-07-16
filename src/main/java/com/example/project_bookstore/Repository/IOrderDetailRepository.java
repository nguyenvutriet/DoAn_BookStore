package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Books;
import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.OrderdetailId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IOrderDetailRepository
        extends JpaRepository<OrderDetail, OrderdetailId> {

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

    @Query(value = """
        SELECT b.*
        FROM orderdetail od1
        JOIN orderdetail od2
             ON od1.orderId = od2.orderId
        JOIN books b
             ON b.bookId = od2.bookId
        WHERE od1.bookId = :bookId
          AND od2.bookId <> :bookId
        GROUP BY b.bookId
        ORDER BY COUNT(*) DESC
        LIMIT 4
        """,
            nativeQuery = true)
    List<Books> recommendBooks(
            @Param("bookId") String bookId
    );
}
