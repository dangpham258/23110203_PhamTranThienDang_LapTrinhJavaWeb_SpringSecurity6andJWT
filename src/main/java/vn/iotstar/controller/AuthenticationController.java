package vn.iotstar.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import vn.iotstar.models.LoginResponse;
import vn.iotstar.models.LoginUserModel;
import vn.iotstar.models.RegisterUserModel;
import vn.iotstar.entity.Users;
import vn.iotstar.service.AuthenticationService;
import vn.iotstar.services.JwtService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

	private final JwtService jwtService;
	private final AuthenticationService authenticationService;
	private final UserDetailsService userDetailsService;

	public AuthenticationController(JwtService jwtService,
									AuthenticationService authenticationService,
									UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.authenticationService = authenticationService;
		this.userDetailsService = userDetailsService;
	}

	@PostMapping("/signup")
	@Transactional
	public ResponseEntity<Users> register(@RequestBody RegisterUserModel registerUser) {
		Users registeredUser = authenticationService.signup(registerUser);
		return ResponseEntity.ok(registeredUser);
	}

	@PostMapping("/login")
	@Transactional
	public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserModel loginUser) {
		Users authenticatedUser = authenticationService.authenticate(loginUser);
		UserDetails userDetails = userDetailsService.loadUserByUsername(authenticatedUser.getEmail());
		String jwtToken = jwtService.generateToken(userDetails);
		LoginResponse loginResponse = new LoginResponse(jwtToken, jwtService.getExpirationTime());
		return ResponseEntity.ok(loginResponse);
	}
}


