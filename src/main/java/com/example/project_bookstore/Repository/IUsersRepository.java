package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsersRepository extends JpaRepository<Users, String> {
    long count();
}
