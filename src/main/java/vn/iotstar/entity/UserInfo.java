package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name", nullable = false, unique = true)
    private String name;
    
    @Column(name = "email")
    private String email;
    
    @Column(name = "password", nullable = false)
    private String password;
    
    @Column(name = "roles")
    private String roles;
}
