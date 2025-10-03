# Demo 3: Spring Security 6 với Thymeleaf

## Các bước thực hiện

### Bước 1: Tạo project Spring Boot 3 với các dependency
*(giữ nguyên như trước)*

### Bước 2: Cấu hình `application.properties`
*(giữ nguyên như trước)*

### Bước 3: Tạo các Entity
*(Users, Role, Product – giữ nguyên như đã viết)*

### Bước 4: Tạo Models (DTO)
*(LoginDto, SignUpDto – giữ nguyên như đã viết)*

### Bước 5: Tạo Services
*(ProductService, ProductServiceImpl, CustomUserDetailsService – giữ nguyên như đã viết)*

### Bước 6: Cấu hình Security

#### File `WebSecurityConfig.java`
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        final List<GlobalAuthenticationConfigurerAdapter> configurers = new ArrayList<>();
        configurers.add(new GlobalAuthenticationConfigurerAdapter() {
            @Override
            public void configure(AuthenticationManagerBuilder auth) throws Exception {
                // custom authentication config nếu cần
            }
        });
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/").hasAnyAuthority("USER", "ADMIN", "EDITOR", "CREATOR")
                .requestMatchers("/new").hasAnyAuthority("ADMIN", "CREATOR")
                .requestMatchers("/edit/**").hasAnyAuthority("ADMIN", "EDITOR")
                .requestMatchers("/delete/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                .requestMatchers("/api/**").permitAll()
                .anyRequest().authenticated()
            )
            .httpBasic(withDefaults())
            .formLogin(login -> login.loginPage("/login").permitAll())
            .logout(logout -> logout.permitAll())
            .exceptionHandling(handling -> handling.accessDeniedPage("/403"))
            .build();
    }
}
```

#### File `MvcConfig.java`
```java
@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/403").setViewName("403");
        registry.addViewController("/login").setViewName("login");
    }
}
```

### Bước 7: Tạo Controller
- `AuthController`: xử lý login/register.
- `ProductController`: CRUD sản phẩm với phân quyền.

### Bước 8: Tạo Views
- `index.html`: Trang chủ, hiển thị danh sách sản phẩm.
- `login.html`: Form đăng nhập.
- `new_product.html`: Form thêm sản phẩm.
- `edit_product.html`: Form chỉnh sửa sản phẩm.
- `403.html`: Trang lỗi quyền truy cập.

---
👉 Đến đây demo Spring Security 6 + Thymeleaf đã đầy đủ: Entity, DTO, Service, Repository, Config, Controller, View và phân quyền theo role.

