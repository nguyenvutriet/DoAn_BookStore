package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ICustomersRepository extends JpaRepository<Customers, String> {

    @Query(value = """
        SELECT *
        FROM Customers
        ORDER BY CAST(SUBSTRING(customerId, 2) AS UNSIGNED) DESC
        LIMIT 1
    """, nativeQuery = true)
    public Customers getLastCustomer();

    @Query("SELECT c FROM Customers c WHERE c.email = :email")
    public Customers findByEmail(@Param("email") String email);


}
