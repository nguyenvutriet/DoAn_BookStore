package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomersRepository extends JpaRepository<Customers, String> {

    long count();


    @Query("SELECT c FROM Customers c WHERE c.email = :email")
    public Customers findByEmail(@Param("email") String email);


}
