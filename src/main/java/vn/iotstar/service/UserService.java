package vn.iotstar.service;

import vn.iotstar.entity.Role;
import vn.iotstar.entity.Users;
import vn.iotstar.repository.RoleRepository;
import vn.iotstar.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public Users createUser(String username, String email, String rawPassword, String... roleNames) {
		if (userRepository.existsByUsername(username))
			throw new IllegalArgumentException("Username đã tồn tại");
		if (userRepository.existsByEmail(email))
			throw new IllegalArgumentException("Email đã tồn tại");

		Set<Role> roles = new HashSet<>();
		for (String rn : roleNames) {
			Optional<Role> r = roleRepository.findByName(rn);
			roles.add(r.orElseGet(() -> {
				Role newRole = new Role();
				newRole.setName(rn);
				return roleRepository.save(newRole);
			}));
		}

		Users u = new Users();
		u.setUsername(username);
		u.setEmail(email);
		u.setPassword(passwordEncoder.encode(rawPassword));
		u.setRoles(roles);
		return userRepository.save(u);
	}
}
