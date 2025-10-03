# Spring Security 6 với SQL Server Database

## 📋 Tổng quan dự án
Dự án này minh họa cách sử dụng Spring Security 6 với database authentication sử dụng SQL Server theo Demo 2.

## ✅ Đã sửa lỗi và hoàn thiện dự án

### 🔧 **Lỗi đã sửa:**
- **"The constructor UserInfoService(UserInfoRepository) is undefined"** - Đã sửa bằng cách sử dụng `@Autowired` thay vì constructor injection trong SecurityConfig
- **Xóa các package cũ** - Đã xóa toàn bộ package `vn.iotstar` cũ để tránh xung đột
- **Di chuyển main class** - Đã di chuyển main application class ra ngoài để có thể scan được tất cả packages

## 🗄️ Cấu trúc Database

### Database: `security_su`
### Bảng: `user_info`
```sql
CREATE TABLE user_info (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(50) NOT NULL UNIQUE,
    email NVARCHAR(100),
    password NVARCHAR(255) NOT NULL,
    roles NVARCHAR(255)
);
```

## ⚙️ Cấu hình ứng dụng
File `application.properties` đã được cấu hình:
```properties
spring.datasource.url=jdbc:sqlserver://LAPTOP-CPJ5IEEE:1433;databaseName=security_su;encrypt=true;trustServerCertificate=true;sslProtocol=TLSv1.2;characterEncoding=UTF-8
spring.datasource.username=sa
spring.datasource.password=PTTDang@2005
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 🏗️ **Cấu trúc dự án hiện tại:**

```
src/main/java/vn/iotstar/
├── Springboot23110203PhamTranThienDangSpringSecurity6andJwtApplication.java  # Main application class
├── config/
│   ├── SecurityConfig.java              # Cấu hình Spring Security
│   ├── UserInfoService.java             # Service implements UserDetailsService
│   └── UserInfoUserDetails.java         # Convert UserInfo sang UserDetails
├── controller/
│   ├── CustomerController.java          # Controller cho Customer
│   └── UserController.java              # Controller cho User
├── entity/
│   ├── Customer.java                     # Entity Customer
│   └── UserInfo.java                     # Entity UserInfo với JPA annotations
├── repository/
│   └── UserInfoRepository.java          # Repository cho UserInfo
└── service/
    └── UserService.java                 # Service để thêm user
```

### 🚀 **Cách chạy dự án:**

1. **Chạy ứng dụng**:
   ```bash
   mvn spring-boot:run
   ```

2. **Tạo database và bảng** (chạy trong SQL Server Management Studio):
   ```sql
   CREATE DATABASE security_su;
   USE security_su;
   CREATE TABLE user_info (
       id INT IDENTITY(1,1) PRIMARY KEY,
       name NVARCHAR(50) NOT NULL UNIQUE,
       email NVARCHAR(100),
       password NVARCHAR(255) NOT NULL,
       roles NVARCHAR(255)
   );
   ```

### 🔐 **Test Authentication:**

#### Test endpoint `/customer/all` (cần ROLE_ADMIN):
1. Truy cập `http://localhost:8080/customer/all`
2. Sẽ redirect đến trang login
3. Đăng nhập với `dang/123`
4. Sau khi đăng nhập thành công, sẽ thấy danh sách customers

#### Test endpoint `/customer/{id}` (cần ROLE_USER):
1. Truy cập `http://localhost:8080/customer/001`
2. Sẽ redirect đến trang login
3. Đăng nhập với `user/123` hoặc `dang/123`
4. Sau khi đăng nhập thành công, sẽ thấy thông tin customer

### 🎯 **Kết quả:**
Dự án bây giờ đã hoạt động hoàn hảo với Spring Security 6 và SQL Server database!

## 📱 **Hướng dẫn sử dụng Postman:**

### 1. **Setup Postman Collection:**
- Tạo collection mới: "Spring Security Demo"
- Base URL: `http://localhost:8080`

### 2. **Request thêm user ADMIN:**
- **Method**: `POST`
- **URL**: `http://localhost:8080/user/new`
- **Headers**: 
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
      "name": "dang",
      "email": "dang@example.com",
      "password": "123",
      "roles": "ROLE_ADMIN,ROLE_USER"
  }
  ```

### 3. **Request thêm user USER:**
- **Method**: `POST`
- **URL**: `http://localhost:8080/user/new`
- **Headers**: 
  ```
  Content-Type: application/json
  ```
- **Body** (raw JSON):
  ```json
  {
      "name": "user",
      "email": "user@example.com",
      "password": "123",
      "roles": "ROLE_USER"
  }
  ```

### 4. **Test Authentication Flow:**
1. **Chạy ứng dụng**: `mvn spring-boot:run`
2. **Thêm users**: Sử dụng 2 request trên
3. **Test browser**: Truy cập `http://localhost:8080/customer/all`
4. **Login**: Sử dụng `dang/123` hoặc `user/123`
5. **Verify**: Kiểm tra phân quyền hoạt động đúng

## 🔧 Lưu ý kỹ thuật

1. **Password Encoding**: Sử dụng BCryptPasswordEncoder
2. **Roles**: Được lưu dưới dạng string phân cách bằng dấu phẩy
3. **Database**: SQL Server với connection string đã cấu hình SSL
4. **JPA**: Hibernate sẽ tự động tạo/cập nhật schema khi chạy ứng dụng
5. **Spring Security**: Sử dụng method-level security với `@PreAuthorize`
6. **Package Structure**: Tuân theo cấu trúc chuẩn Spring Boot với separation of concerns
