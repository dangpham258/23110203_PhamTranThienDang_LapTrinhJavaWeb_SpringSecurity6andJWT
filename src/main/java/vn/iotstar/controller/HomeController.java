package vn.iotstar.controller;

import vn.iotstar.service.ProductService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	private final ProductService productService;

	public HomeController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping({"/", "/index"})
	public String index(Authentication auth, Model model) {
		model.addAttribute("products", productService.findAll());
		model.addAttribute("username", auth != null ? auth.getName() : "");
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/403")
	public String accessDenied() {
		return "403";
	}
}


