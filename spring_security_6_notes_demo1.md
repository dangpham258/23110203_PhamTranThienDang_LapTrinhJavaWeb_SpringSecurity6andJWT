## Demo 1 – Cài đặt & Cấu hình Spring Security

### Bước 2: Tạo Model với Lombok
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class Customer {
    private String id;
    private String name;
    private String phoneNumber;
    private String email;
}
```
Nếu chưa cài đặt được Lombok, bạn có thể tự tạo Getters, Setters, Constructor, ToString.

---

### Bước 3: Tạo Controller & phân quyền
```java
@RestController
@EnableMethodSecurity
public class CustomerController {
    final private List<Customer> customers = List.of(
            Customer.builder().id("001").name("Nguyễn Hữu Trung").email("trunghspkt@gmail.com").build(),
            Customer.builder().id("002").name("Hữu Trung").email("trunghuu@gmail.com").build()
    );

    @GetMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("hello is Guest");
    }

    @GetMapping("/customer/all")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<Customer>> getCustomerList() {
        List<Customer> list = this.customers;
        return ResponseEntity.ok(list);
    }

    @GetMapping("/customer/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Customer> getCustomerList(@PathVariable("id") String id) {
        List<Customer> customers = this.customers.stream()
                .filter(customer -> customer.getId().equals(id))
                .toList();
        return ResponseEntity.ok(customers.get(0));
    }
}
```
- `hello()` không yêu cầu phân quyền → ai cũng truy cập được.
- `getCustomerList()` yêu cầu quyền `ROLE_ADMIN`.
- `getCustomerById()` yêu cầu quyền `ROLE_USER`.

---

### Bước 4: Tạo user mẫu & SecurityConfig
Trong `application.properties`:
```properties
spring.security.user.name=trung
spring.security.user.password=123
```

Tạo class cấu hình:
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.withUsername("trung")
                .password(encoder.encode("123"))
                .roles("ADMIN")
                .build();

        UserDetails user = User.withUsername("user")
                .password(encoder.encode("123"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

---

### Bước 5: Cấu hình SecurityFilterChain
```java
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/hello").permitAll()
                    .requestMatchers("/customer/**").authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .build();
}
```
- `/hello` → ai cũng truy cập.
- `/customer/**` → yêu cầu đăng nhập trước khi truy cập.

---

👉 Kết quả: khi chạy chương trình, truy cập `http://localhost:8080/hello` sẽ redirect sang trang login. Đăng nhập với user đã cấu hình để xem kết quả.

