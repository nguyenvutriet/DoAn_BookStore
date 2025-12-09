package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Customers;
import com.example.project_bookstore.Entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface ICustomersRepository extends JpaRepository<Customers, String> {

    long count();

    @Transactional
    @Modifying
    @Query("UPDATE Customers SET fullName=:name, phone=:pho, email=:em, address=:add, dateOfBirth=:birthDay WHERE customerId=:id")
    public int updateCus(@Param("id") String customerId,@Param("name") String name,@Param("pho") String phone,@Param("em") String email,@Param("add") String address, Date birthDay);

    @Query("SELECT c FROM Customers c WHERE c.email = :email")
    public Customers findByEmail(@Param("email") String email);


}
