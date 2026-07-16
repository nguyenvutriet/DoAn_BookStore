package com.example.project_bookstore.Service;

import com.example.project_bookstore.Entity.OrderDetail;
import com.example.project_bookstore.Entity.Orders;
import com.example.project_bookstore.Repository.IOrderDetailRepository;
import com.example.project_bookstore.Repository.IOrdersRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class OrdersService {

    // Số phút khách được phép thanh toán lại trước khi đơn tự động bị hủy
    public static final long RETRY_WINDOW_MINUTES = 1440;

    @Autowired
    private IOrdersRepository repo;
    @Autowired
    private IOrderDetailRepository orderDetailRepo;

    @Autowired
    private FlashSaleService flashSaleService;

    @Autowired
    private PaymentService paymentService;

    public String generateId(){
        List<Orders> orders = repo.findAll();
        List<Integer> dsSo = new java.util.ArrayList<>();
        for(Orders o : orders){
            int id = Integer.parseInt(o.getOrderId().substring(1));
            dsSo.add(id);
        }
        int idMax = Collections.max(dsSo);
        idMax = idMax+1;
        return "O" + idMax;
    }

    public List<Orders> getOrders(String customerId){
        return repo.findByCustomerId(customerId);
    }

    public void updateStatus(String id, String status){
        repo.updateStatus(id, status);
    }

    public Orders getOrderById(String orderId){
        return repo.findById(orderId).orElse(null);
    }

    /** Kiểm tra đơn hàng có phải thanh toán qua VNPay không */
    public boolean isVnpayOrder(Orders order) {
        return order != null && order.getPaymentMethod() != null
                && order.getPaymentMethod().toLowerCase().contains("vnpay");
    }

    /** Số giây còn lại để thanh toán lại (0 nếu hết hạn) */
    public long getRemainingSeconds(Orders order) {
        if (order.getOrderDate() == null) return 0;
        long elapsedMs = new Date().getTime() - order.getOrderDate().getTime();
        long remainingMs = RETRY_WINDOW_MINUTES * 60 * 1000 - elapsedMs;
        return Math.max(0, remainingMs / 1000);
    }

    /** Có được phép bấm nút "Thanh toán lại" không */
    public boolean canRetryPayment(Orders order) {
        if (order == null) return false;
        if (!"Pending".equals(order.getStatus())) return false;
        if (!isVnpayOrder(order)) return false;
        return getRemainingSeconds(order) > 0;
    }

    /** Gọi khi hiển thị 1 order cụ thể: tự hủy nếu đã quá hạn 5p mà chưa thanh toán */
    @Transactional
    public void expireIfNeeded(Orders order) {
        if (order != null && "Pending".equals(order.getStatus())
                && isVnpayOrder(order) && getRemainingSeconds(order) <= 0) {
            order.setStatus("Cancelled");
            repo.save(order);
        }
    }

    @Autowired
    private VNPayService vnPayService;

    /** VNPay báo thành công -> Confirmed + lưu Payment. Idempotent: gọi lại nhiều lần vẫn an toàn */
    @Transactional
    public void markOrderPaid(String orderId) {
        Orders order = repo.findById(orderId).orElse(null);
        if (order == null) return;

        // Idempotency: nếu đã Confirmed hoặc đã có Payment rồi thì không xử lý lại
        if ("Confirmed".equals(order.getStatus())) return;

        order.setStatus("Confirmed");
        repo.save(order);
        paymentService.saveVnpayPayment(order); // bên trong đã check findByOrder_OrderId trước khi save
    }

    @Transactional
    public void markOrderUnPaid(String orderId) {
        Orders order = repo.findById(orderId).orElse(null);
        if (order != null) {
            expireIfNeeded(order);
        }
    }

    /**
     * Job chạy mỗi phút: trước khi hủy đơn VNPay quá hạn, GỌI QUERYDR để đối soát thật
     * -> tránh hủy nhầm đơn đã bị trừ tiền nhưng return/IPN không về được (mất kết nối).
     */
    @Scheduled(fixedRate = 600000)
    @Transactional
    public void autoCancelExpiredVnpayOrders() {
        List<Orders> pendingOrders = repo.findByStatus("Pending");

        for (Orders o : pendingOrders) {
            if (!isVnpayOrder(o)) continue;
            if (getRemainingSeconds(o) > 0) continue; // còn hạn thì bỏ qua

            try {
                Map<String, String> result = vnPayService.queryTransaction(o.getOrderId(), o.getOrderDate());
                String transStatus = result.get("vnp_TransactionStatus");

                if ("00".equals(transStatus)) {
                    // VNPay xác nhận ĐÃ thanh toán thành công nhưng hệ thống mình chưa ghi nhận
                    // (đúng case "tiền trừ nhưng mất kết nối") -> confirm lại thay vì hủy
                    markOrderPaid(o.getOrderId());
                    continue;
                }
            } catch (Exception e) {
                // Không gọi được VNPay (sandbox lỗi, timeout...) -> an toàn là KHÔNG hủy vội,
                // để lần chạy job kế tiếp thử lại, tránh hủy oan đơn đã thanh toán
                continue;
            }

            // VNPay xác nhận KHÔNG có giao dịch thành công -> hủy thật
            o.setStatus("Cancelled");
            repo.save(o);
        }
    }

    @Transactional
    public void placeOrder(Orders order, List<OrderDetail> details) {
        repo.save(order);

        for (OrderDetail detail : details) {
            String bookId = detail.getBook().getBookId();
            java.util.Optional<com.example.project_bookstore.Entity.FlashSaleDetail> optFs =
                    flashSaleService.getActiveSaleForBook(bookId);
            if (optFs.isPresent()) {
                String flashSaleId = optFs.get().getFlashSale().getFlashSaleId();
                flashSaleService.reserveStock(flashSaleId, bookId, detail.getQuantity());
            }
            orderDetailRepo.save(detail);
        }
    }

    public List<Orders> getOrdersByStatus(String customerId, String status){
        return repo.findByCustomerIdAndStatus(customerId, status);
    }
}