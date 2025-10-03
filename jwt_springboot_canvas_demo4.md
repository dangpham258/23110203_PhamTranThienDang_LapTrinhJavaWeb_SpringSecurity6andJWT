# JSON Web Token (JWT)

## Thông tin Giảng viên
- **Giảng viên:** ThS. Nguyễn Hữu Trung  
- **Khoa:** Công Nghệ Thông Tin  
- **Trường:** Đại học Sư Phạm Kỹ Thuật TP.HCM  
- **Liên hệ:** 090.861.7108 – trungnh@hcmute.edu.vn  
- **YouTube:** [@baigiai](https://www.youtube.com/@baigiai)

---

## Nội dung
1. Giới thiệu JWT  
2. Demo JWT trên Spring Boot 3 – Security 6

---

## JWT là gì?
- JWT là phương tiện đại diện cho yêu cầu giữa Client – Server.  
- Chuỗi JWT gồm **3 phần**: header, payload, signature, ngăn cách bằng dấu `.`.  
- Có thể ký bằng **secret** (HMAC) hoặc **public/private key** (RSA/ECDSA).

---

## Cấu trúc của JWT
### Header
```json
{ "typ": "JWT", "alg": "HS256" }
```
- `typ`: Loại đối tượng (JWT).  
- `alg`: Thuật toán mã hóa (HS256).

### Payload
Chứa thông tin cần đặt trong Token, ví dụ:
```json
{ "user_name": "admin", "user_id": "1", "authorities": "ADMIN", "images": "u1.jpg" }
```

### Signature
- Dùng để xác thực danh tính người gửi.  
- Được tạo từ header, payload và secret.
```text
data = base64urlEncode(header) + "." + base64urlEncode(payload)
signature = Hash(data, secret)
```
Kết quả: `header.payload.signature`

Ví dụ token:
```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsidGVzdGp3dHJlc291cmNlaWQiXSwidXNlcl9uYW1lIjoiYWRtaW4iLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNTEzNzE.9nRhBWiRoryc8fV5xRpTmw9iyJ6EM7WTGTjvCM1e36Q
```

---

## Claims trong JWT
- **Registered Claims** (nên có):
  - `iss`: Issuer – Tổ chức phát hành.
  - `sub`: Subject – Chủ thể.
  - `aud`: Audience – Người nhận.
  - `exp`: Expiration – Hạn sử dụng.

- **Public Claims:** Định nghĩa bởi người dùng JWT. Ví dụ: `name`, `email`, `locale`, `profile`, `picture`.

- **Private Claims:** Do các bên thỏa thuận, không có tiêu chuẩn, thường đặt tên ngắn gọn (≤3 ký tự).

---

## JWT hoạt động thế nào?
1. Client gửi yêu cầu đăng nhập.  
2. Server xác thực, tạo JWT bằng secret key và trả về Client.  
3. Client lưu JWT (localStorage, sessionStorage, cookies).  
4. Mọi request tiếp theo Client gửi kèm JWT trong header:  
```
Authorization: Bearer <token>
```
5. Server xác minh signature, phản hồi phù hợp.

---

## Lý do sử dụng JWT
- **Authentication:** Xác thực người dùng.  
- **Authorization:** Cấp quyền truy cập tài nguyên.  
- **Trao đổi thông tin an toàn:** JWT được ký trước khi gửi đi.

---

## Ưu điểm
- **Gọn nhẹ:** Chi phí truyền tải thấp.  
- **Bảo mật:** Chống giả mạo, đảm bảo an toàn.  
- **Phổ thông:** Hỗ trợ đa ngôn ngữ, dễ triển khai.

---

## Nhược điểm
- **Kích thước:** Bị giới hạn bởi HTTP Header (~8KB).  
- **Rủi ro bảo mật:** Nếu không kiểm tra signature, expire, dễ bị lợi dụng.  
- **Thời gian hết hạn dài:** Gây nguy cơ rò rỉ.

---

## Ứng dụng JWT
- **Single Sign-On (SSO).**  
- **API Authorization.**  
- **User Authentication.**  
- **Microservices Communication.**

---

## Các lưu ý bảo mật
- Tránh dùng secret key yếu.  
- Không lưu token không an toàn (XSS).  
- Phải thiết lập thời gian hết hạn.  
- Luôn dùng HTTPS để truyền tải.

---

# Demo JWT với Spring Boot 3 – Security 6

### Bước 1: Thêm dependency
```xml
<dependency>
   <groupId>io.jsonwebtoken</groupId>
   <artifactId>jjwt-api</artifactId>
   <version>0.12.6</version>
</dependency>
<dependency>
   <groupId>io.jsonwebtoken</groupId>
   <artifactId>jjwt-impl</artifactId>
   <version>0.12.6</version>
</dependency>
<dependency>
   <groupId>io.jsonwebtoken</groupId>
   <artifactId>jjwt-jackson</artifactId>
   <version>0.12.6</version>
</dependency>
```

### Bước 2: Tạo Entity
### Bước 3: Tạo Models
### Bước 4: Khởi tạo Repository và Services
```properties
spring.application.name=JWT_springboot3
server.port=8005
spring.datasource.url=jdbc:mysql://localhost:3306/jwt_springboot3?serverTimezone=UTC&allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=1234567@a$

spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false

security.jwt.secret-key=3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b
security.jwt.expiration-time=3600000
```

### Bước 5: Tạo ApplicationConfiguration
### Bước 6: Tạo Filter
### Bước 7: Tạo SecurityConfig
### Bước 8: Tạo @RestController
### Bước 9: Test
- Đăng ký, đăng nhập sinh JWT.  
- Dùng JWT với `Bearer token` để truy cập endpoint `/users/me`, `/users`.

### Bước 10: Xử lý Exception
| Lỗi | Ngoại lệ | HTTP Status |
|-----|----------|-------------|
| Thông tin đăng nhập sai | BadCredentials | 401 |
| Tài khoản bị khóa | AccountStatusException | 403 |
| Truy cập trái phép | AccessDenied | 403 |
| JWT không hợp lệ | SignatureException | 401 |
| JWT hết hạn | ExpiredJwtException | 401 |

### Render bằng Ajax
- View: `login.html`, `profile.html`.  
- File JS: `mainjs.js`.  
- Controller: `AuthController.java`.

---

# Kết luận
Tài liệu trình bày toàn bộ khái niệm, cấu trúc, cách hoạt động, ưu nhược điểm, ứng dụng và demo triển khai JWT trên Spring Boot 3 với Security 6.



---

## PHỤ LỤC MÃ NGUỒN ĐẦY ĐỦ (Spring Boot 3 + Spring Security 6 + JJWT 0.12.6)

> Phần này bổ sung toàn bộ mã nguồn tham khảo theo đúng luồng các bước trên slide: Entity → Model (DTO) → Repository/Service → Application Configuration → Jwt Filter → SecurityConfig → REST Controllers → Views (login.html, profile.html) → mainjs.js → Test/API. Có thể copy‑paste chạy trực tiếp (điều chỉnh `spring.datasource.*` theo máy của bạn).

> Lưu ý: `application.properties` đã có ở phần trên nên giữ nguyên. Secret key trong ví dụ mang tính minh họa.

---

### 0) pom.xml (tham khảo)
```xml
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>
  <groupId>com.example</groupId>
  <artifactId>jwt_springboot3</artifactId>
  <version>0.0.1-SNAPSHOT</version>
  <properties>
    <java.version>17</java.version>
    <spring-boot.version>3.3.2</spring-boot.version>
  </properties>
  <dependencyManagement>
    <dependencies>
      <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
      </dependency>
    </dependencies>
  </dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
      <groupId>com.mysql</groupId>
      <artifactId>mysql-connector-j</artifactId>
      <scope>runtime</scope>
    </dependency>

    <!-- JJWT 0.12.6 theo slide -->
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-api</artifactId>
      <version>0.12.6</version>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-impl</artifactId>
      <version>0.12.6</version>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-jackson</artifactId>
      <version>0.12.6</version>
    </dependency>

    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
  </dependencies>
  <build>
    <plugins>
      <plugin>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-maven-plugin</artifactId>
      </plugin>
    </plugins>
  </build>
</project>
```

---

### 1) Entity
`com.example.jwt.entity.UserEntity`
```java
package com.example.jwt.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "users")
public class UserEntity implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    private String fullName;

    @Column(unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole role;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
```

`com.example.jwt.entity.UserRole`
```java
package com.example.jwt.entity;
public enum UserRole { USER, ADMIN }
```

---

### 2) Models (DTO)
`com.example.jwt.model.AuthRequest`
```java
package com.example.jwt.model;
import lombok.Data;
@Data
public class AuthRequest { private String username; private String password; }
```

`com.example.jwt.model.RegisterRequest`
```java
package com.example.jwt.model;
import com.example.jwt.entity.UserRole;
import lombok.Data;
@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String fullName;
    private String email;
    private UserRole role = UserRole.USER;
}
```

`com.example.jwt.model.AuthResponse`
```java
package com.example.jwt.model;
import lombok.AllArgsConstructor; import lombok.Data;
@Data @AllArgsConstructor
public class AuthResponse { private String token; }
```

---

### 3) Repository & Service
`com.example.jwt.repo.UserRepository`
```java
package com.example.jwt.repo;
import com.example.jwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
}
```

`com.example.jwt.service.UserService`
```java
package com.example.jwt.service;
import com.example.jwt.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepo;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
```

`com.example.jwt.service.JwtService`
```java
package com.example.jwt.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
    @Value("${security.jwt.secret-key}")
    private String secret;

    @Value("${security.jwt.expiration-time}")
    private long expirationMs; // e.g. 3600000

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails user, Map<String, Object> extraClaims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSignKey())
                .compact();
    }

    public String generateToken(UserDetails user) {
        return generateToken(user, Map.of("authorities", user.getAuthorities()));
    }

    public String extractUsername(String token) {
        return Jwts.parser().verifyWith(getSignKey()).build()
                .parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        var payload = Jwts.parser().verifyWith(getSignKey()).build()
                .parseSignedClaims(token).getPayload();
        String username = payload.getSubject();
        Date exp = payload.getExpiration();
        return username.equals(user.getUsername()) && exp.after(new Date());
    }
}
```

---

### 4) Application Configuration
`com.example.jwt.config.ApplicationConfig`
```java
package com.example.jwt.config;

import com.example.jwt.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration @RequiredArgsConstructor
public class ApplicationConfig {
    private final UserService userService;

    @Bean public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

### 5) JWT Filter
`com.example.jwt.security.JwtAuthenticationFilter`
```java
package com.example.jwt.security;

import com.example.jwt.service.JwtService;
import com.example.jwt.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);
        String username;
        try { username = jwtService.extractUsername(token); }
        catch (Exception e) { filterChain.doFilter(request, response); return; }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userService.loadUserByUsername(username);
            if (jwtService.isTokenValid(token, userDetails)) {
                var authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}
```

---

### 6) SecurityConfig
`com.example.jwt.config.SecurityConfig`
```java
package com.example.jwt.config;

import com.example.jwt.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration @RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/login", "/profile", "/js/**", "/css/**").permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
```

---

### 7) REST Controllers
`com.example.jwt.controller.AuthRestController`
```java
package com.example.jwt.controller;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.model.AuthRequest;
import com.example.jwt.model.AuthResponse;
import com.example.jwt.model.RegisterRequest;
import com.example.jwt.repo.UserRepository;
import com.example.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {
    private final AuthenticationManager authManager;
    private final JwtService jwtService;
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest req) {
        if (userRepo.findByUsername(req.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        UserEntity user = UserEntity.builder()
                .username(req.getUsername())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .email(req.getEmail())
                .role(req.getRole())
                .build();
        userRepo.save(user);
        String token = jwtService.generateToken(user);
        return new AuthResponse(token);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        try {
            authManager.authenticate(new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username or password");
        }
        var user = userRepo.findByUsername(req.getUsername()).orElseThrow();
        return new AuthResponse(jwtService.generateToken(user));
    }
}
```

`com.example.jwt.controller.UserController`
```java
package com.example.jwt.controller;

import com.example.jwt.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController @RequiredArgsConstructor
public class UserController {
    private final UserRepository userRepo;

    @GetMapping("/users/me")
    public Object me(@AuthenticationPrincipal UserDetails me) { return me; }

    @GetMapping("/users")
    public Object all() { return List.copyOf(userRepo.findAll()); }
}
```

---

### 8) View Controllers (trình bày login.html, profile.html)
`com.example.jwt.controller.ViewController`
```java
package com.example.jwt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/profile") public String profile() { return "profile"; }
}
```

---

### 9) Templates (Thymeleaf)
`src/main/resources/templates/login.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8"><title>Login</title>
</head>
<body>
  <h1>Đăng nhập (AJAX)</h1>
  <input id="username" placeholder="Username" />
  <input id="password" type="password" placeholder="Password" />
  <button id="btnLogin">Login</button>
  <pre id="result"></pre>
  <script src="/js/mainjs.js"></script>
</body>
</html>
```

`src/main/resources/templates/profile.html`
```html
<!DOCTYPE html>
<html lang="vi" xmlns:th="http://www.thymeleaf.org">
<head>
  <meta charset="UTF-8"><title>Profile</title>
</head>
<body>
  <h1>Thông tin người dùng (JWT)</h1>
  <button id="btnMe">Tải /users/me</button>
  <button id="btnAll">Tải /users</button>
  <pre id="output"></pre>
  <script src="/js/mainjs.js"></script>
</body>
</html>
```

---

### 10) Static JS
`src/main/resources/static/js/mainjs.js`
```javascript
function saveToken(t){ localStorage.setItem('token', t); }
function getToken(){ return localStorage.getItem('token'); }

async function login(){
  const username = document.getElementById('username').value;
  const password = document.getElementById('password').value;
  const res = await fetch('/api/auth/login', {
    method: 'POST', headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username, password })
  });
  const data = await res.json();
  if (data.token){ saveToken(data.token); document.getElementById('result').textContent = 'TOKEN=' + data.token; }
  else { document.getElementById('result').textContent = JSON.stringify(data, null, 2); }
}

async function callMe(){
  const res = await fetch('/users/me', { headers: { 'Authorization': 'Bearer ' + getToken() }});
  document.getElementById('output').textContent = await res.text();
}

async function callAll(){
  const res = await fetch('/users', { headers: { 'Authorization': 'Bearer ' + getToken() }});
  document.getElementById('output').textContent = await res.text();
}

window.addEventListener('DOMContentLoaded', ()=>{
  const btnLogin = document.getElementById('btnLogin'); if(btnLogin) btnLogin.addEventListener('click', login);
  const btnMe = document.getElementById('btnMe'); if(btnMe) btnMe.addEventListener('click', callMe);
  const btnAll = document.getElementById('btnAll'); if(btnAll) btnAll.addEventListener('click', callAll);
});
```

---

### 11) Exception Handling (theo bảng lỗi trên slide)
`com.example.jwt.error.GlobalExceptionHandler`
```java
package com.example.jwt.error;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BadCredentialsException.class)
    public Map<String,Object> badCredential(BadCredentialsException e){
        return Map.of("status", 401, "error", "BadCredentials", "message", e.getMessage());
    }
    @ExceptionHandler(AccountStatusException.class)
    public Map<String,Object> accountStatus(AccountStatusException e){
        return Map.of("status", 403, "error", "AccountStatus", "message", e.getMessage());
    }
    @ExceptionHandler(AccessDeniedException.class)
    public Map<String,Object> accessDenied(AccessDeniedException e){
        return Map.of("status", 403, "error", "AccessDenied", "message", e.getMessage());
    }
    @ExceptionHandler(SignatureException.class)
    public Map<String,Object> invalidSignature(SignatureException e){
        return Map.of("status", 401, "error", "InvalidSignature", "message", e.getMessage());
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public Map<String,Object> expired(ExpiredJwtException e){
        return Map.of("status", 401, "error", "TokenExpired", "message", e.getMessage());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public Map<String,Object> illegalArg(IllegalArgumentException e){
        return Map.of("status", 400, "error", "BadRequest", "message", e.getMessage());
    }
}
```

---

### 12) Khởi tạo dữ liệu mẫu (tùy chọn)
`com.example.jwt.bootstrap.DataInit`
```java
package com.example.jwt.bootstrap;

import com.example.jwt.entity.UserEntity;
import com.example.jwt.entity.UserRole;
import com.example.jwt.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration @RequiredArgsConstructor
public class DataInit {
    private final PasswordEncoder encoder;

    @Bean CommandLineRunner clr(UserRepository repo){
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                repo.save(UserEntity.builder()
                        .username("admin")
                        .password(encoder.encode("123456"))
                        .fullName("Administrator")
                        .email("admin@example.com")
                        .role(UserRole.ADMIN)
                        .build());
            }
        };
    }
}
```

---

### 13) Test nhanh
- Đăng nhập:
```bash
curl -X POST http://localhost:8005/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"123456"}'
```
Kết quả trả về `{ "token": "..." }`.

- Gọi `/users/me` với Bearer token:
```bash
curl http://localhost:8005/users/me -H "Authorization: Bearer <TOKEN>"
```

- Gọi `/users` với Bearer token:
```bash
curl http://localhost:8005/users -H "Authorization: Bearer <TOKEN>"
```

> Đến đây bạn đã hoàn chỉnh flow: **Đăng ký/Đăng nhập → Nhận JWT → Gọi API bảo vệ bằng JWT → Xử lý Exception**.

