package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.CartDetail;
import com.example.project_bookstore.Entity.CartDetailId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
    // Lấy các dòng chi tiết theo cartId
    List<CartDetail> findByCartDetailId_CartId(String cartId);
}
