package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.Cart;
import com.example.project_bookstore.Entity.CartDetail;
import com.example.project_bookstore.Entity.CartDetailId;
import com.example.project_bookstore.Repository.ICartDetailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartDetailService {

    @Autowired
    private ICartDetailRepository repo;   // dùng bean repo, không dùng static

    public List<CartDetail> getDetailsByCart(Cart cart) {
        return repo.findByCartDetailId_CartId(cart.getCartId());
    }

    public void updateQuantity(String cartId, String bookId, int quantity) {

        CartDetailId id = new CartDetailId(cartId, bookId);

        // SỬA: dùng repo.findById() — KHÔNG gọi ICartDetailRepository.findById()
        CartDetail detail = repo.findById(id).orElse(null);

        if (detail != null) {
            detail.setQuantity(quantity);
            repo.save(detail);   // Lưu thay đổi
        }
    }
}
