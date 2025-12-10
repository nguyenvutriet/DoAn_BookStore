package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Users;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsersRepository extends JpaRepository<Users, String> {
    long count();

    @Query("SELECT u FROM Users u WHERE u.customer.customerId=:id")
    public Users findByCustomerId(@Param("id") String customerId);

    @Transactional
    @Modifying
    @Query("UPDATE Users SET password=:pass WHERE userName=:user")
    public int updatePassword(@Param("user") String username, @Param("pass") String password);



}
