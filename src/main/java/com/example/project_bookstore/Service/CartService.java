package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.*;
import com.example.project_bookstore.Repository.IBooksRepository;
import com.example.project_bookstore.Repository.ICartDetailRepository;
import com.example.project_bookstore.Repository.ICartRepository;
import com.example.project_bookstore.Repository.IUsersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CartService {

    @Autowired
    private ICartRepository repo;

    public Cart getCartByCustomer(String customerId) {
        return repo.findByCustomer_CustomerId(customerId);
    }

    @Autowired
    private ICartDetailRepository cartDetailRepository;

    @Autowired
    private IUsersRepository usersRepository;

    @Autowired
    private IBooksRepository booksRepository;
    /**
     * HÀM MỚI: Thêm sách vào giỏ của user đang đăng nhập
     */
    @Transactional
    public void addToCart(String username, String bookId, int quantity) {

        // 1. Lấy user theo username
        Users user = usersRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại: " + username));

        Customers customer = user.getCustomer();
        if (customer == null) {
            throw new RuntimeException("User chưa gắn với Customer");
        }

        // 2. Lấy hoặc tạo cart cho customer
        Cart cart = repo.findByCustomer_CustomerId(customer.getCustomerId());
        if (cart == null) {
            cart = new Cart();
            cart.setCartId(generateCartId());
            cart.setCustomer(customer);
            cart.setQuantity(0);
            cart.setTotalAmount(BigDecimal.ZERO);
            cart = repo.save(cart);
        }

        // 3. Lấy sách
        Books book = booksRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book không tồn tại: " + bookId));

        // 4. Lấy / tạo CartDetail (1 dòng trong CartDetail)
        CartDetailId cdId = new CartDetailId(cart.getCartId(), bookId);
        Optional<CartDetail> optDetail = cartDetailRepository.findById(cdId);

        CartDetail detail;
        if (optDetail.isPresent()) {
            // Nếu đã có sách này trong giỏ → cộng thêm số lượng
            detail = optDetail.get();
            detail.setQuantity(detail.getQuantity() + quantity);
        } else {
            // Nếu chưa có → tạo mới
            detail = new CartDetail();
            detail.setCartDetailId(cdId);
            detail.setCart(cart);
            detail.setBook(book);
            detail.setQuantity(quantity);
            detail.setUnitPrice(book.getPrice()); // đơn giá = price trong Books
        }

        cartDetailRepository.save(detail);

        // 5. Tính lại tổng số lượng và tổng tiền giỏ hàng
        recalcCart(cart);
    }

    /**
     * Tính lại quantity + totalAmount của Cart dựa vào CartDetail
     */
    private void recalcCart(Cart cart) {
        List<CartDetail> details =
                cartDetailRepository.findByCartDetailId_CartId(cart.getCartId());

        BigDecimal total = BigDecimal.ZERO;
        int totalQuantity = 0;

        for (CartDetail d : details) {
            BigDecimal line = d.getUnitPrice()
                    .multiply(BigDecimal.valueOf(d.getQuantity()));
            total = total.add(line);
            totalQuantity += d.getQuantity();
        }

        cart.setTotalAmount(total);
        cart.setQuantity(totalQuantity);
        repo.save(cart);
    }

    /**
     * Sinh cartId đơn giản
     */
    private String generateCartId() {
        return "CART_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

}
