package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<Category, String> {
    @Query("""
            SELECT COUNT(b)
            FROM Books b
            WHERE b.category.categoryId = :categoryId
           """)
    Long countBooksByCategory(@Param("categoryId") String categoryId);

    @Query("""
            SELECT COALESCE(SUM(od.quantity), 0)
            FROM OrderDetail od
            WHERE od.book.category.categoryId = :categoryId
           """)
    Long countSoldBooksByCategory(@Param("categoryId") String categoryId);

}
