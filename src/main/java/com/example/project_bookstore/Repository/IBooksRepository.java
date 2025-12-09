package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBooksRepository extends JpaRepository<Books, String> {
    long count();

    // Lấy tất cả sách theo categoryId
    List<Books> findByCategory_CategoryId(String categoryId);

    // ========= NEW BOOKS =========
    // Không filter category
    List<Books> findTop8ByOrderByPublicationYearDesc();

    // Có filter category
    List<Books> findTop8ByCategory_CategoryIdOrderByPublicationYearDesc(String categoryId);

    // ========= BEST-SELLING =========
    // Không filter category
    List<Books> findTop8ByOrderByQuantityDesc();

    // Có filter category
    List<Books> findTop8ByCategory_CategoryIdOrderByQuantityDesc(String categoryId);

    // ========= MOST-FAVORED =========
    // Không filter category
    List<Books> findTop8ByOrderByPriceDesc();

    // Có filter category
    List<Books> findTop8ByCategory_CategoryIdOrderByPriceDesc(String categoryId);
}
