package vn.iotstar.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class Users implements java.io.Serializable {
	@Id
	@Column(name = "user_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(nullable = false, unique = true, length = 50, columnDefinition = "nvarchar(50)")
    private String username;

    @Column(nullable = false, unique = true, length = 120, columnDefinition = "nvarchar(120)")
    private String email;

	@Column(nullable = false)
	private String password;

	@Column(name = "name", length = 50, columnDefinition = "nvarchar(50) not null")
	private String name;

	@Column(name = "enabled")
	private boolean enabled;

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable(
		name = "users_roles",
		joinColumns = @JoinColumn(name = "user_id"),
		inverseJoinColumns = @JoinColumn(name = "role_id")
	)
	private Set<Role> roles = new HashSet<>();
}


