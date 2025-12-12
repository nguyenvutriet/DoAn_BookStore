package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Orders;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
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
    @Query(value = "SELECT orderId FROM Orders ORDER BY orderId DESC LIMIT 1", nativeQuery = true)
    String findLastOrderId();

    List<Orders> findByCustomer_CustomerId(String customerId);

    List<Orders> findByStatus(String status);


    // Tổng doanh thu
    @Query("""
           SELECT COALESCE(SUM(o.totalAmount), 0)
           FROM Orders o
           WHERE o.status = 'Delivered'
           """)
    Double getTotalRevenue();

    // Tổng đơn đã giao
    @Query("""
           SELECT COUNT(o)
           FROM Orders o
           WHERE o.status = 'Delivered'
           """)
    Long getTotalDeliveredOrders();

    // Doanh thu theo tháng (từ ngày bất kỳ)
    @Query("""
           SELECT FUNCTION('MONTH', o.orderDate),
                  FUNCTION('YEAR', o.orderDate),
                  SUM(o.totalAmount)
           FROM Orders o
           WHERE o.status = 'Delivered'
             AND o.orderDate >= :fromDate
           GROUP BY FUNCTION('MONTH', o.orderDate), FUNCTION('YEAR', o.orderDate)
           ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)
           """)
    List<Object[]> getMonthlyRevenue(Date fromDate);

    @Query("""
       SELECT COALESCE(SUM(o.totalAmount), 0)
       FROM Orders o
       WHERE o.status = 'Delivered'
         AND FUNCTION('MONTH', o.orderDate) = :month
         AND FUNCTION('YEAR', o.orderDate) = :year
       """)
    Double getRevenueOfMonth(@Param("month") int month,
                             @Param("year") int year);

    @Query(
            value = """
           SELECT COUNT(DISTINCT MONTH(o.orderDate))
           FROM orders o
           WHERE o.status = 'Delivered'
           """,
            nativeQuery = true
    )
    long countRevenueMonths();

}

