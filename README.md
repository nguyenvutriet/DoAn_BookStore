# UTE_BookStore

## Giới thiệu

UTEBookStore là website bán sách trực tuyến được phát triển bằng **Spring Boot** và **Thymeleaf**. Hệ thống hỗ trợ khách hàng tìm kiếm sách, quản lý giỏ hàng, đặt hàng, thanh toán trực tuyến và theo dõi đơn hàng. Ngoài các chức năng nghiệp vụ, website còn tích hợp nhiều cơ chế bảo mật và dịch vụ bên ngoài nhằm nâng cao trải nghiệm người dùng.

---

# Công nghệ sử dụng

## Backend
- Spring Boot
- Spring Security
- Spring Data JPA (Hibernate)
- Maven

## Frontend
- Thymeleaf
- Bootstrap
- HTML/CSS
- JavaScript

## Database
- MySQL

## Dịch vụ tích hợp
- Google OAuth 2.0
- Google reCAPTCHA
- Google Maps API
- Google Analytics 4 (GA4)
- OpenAI API (AI Chatbot)
- VNPay Sandbox
- Progressive Web App (PWA)

## Deploy
- AWS EC2
- Ubuntu Server
- Nginx
- Let's Encrypt (SSL/TLS)

---

# Kiến trúc hệ thống

```
Browser
    │
    ▼
 Nginx (HTTPS)
    │
    ▼
Spring Boot
    │
    ▼
MySQL Database
```

---

# Chức năng chính

## Chức năng dành cho khách hàng

- **Đăng ký và đăng nhập:** Hỗ trợ đăng ký tài khoản, đăng nhập bằng email/mật khẩu hoặc Google OAuth.
- **Quản lý hồ sơ cá nhân:** Cập nhật thông tin cá nhân, thay đổi mật khẩu và quản lý địa chỉ giao hàng.
- **Tìm kiếm và khám phá sách:** Tìm kiếm theo tên sách, tác giả, danh mục và lọc theo nhiều tiêu chí.
- **Xem chi tiết sách:** Hiển thị thông tin sách, hình ảnh, giá bán, số lượng tồn kho, đánh giá của khách hàng và chức năng đọc thử.
- **Đánh giá và xếp hạng:** Khách hàng có thể đánh giá, chấm sao và xem AI Summary tổng hợp đánh giá.
- **Gợi ý sách:** Đề xuất các đầu sách liên quan dựa trên nội dung và hành vi mua sắm.
- **Giỏ hàng:** Thêm, cập nhật, xóa sản phẩm và lưu trạng thái giỏ hàng.
- **Flash Sale:** Hiển thị các chương trình khuyến mãi với đồng hồ đếm ngược thời gian thực.
- **Đặt hàng (Checkout):** Hỗ trợ nhập thông tin nhận hàng, tính phí vận chuyển và xác minh Google reCAPTCHA trước khi tạo đơn.
- **Thanh toán trực tuyến:** Thanh toán thông qua VNPay Sandbox và xử lý kết quả giao dịch tự động.
- **Lịch sử đơn hàng:** Theo dõi trạng thái, xem chi tiết và lịch sử mua hàng.
- **Chatbot AI:** Hỗ trợ giải đáp thắc mắc, tìm kiếm sách và tư vấn sản phẩm bằng OpenAI API.
- **Progressive Web App (PWA):** Website có thể cài đặt lên màn hình chính (Add to Home Screen), hoạt động như ứng dụng độc lập (Standalone), hỗ trợ Service Worker để lưu trữ tài nguyên tĩnh và cho phép hoạt động ngoại tuyến ở mức cơ bản.

---

## Chức năng dành cho quản trị viên

- **Dashboard:** Thống kê doanh thu, số lượng đơn hàng, khách hàng và sách.
- **Quản lý sách:** Thêm, sửa, xóa, tìm kiếm và cập nhật thông tin sách.
- **Quản lý danh mục:** Quản lý các thể loại sách.
- **Quản lý Flash Sale:** Tạo và quản lý các chương trình giảm giá.
- **Quản lý tài khoản:** Quản lý tài khoản người dùng và phân quyền.
- **Quản lý khách hàng:** Xem thông tin khách hàng và lịch sử giao dịch.
- **Quản lý đơn hàng:** Theo dõi, xác nhận và cập nhật trạng thái đơn hàng.
- **Quản lý đánh giá:** Kiểm duyệt và quản lý đánh giá của khách hàng.
- **Hỗ trợ khách hàng:** Trò chuyện trực tuyến với khách hàng.
- **Quản lý đơn hàng bất thường:** Theo dõi các đơn hàng được đánh dấu bởi Rule-based Fraud Detection thông qua các trường `fraudFlag` và `fraudReason`.

---

# Các tính năng bảo mật

- **Spring Security:** Xác thực và phân quyền người dùng theo vai trò Customer và Admin.
- **BCrypt Password Encoder:** Mã hóa mật khẩu trước khi lưu vào cơ sở dữ liệu.
- **Google OAuth 2.0:** Đăng nhập nhanh bằng tài khoản Google.
- **Google reCAPTCHA:** Ngăn chặn bot tạo đơn hàng tự động tại chức năng Checkout.
- **Login Rate Limiting:** Giới hạn số lần đăng nhập sai nhằm chống Brute Force Attack.
- **Order Rate Limiting:** Giới hạn số lượng yêu cầu đặt hàng trong khoảng thời gian nhất định nhằm giảm spam.
- **Device Fingerprinting:** Nhận diện thiết bị để hỗ trợ phát hiện hành vi bất thường.
- **Rule-based Fraud Detection:** Tự động phát hiện các đơn hàng có dấu hiệu gian lận dựa trên các luật nghiệp vụ.
- **HTTPS (SSL/TLS):** Mã hóa dữ liệu truyền giữa trình duyệt và máy chủ bằng chứng chỉ SSL.
- **VNPay Signature Validation:** Kiểm tra chữ ký số và số tiền giao dịch để đảm bảo tính toàn vẹn của dữ liệu.
- **VNPay IPN Idempotency:** Đảm bảo mỗi giao dịch VNPay chỉ được xử lý một lần, tránh cập nhật trạng thái đơn hàng hoặc gửi email trùng lặp.
- **Input Validation:** Kiểm tra dữ liệu đầu vào nhằm hạn chế dữ liệu không hợp lệ và giảm nguy cơ khai thác lỗ hổng.
- **Session Management:** Quản lý phiên đăng nhập và tự động hủy phiên khi người dùng đăng xuất hoặc hết thời gian hiệu lực.

---

# Progressive Web App (PWA)

Website được triển khai dưới dạng Progressive Web App nhằm cải thiện trải nghiệm người dùng trên thiết bị di động.

Các tính năng đã triển khai:

- Web App Manifest
- Service Worker
- Installable (Add to Home Screen)
- Standalone Display Mode
- Offline Caching cơ bản
- Responsive trên Desktop và Mobile

---

# Hướng dẫn cài đặt

## 1. Yêu cầu

- Java 17
- Maven 3.9+
- MySQL 8+
- Git

---

## 2. Clone dự án

```bash
git clone https://github.com/nguyenvutriet/DoAn_BookStore

cd bookstore
```

---

## 3. Tạo cơ sở dữ liệu

Tạo database:

```sql
CREATE DATABASE storebookdb;
```

Import file:

```
database/storebookdb.sql
```

---

## 4. Cấu hình

Mở file

```
src/main/resources/application.properties
```

Sửa thông tin kết nối MySQL

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/storebookdb

spring.datasource.username=root

spring.datasource.password=your_password
```

Cấu hình các API:

- Google OAuth
- Google Maps API
- OpenAI API
- Google reCAPTCHA
- VNPay Sandbox
- Google Analytics 4

---

## 5. Chạy dự án

Sử dụng Maven

```bash
mvn clean install

mvn spring-boot:run
```

Hoặc

```bash
java -jar target/Project_BookStore-0.0.1-SNAPSHOT.jar
```

Website:

```
http://localhost:8080
```

---

# Tài khoản Demo

## Admin

```
Tài khoản:
admin

Password:
ad123456
```

## Customer

```
Tài khoản:
hoangbang

Password:
ad123456
```

---

# Link Deploy

Website

```
https://utebookstore.duckdns.org
```

GitHub

```
https://github.com/nguyenvutriet/DoAn_BookStore
```

---

# Phân công công việc

| Thành viên | Công việc                                                                                                                                                                                                                                                                                                                                                          |
|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Hoàng Bang | Phân tích yêu cầu hệ thống, Chat Socket với người bán, AI Chatbot tư vấn, AI Review Summary, Google reCAPTCHA, Sping Security, Device Fingerprinting, Rate Limiting (Order & Login), Rule-based Fraund Detection, triển khai AWS EC2, Nginx, HTTPS (SSL/TLS), kiểm thử và bảo mật, thực hiện Security Hardening, Pentest theo OWASP Top 10, định vị "Về chúng tôi" |
| Mai Quỳnh  | Giao diện trang chủ, trang danh mục và trang chi tiết sách, module Flash Sale và đồng hồ đếm ngược, quản lý đánh giá sản phẩm, quản lý khách hàng, đơn hàng và sản phẩm, tích hợp Google Analytis 4 (GA4) theo dõi hành vi người dùng                                                                                                                              |
| Thanh Tú   | Phát triển chức năng Checkout và quản lý đơn hàng, tích hợp VNPay Sandbox và xử lý kết quả giao dịch, phát triển Dashboard quản trị, xây dựng giỏ hàng, lưu trạng thái giỏ hàng, kiểm thử chức năng quản lý sách và cơ sở dữ liệu                                                                                                                                  |
| Thanh Trà  | Xây dựng module quản lý sách và danh mục. Chức năng tìm kiếm, lọc sách theo danh mục và từ khóa, thống kê doanh thu, xây dựng tìm kiếm sách tương đồng, lịch sử đơn hàng, kiểm thử giao diện và trải nghiệm, kiểm thử quy trình đặt hàng và thanh toán                                                                                                             |
| Vũ Triết   | Thiết kế cơ sở dữ liệu, Entity, quan hệ giữa các bảng, Đăng ký/Đăng nhập, Google OAuth 2.0, quản lý tài khoản, hồ sơ cá nhân, tích hợp Google OAuth 2.0, đổi mật khẩu, mô hình hồi quy dự đoán doanh thu, xác thực session, tích hợp GG Map để tính cost vận chuyển                                                                                                |

---

# Cấu trúc thư mục

```
src
 ├── main
 │   ├── java
 │   ├── resources
 │   │      ├── static
 │   │      ├── templates
 │   │      └── application.properties
 │   └── test
 └── pom.xml
```

---

# Giấy phép

Dự án được phát triển phục vụ mục đích học tập tại **Trường Đại học Công Nghệ Kỹ thuật Thành phố Hồ Chí Minh (HCM-UTE)**.
