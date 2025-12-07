package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Books;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IBooksRepository extends JpaRepository<Books, String> {
}
