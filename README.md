# 🔐 Spring Boot Security 6 + JWT Authentication

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-green.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.x-blue.svg)](https://spring.io/projects/spring-security)
[![JWT](https://img.shields.io/badge/JWT-JSON%20Web%20Token-orange.svg)](https://jwt.io/)
[![SQL Server](https://img.shields.io/badge/Database-SQL%20Server-red.svg)](https://www.microsoft.com/en-us/sql-server)
[![Thymeleaf](https://img.shields.io/badge/Template-Thymeleaf-green.svg)](https://www.thymeleaf.org/)

> **Dự án demo Spring Security 6 với JWT Authentication và Thymeleaf**  
> **Sinh viên:** 23110203 - Phạm Trần Thiên Đăng  
> **Môn học:** Lập trình Web

---

## 📝 Commit History

| Commit | Mô tả |
|--------|-------|
| `demo 1` | Cài đặt, Cấu hình, Phân quyền trong Spring Security |
| `demo 2` | Sử dụng database để lưu và lấy dữ liệu cho việc phân quyền trong Spring Security |
| `xóa file thừa` | Spring security với Thymeleaf |
| `demo 4 - hoàn thiện` | Demo JWT với Spring Boot 3 – Security 6 |

---

## 📋 Tổng quan

Dự án này là một ứng dụng web Spring Boot với Spring Security 6, tích hợp JWT authentication và giao diện Thymeleaf. Ứng dụng hỗ trợ:

- ✅ **Xác thực người dùng** với JWT tokens
- ✅ **Phân quyền dựa trên vai trò** (ADMIN, USER)
- ✅ **Quản lý sản phẩm** với CRUD operations
- ✅ **Giao diện web** với Thymeleaf templates
- ✅ **REST API** cho mobile/frontend applications
- ✅ **CORS** configuration cho cross-origin requests

---

## 🛠️ Công nghệ sử dụng

| Technology | Version | Mô tả |
|------------|---------|-------|
| **Spring Boot** | 3.x | Framework chính |
| **Spring Security** | 6.x | Authentication & Authorization |
| **JWT** | 0.12.6 | Token-based authentication |
| **Thymeleaf** | 3.x | Template engine |
| **Spring Data JPA** | - | Database access layer |
| **SQL Server** | - | Database |
| **Lombok** | - | Code generation |
| **Maven** | - | Build tool |

---

## 🚀 Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17+
- Maven 3.6+
- SQL Server
- IDE (IntelliJ IDEA, Eclipse, VS Code)

### Cấu hình Database
1. Tạo database `security_su` trong SQL Server
2. Cập nhật thông tin kết nối trong `application.properties`:
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=security_su;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Chạy ứng dụng
```bash
# Clone repository
git clone <repository-url>
cd Springboot_23110203_PhamTranThienDang_SpringSecurity6andJWT

# Build và chạy
./mvnw clean install
./mvnw spring-boot:run

# Hoặc trên Windows
mvnw.cmd clean install
mvnw.cmd spring-boot:run
```

Ứng dụng sẽ chạy tại: **http://localhost:8092**

---

## 📚 API Documentation

### Authentication Endpoints

#### 🔐 Đăng ký tài khoản
```http
POST /auth/signup
Content-Type: application/json

{
  "fullName": "Nguyễn Văn A",
  "email": "user@example.com",
  "password": "123456"
}
```

#### 🔑 Đăng nhập
```http
POST /auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "123456"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "expiresIn": 3600000
}
```

### Protected Endpoints

#### 👤 Thông tin user hiện tại
```http
GET /users/me
Authorization: Bearer <jwt_token>
```

#### 👥 Danh sách users
```http
GET /users
Authorization: Bearer <jwt_token>
```

#### ➕ Tạo user mới (Admin)
```http
POST /user/new
Content-Type: application/json

{
  "username": "newuser",
  "email": "newuser@example.com",
  "password": "123456",
  "roles": ["ROLE_USER"]
}
```

---

## 🌐 Web Interface

### Trang chủ
- **URL:** http://localhost:8092/
- **Mô tả:** Hiển thị danh sách sản phẩm
- **Quyền:** Đã đăng nhập

### Quản lý sản phẩm
- **Thêm sản phẩm:** `/products/new` (ADMIN only)
- **Sửa sản phẩm:** `/products/edit/{id}` (ADMIN only)
- **Xóa sản phẩm:** `/products/delete/{id}` (ADMIN only)
- **Xem sản phẩm:** `/products/view/{id}` (ADMIN, USER)

---

## 🧪 Testing với Postman

### 1. Đăng ký tài khoản
```bash
POST http://localhost:8092/auth/signup
Body: {
  "fullName": "Test User",
  "email": "test@example.com", 
  "password": "123456"
}
```

### 2. Đăng nhập lấy JWT
```bash
POST http://localhost:8092/auth/login
Body: {
  "email": "test@example.com",
  "password": "123456"
}
```

### 3. Sử dụng JWT cho các request bảo vệ
```bash
GET http://localhost:8092/users/me
Authorization: Bearer <token_from_step_2>
```

---

## 📁 Cấu trúc dự án

```
src/main/java/vn/iotstar/
├── config/
│   ├── SecurityConfig.java          # Cấu hình Spring Security
│   └── ApplicationConfiguration.java # Bean configuration
├── controller/
│   ├── AuthenticationController.java # JWT auth endpoints
│   ├── UsersController.java         # User management
│   ├── HomeController.java          # Web pages
│   └── ProductController.java       # Product CRUD
├── entity/
│   ├── Users.java                   # User entity
│   ├── Role.java                    # Role entity
│   └── Product.java                 # Product entity
├── repository/
│   ├── UserRepository.java          # User data access
│   └── RoleRepository.java          # Role data access
├── service/
│   ├── AuthenticationService.java   # Auth business logic
│   ├── CustomUserDetails.java       # UserDetails implementation
│   └── CustomUserDetailsService.java # UserDetailsService
├── services/
│   └── JwtService.java              # JWT operations
├── filter/
│   └── JwtAuthenticationFilter.java # JWT filter
├── models/
│   ├── LoginUserModel.java          # Login request DTO
│   ├── RegisterUserModel.java       # Register request DTO
│   └── LoginResponse.java           # Login response DTO
└── exception/
    └── GlobalExceptionHandler.java  # Global error handling
```

---

## 🔧 Cấu hình quan trọng

### JWT Configuration
```properties
# JWT Secret Key (Base64 encoded)
security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b

# Token expiration time (1 hour)
security.jwt.expiration-time=3600000
```

### Security Configuration
- **Stateless session** cho JWT
- **CORS enabled** cho cross-origin requests
- **Role-based access control** (ADMIN, USER)
- **Form login** cho web interface
- **JWT authentication** cho API

---

## 🐛 Troubleshooting

### Lỗi thường gặp

1. **"Invalid column name"**
   - **Nguyên nhân:** Database schema không khớp với entity
   - **Giải pháp:** Set `spring.jpa.hibernate.ddl-auto=create` để tạo lại tables

2. **"Invalid compact JWT string"**
   - **Nguyên nhân:** Token không đúng format
   - **Giải pháp:** Copy đúng token từ `/auth/login` response

3. **403 Forbidden**
   - **Nguyên nhân:** Thiếu hoặc sai JWT token
   - **Giải pháp:** Thêm `Authorization: Bearer <token>` header

4. **Port conflict**
   - **Nguyên nhân:** Port 8092 đã được sử dụng
   - **Giải pháp:** Đổi port trong `application.properties`


---

## 👨‍💻 Tác giả

**Phạm Trần Thiên Đăng**  
- **MSSV:** 23110203
- **Môn học:** Lập trình Web

---

## 📄 License

Dự án này được tạo ra cho mục đích học tập và demo. Vui lòng không sử dụng cho mục đích thương mại.

---

<div align="center">

**⭐ Nếu dự án hữu ích, hãy cho một star! ⭐**

Made with ❤️ by Phạm Trần Thiên Đăng

</div>
