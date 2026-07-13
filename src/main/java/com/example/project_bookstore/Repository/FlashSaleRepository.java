package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.FlashSale;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlashSaleRepository extends JpaRepository<FlashSale, String> {

    @Query("SELECT f FROM FlashSale f WHERE f.status = 'ACTIVE' " +
            "AND :now BETWEEN f.startTime AND f.endTime " +
            "ORDER BY f.startTime DESC")
    List<FlashSale> findCurrentActiveList(@Param("now") LocalDateTime now);
}