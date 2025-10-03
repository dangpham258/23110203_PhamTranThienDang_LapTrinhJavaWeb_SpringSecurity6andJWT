# Demo 3 — Spring Security 6 + Thymeleaf (Spring Boot 3)

> Bản viết lại đầy đủ từ phần **Demo 3** đến hết (bao gồm mọi đoạn code trong ảnh), chuẩn hóa theo Spring Boot 3.x + Spring Security 6.x + Thymeleaf + SQL Server. Có thể chạy thẳng.

---

## 1) Khởi tạo Project & `pom.xml`

**Dependencies chính cần có:**

```xml
<dependencies>
    <!-- Web, Thymeleaf, JPA, Security -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <!-- SQL Server JDBC driver -->
    <dependency>
        <groupId>com.microsoft.sqlserver</groupId>
        <artifactId>mssql-jdbc</artifactId>
    </dependency>

    <!-- Thymeleaf extras for Spring Security 6 (sec:authorize, #authentication, ...) -->
    <dependency>
        <groupId>org.thymeleaf.extras</groupId>
        <artifactId>thymeleaf-extras-springsecurity6</artifactId>
    </dependency>

    <!-- Validation (nếu cần validate form) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>

    <!-- Devtools (tùy chọn) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>

    <!-- Lombok (tùy chọn) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>

    <!-- H2 (tùy chọn cho test nhanh) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

> **Lưu ý**: Nếu deploy war lên servlet container, thêm `spring-boot-starter-tomcat` scope `provided`. Với fat jar mặc định, không cần.

---

## 2) `application.properties` (SQL Server)

```properties
# ====== SQL Server ======
spring.datasource.driverClassName=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.datasource.url=jdbc:sqlserver://localhost;databaseName=SpringBootLoginRole;encrypt=false;trustServerCertificate=true;sslProtocol=TLSv1.2;characterEncoding=UTF-8
spring.datasource.username=sa
spring.datasource.password=1234567@a$

# ====== JPA / Hibernate ======
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

# ====== Thymeleaf & Server ======
spring.thymeleaf.cache=false
server.port=8092

spring.mandatory-file-encoding=UTF-8
spring.mail.default-encoding=UTF-8
```

> Có thể đổi `ddl-auto` thành `create` lần đầu để sinh bảng, sau đó đổi lại `update`.

---

## 3) Cấu trúc Package gợi ý

```
com.example.security6thymeleaf
├── Security6ThymeleafApplication.java
├── config
│   └── SecurityConfig.java
├── entity
│   ├── Role.java
│   ├── Users.java
│   └── Product.java
├── repository
│   ├── RoleRepository.java
│   ├── UserRepository.java
│   └── ProductRepository.java
├── service
│   ├── CustomUserDetails.java
│   ├── CustomUserDetailsService.java
│   ├── ProductService.java
│   └── UserService.java
├── controller
│   ├── HomeController.java
│   └── ProductController.java
└── util
    └── DataInitializer.java
```

---

## 4) Entity

### 4.1 `Role`
```java
package com.example.security6thymeleaf.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // ví dụ: ROLE_ADMIN, ROLE_USER
}
```

### 4.2 `Users`
```java
package com.example.security6thymeleaf.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @Column(nullable = false)
    private String password; // đã mã hóa (BCrypt)

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();
}
```

### 4.3 `Product`
```java
package com.example.security6thymeleaf.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "products")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Tên sản phẩm không được trống")
    @Column(nullable = false)
    private String name;

    @Positive(message = "Giá phải > 0")
    private double price;

    private String description;
}
```

---

## 5) Repository

### 5.1 `UserRepository`
```java
package com.example.security6thymeleaf.repository;

import com.example.security6thymeleaf.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.username = :username")
    Users getUserByUsername(@Param("username") String username);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByUsernameOrEmail(String username, String email);

    Optional<Users> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
```

### 5.2 `RoleRepository`
```java
package com.example.security6thymeleaf.repository;

import com.example.security6thymeleaf.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r FROM Role r WHERE r.name = :name")
    Role getUserByName(@Param("name") String name);

    Optional<Role> findByName(String name);
}
```

### 5.3 `ProductRepository`
```java
package com.example.security6thymeleaf.repository;

import com.example.security6thymeleaf.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
```

---

## 6) Service & UserDetails

### 6.1 `CustomUserDetails`
```java
package com.example.security6thymeleaf.service;

import com.example.security6thymeleaf.entity.Role;
import com.example.security6thymeleaf.entity.Users;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {
    private final Users user;

    public CustomUserDetails(Users user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = user.getRoles();
        return roles.stream()
                .map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
```

### 6.2 `CustomUserDetailsService`
```java
package com.example.security6thymeleaf.service;

import com.example.security6thymeleaf.entity.Users;
import com.example.security6thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return new CustomUserDetails(user);
    }
}
```

### 6.3 `UserService` (tiện thêm user)
```java
package com.example.security6thymeleaf.service;

import com.example.security6thymeleaf.entity.Role;
import com.example.security6thymeleaf.entity.Users;
import com.example.security6thymeleaf.repository.RoleRepository;
import com.example.security6thymeleaf.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public Users createUser(String username, String email, String rawPassword, String... roleNames) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username đã tồn tại");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email đã tồn tại");

        Set<Role> roles = new HashSet<>();
        for (String rn : roleNames) {
            Optional<Role> r = roleRepository.findByName(rn);
            roles.add(r.orElseGet(() -> roleRepository.save(Role.builder().name(rn).build())));
        }

        Users u = Users.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .roles(roles)
                .build();
        return userRepository.save(u);
    }
}
```

### 6.4 `ProductService`
```java
package com.example.security6thymeleaf.service;

import com.example.security6thymeleaf.entity.Product;
import com.example.security6thymeleaf.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public List<Product> findAll() { return productRepository.findAll(); }

    public Product findById(Long id) { return productRepository.findById(id).orElse(null); }

    public Product save(Product p) { return productRepository.save(p); }

    public void deleteById(Long id) { productRepository.deleteById(id); }
}
```

---

## 7) Security Config (Spring Security 6)

```java
package com.example.security6thymeleaf.config;

import com.example.security6thymeleaf.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity // cho phép @PreAuthorize nếu muốn
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/", "/index").authenticated()
                .requestMatchers("/products/new", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                .requestMatchers("/products", "/products/view/**").hasAnyRole("ADMIN", "USER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"));

        http.authenticationProvider(authenticationProvider());
        return http.build();
    }
}
```

---

## 8) Controllers

### 8.1 `HomeController`
```java
package com.example.security6thymeleaf.controller;

import com.example.security6thymeleaf.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;

    @GetMapping({"/", "/index"})
    public String index(Authentication auth, Model model) {
        model.addAttribute("products", productService.findAll());
        model.addAttribute("username", auth != null ? auth.getName() : "");
        return "index"; // templates/index.html
    }

    @GetMapping("/login")
    public String login() {
        return "login"; // templates/login.html
    }

    @GetMapping("/403")
    public String accessDenied() {
        return "403"; // templates/403.html
    }
}
```

### 8.2 `ProductController`
```java
package com.example.security6thymeleaf.controller;

import com.example.security6thymeleaf.entity.Product;
import com.example.security6thymeleaf.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "index"; // tái dùng index để hiển thị bảng sản phẩm
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        return "new_product"; // templates/new_product.html
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public String save(@Valid @ModelAttribute("product") Product product,
                       BindingResult result) {
        if (result.hasErrors()) return "new_product";
        productService.save(product);
        return "redirect:/";
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String edit(@PathVariable Long id, Model model) {
        Product p = productService.findById(id);
        if (p == null) return "redirect:/";
        model.addAttribute("product", p);
        return "edit_product"; // templates/edit_product.html
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("product") Product product,
                         BindingResult result) {
        if (result.hasErrors()) return "edit_product";
        product.setId(id);
        productService.save(product);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String delete(@PathVariable Long id) {
        productService.deleteById(id);
        return "redirect:/";
    }
}
```

---

## 9) Khởi tạo dữ liệu mẫu (roles, user, products)

### 9.1 `DataInitializer`
```java
package com.example.security6thymeleaf.util;

import com.example.security6thymeleaf.entity.Product;
import com.example.security6thymeleaf.service.ProductService;
import com.example.security6thymeleaf.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserService userService, ProductService productService) {
        return args -> {
            // Tạo user ADMIN & USER nếu chưa có
            try {
                userService.createUser("admin", "admin@example.com", "123456", "ROLE_ADMIN");
            } catch (Exception ignored) {}
            try {
                userService.createUser("user", "user@example.com", "123456", "ROLE_USER");
            } catch (Exception ignored) {}

            // Thêm vài product mẫu
            if (productService.findAll().isEmpty()) {
                productService.save(Product.builder().name("Bàn gỗ").price(1200000).description("Bàn gỗ sồi").build());
                productService.save(Product.builder().name("Ghế đơn").price(450000).description("Ghế bọc nỉ").build());
            }
        };
    }
}
```

### 9.2 `Security6ThymeleafApplication`
```java
package com.example.security6thymeleaf;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Security6ThymeleafApplication {
    public static void main(String[] args) {
        SpringApplication.run(Security6ThymeleafApplication.class, args);
    }
}
```

---

## 10) Thymeleaf Views

> Thêm `xmlns:th` và `xmlns:sec` để dùng attribute và sec:authorize.

### 10.1 `templates/index.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org" xmlns:sec="https://www.thymeleaf.org/thymeleaf-extras-springsecurity6">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Trang chủ</title>
    <link rel="stylesheet" th:href="@{/css/style.css}" />
</head>
<body>
<header>
    <h2>Demo Spring Security 6 + Thymeleaf</h2>
    <div>
        <span sec:authorize="isAuthenticated()">
            Xin chào, <b sec:authentication="name">user</b>!
            [<span sec:authentication="principal.authorities"></span>]
        </span>
        <span sec:authorize="!isAuthenticated()">
            <a th:href="@{/login}">Đăng nhập</a>
        </span>
        <span sec:authorize="isAuthenticated()">
            <a th:href="@{/logout}">Đăng xuất</a>
        </span>
    </div>
</header>

<main>
    <h3>Danh sách sản phẩm</h3>

    <div sec:authorize="hasRole('ADMIN')">
        <a th:href="@{/products/new}">+ Thêm sản phẩm</a>
    </div>

    <table border="1" cellpadding="6" cellspacing="0">
        <thead>
        <tr>
            <th>ID</th>
            <th>Tên</th>
            <th>Giá</th>
            <th>Mô tả</th>
            <th sec:authorize="hasRole('ADMIN')">Hành động</th>
        </tr>
        </thead>
        <tbody>
        <tr th:each="p : ${products}">
            <td th:text="${p.id}">1</td>
            <td th:text="${p.name}">Tên</td>
            <td th:text="${#numbers.formatDecimal(p.price, 0, 'COMMA', 0, 'POINT')}">0</td>
            <td th:text="${p.description}">Mô tả</td>
            <td sec:authorize="hasRole('ADMIN')">
                <a th:href="@{'/products/edit/' + ${p.id}}">Sửa</a>
                <form th:action="@{'/products/delete/' + ${p.id}}" method="post" style="display:inline">
                    <button type="submit" onclick="return confirm('Xóa sản phẩm này?')">Xóa</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
</main>
</body>
</html>
```

### 10.2 `templates/login.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Đăng nhập</title>
</head>
<body>
<h2>Đăng nhập</h2>

<div th:if="${param.error}">
    <p style="color:red">Sai tài khoản hoặc mật khẩu!</p>
</div>
<div th:if="${param.logout}">
    <p style="color:green">Bạn đã đăng xuất.</p>
</div>

<form th:action="@{/perform_login}" method="post">
    <label>Tên đăng nhập:</label>
    <input type="text" name="username" required />

    <label>Mật khẩu:</label>
    <input type="password" name="password" required />

    <button type="submit">Đăng nhập</button>
</form>
</body>
</html>
```

### 10.3 `templates/new_product.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Thêm sản phẩm</title>
</head>
<body>
<h2>Thêm sản phẩm</h2>

<form th:action="@{/products}" th:object="${product}" method="post">
    <div>
        <label>Tên:</label>
        <input type="text" th:field="*{name}" />
        <p style="color:red" th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Lỗi</p>
    </div>
    <div>
        <label>Giá:</label>
        <input type="number" step="0.01" th:field="*{price}" />
        <p style="color:red" th:if="${#fields.hasErrors('price')}" th:errors="*{price}">Lỗi</p>
    </div>
    <div>
        <label>Mô tả:</label>
        <textarea th:field="*{description}"></textarea>
    </div>

    <button type="submit">Lưu</button>
    <a th:href="@{/}">Hủy</a>
</form>
</body>
</html>
```

### 10.4 `templates/edit_product.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>Sửa sản phẩm</title>
</head>
<body>
<h2>Sửa sản phẩm</h2>

<form th:action="@{'/products/edit/' + ${product.id}}" th:object="${product}" method="post">
    <div>
        <label>Tên:</label>
        <input type="text" th:field="*{name}" />
        <p style="color:red" th:if="${#fields.hasErrors('name')}" th:errors="*{name}">Lỗi</p>
    </div>
    <div>
        <label>Giá:</label>
        <input type="number" step="0.01" th:field="*{price}" />
        <p style="color:red" th:if="${#fields.hasErrors('price')}" th:errors="*{price}">Lỗi</p>
    </div>
    <div>
        <label>Mô tả:</label>
        <textarea th:field="*{description}"></textarea>
    </div>

    <button type="submit">Cập nhật</button>
    <a th:href="@{/}">Hủy</a>
</form>
</body>
</html>
```

### 10.5 `templates/403.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <title>403 - Forbidden</title>
</head>
<body>
<h2>403 - Bạn không có quyền truy cập trang này.</h2>
<p><a th:href="@{/}">Về trang chủ</a></p>
</body>
</html>
```

---

## 11) Chạy thử

- Chạy ứng dụng.
- Truy cập `http://localhost:8092/login`.
- Đăng nhập:
  - **admin / 123456** → ROLE_ADMIN (được thêm qua `DataInitializer`).
  - **user / 123456** → ROLE_USER.
- Vào `/` (index):
  - USER nhìn thấy danh sách sản phẩm.
  - ADMIN ngoài xem còn có link **Thêm/Sửa/Xóa**.
- Nếu truy cập URL admin bởi USER → chuyển `/403`.

> Nếu muốn gọi API thêm user (giống Demo 2), có thể bổ sung REST Controller. Bản này tập trung vào giao diện Thymeleaf & phân quyền qua Security 6.

---

## 12) Ghi chú tương thích Spring Security 6

- Không còn `WebSecurityConfigurerAdapter`. Dùng `SecurityFilterChain` bean để cấu hình.
- `antMatchers` → **`requestMatchers`**.
- Khai báo `DaoAuthenticationProvider` + `UserDetailsService` + `PasswordEncoder`.
- Login form tùy biến qua `.formLogin()`.

---

## 13) Mẹo debug nhanh

- Bật `spring.jpa.show-sql=true` và `format_sql` để xem SQL.
- Nếu 403, kiểm tra role có prefix `ROLE_` chưa (ví dụ “ROLE_ADMIN”).
- Nếu login fail, xem `failureUrl` và log backend.

---

**Hoàn tất Demo 3 đến hết.**

