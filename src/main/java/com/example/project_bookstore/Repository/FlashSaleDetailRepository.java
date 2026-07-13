package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.FlashSaleDetail;
import com.example.project_bookstore.Entity.FlashSaleDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface FlashSaleDetailRepository extends JpaRepository<FlashSaleDetail, FlashSaleDetailId> {

    @Query("SELECT d FROM FlashSaleDetail d JOIN FETCH d.book JOIN FETCH d.flashSale WHERE d.flashSale.flashSaleId = :flashSaleId")
    List<FlashSaleDetail> findByFlashSaleIdWithBook(@Param("flashSaleId") String flashSaleId);

    Optional<FlashSaleDetail> findByFlashSale_FlashSaleIdAndBook_BookId(String flashSaleId, String bookId);

    @Query("SELECT d FROM FlashSaleDetail d WHERE d.flashSale.status = 'ACTIVE' " +
            "AND :now BETWEEN d.flashSale.startTime AND d.flashSale.endTime " +
            "AND d.book.bookId = :bookId " +
            "ORDER BY d.flashSale.startTime DESC")
    List<FlashSaleDetail> findActiveByBookIdList(@Param("bookId") String bookId, @Param("now") LocalDateTime now);

    @Query("SELECT d FROM FlashSaleDetail d WHERE d.flashSale.status = 'ACTIVE' " +
           "AND :now BETWEEN d.flashSale.startTime AND d.flashSale.endTime " +
           "AND d.book.bookId IN :bookIds")
    List<FlashSaleDetail> findActiveByBookIds(@Param("bookIds") java.util.List<String> bookIds, @Param("now") LocalDateTime now);

    @Query("SELECT d.flashSale.flashSaleId, COUNT(d) FROM FlashSaleDetail d GROUP BY d.flashSale.flashSaleId")
    List<Object[]> countDetailsGroupByFlashSale();

    List<FlashSaleDetail> findByBook_BookId(String bookId);

    @Query("SELECT d FROM FlashSaleDetail d WHERE d.flashSale.flashSaleId = :flashSaleId AND d.book.bookId IN :bookIds")
    List<FlashSaleDetail> findByFlashSaleIdAndBookIds(@Param("flashSaleId") String flashSaleId,
                                                       @Param("bookIds") List<String> bookIds);
}