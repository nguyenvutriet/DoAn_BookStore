package com.example.project_bookstore.Repository;

import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Entity.CartDetail;
import com.example.project_bookstore.Entity.CartDetailId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICartDetailRepository extends JpaRepository<CartDetail, CartDetailId> {
    // Lấy các dòng chi tiết theo cartId
    List<CartDetail> findByCartDetailId_CartId(String cartId);
    @Transactional
    List<CartDetail> findByCart(Cart cart);
    @Transactional
    @Modifying
    void deleteByCartAndBook_BookId(Cart cart, String bookId);
    void deleteByCart(Cart cart);

}
