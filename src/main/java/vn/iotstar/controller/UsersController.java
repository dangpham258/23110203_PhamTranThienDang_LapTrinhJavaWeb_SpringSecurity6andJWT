package vn.iotstar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.iotstar.entity.Users;
import vn.iotstar.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

	private final UserRepository userRepository;

	public UsersController(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@GetMapping("/me")
	public ResponseEntity<Users> authenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		String username;
		if (principal instanceof UserDetails userDetails) {
			username = userDetails.getUsername();
		} else {
			username = principal.toString();
		}
		Users current = userRepository.findByEmail(username).orElse(null);
		return ResponseEntity.ok(current);
	}

	@GetMapping
	public ResponseEntity<List<Users>> allUsers() {
		return ResponseEntity.ok(userRepository.findAll());
	}
}


