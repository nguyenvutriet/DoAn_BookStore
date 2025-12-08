package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Customers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomersRepository extends JpaRepository<Customers, String> {
    long count();
}
