package vn.iotstar.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Users;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;
import vn.iotstar.repository.UserRepository;

@Service
public class AuthenticationService {
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;

	public AuthenticationService(UserRepository userRepository,
								AuthenticationManager authenticationManager,
								PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.authenticationManager = authenticationManager;
		this.passwordEncoder = passwordEncoder;
	}

	public Users signup(RegisterUserModel input) {
		Users user = new Users();
		user.setName(input.getFullName());
		user.setEmail(input.getEmail());
		user.setUsername(input.getEmail());
		user.setEnabled(true);
		user.setPassword(passwordEncoder.encode(input.getPassword()));
		return userRepository.save(user);
	}

	public Users authenticate(LoginUserModel input) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						input.getEmail(),
						input.getPassword()
				)
		);
		return userRepository.findByEmail(input.getEmail()).orElseThrow();
	}
}


