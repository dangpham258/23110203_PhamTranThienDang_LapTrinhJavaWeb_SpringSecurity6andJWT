## Demo 2 – Spring Security với Database

### Bước 1: Khởi tạo database
```sql
CREATE DATABASE springb3_security6;
CREATE USER 'security_su'@'localhost' IDENTIFIED BY '1234567@a$';
GRANT ALL PRIVILEGES ON springb3_security6.* TO 'security_su'@'localhost';
```

---

### Bước 2: Kết nối Spring Boot và database
Trong file `application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/springb3_security6
spring.datasource.username=security_su
spring.datasource.password=1234567@a$
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Thêm dependency vào `pom.xml`:
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
</dependency>
```

---

### Bước 3: Khởi tạo entity
Thêm dependency JPA:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```

Tạo entity `UserInfo`:
```java
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String email;
    private String password;
    private String roles;
}
```

---

### Bước 4: Khởi tạo repository
```java
@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, Integer> {
    Optional<UserInfo> findByName(String username);
}
```

---

### Bước 5: Khởi tạo service
```java
@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserInfoRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> userInfo = repository.findByName(username);
        return userInfo.map(UserInfoUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));
    }
}
```

---

### Bước 6: Convert UserInfo sang UserDetails
```java
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoUserDetails implements UserDetails {
    private String name;
    private String password;
    private List<GrantedAuthority> authorities;

    public UserInfoUserDetails(UserInfo userInfo) {
        name = userInfo.getName();
        password = userInfo.getPassword();
        authorities = Arrays.stream(userInfo.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return name; }
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

---

### Bước 7: Thêm user vào database
Tạo service `UserService`:
```java
@Service
public record UserService(UserInfoRepository repository, PasswordEncoder passwordEncoder) {
    public String addUser(UserInfo userInfo) {
        userInfo.setPassword(passwordEncoder.encode(userInfo.getPassword()));
        repository.save(userInfo);
        return "Thêm user thành công!";
    }
}
```

Tạo controller `UserController`:
```java
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/new")
    public String addUser(@RequestBody UserInfo userInfo) {
        return userService.addUser(userInfo);
    }
}
```

---

### Bước 8: Cấu hình SecurityConfig
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserInfoRepository repository;

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserInfoService(repository);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/new").permitAll()
                        .requestMatchers("/customer/**").authenticated()
                )
                .formLogin(Customizer.withDefaults())
                .build();
    }
}
```

---

👉 Kết quả: 
- Gọi API `/user/new` bằng Postman để thêm user mới.
- Sau khi thêm user, có thể đăng nhập và truy cập `/customer/**` theo phân quyền đã lưu trong database.

