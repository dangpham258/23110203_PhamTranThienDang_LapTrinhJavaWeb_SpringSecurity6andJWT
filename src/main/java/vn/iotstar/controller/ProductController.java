package vn.iotstar.controller;

import vn.iotstar.entity.Product;
import vn.iotstar.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/products")
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	public String list(Model model) {
		model.addAttribute("products", productService.findAll());
		return "index";
	}

	@GetMapping("/new")
	@PreAuthorize("hasRole('ADMIN')")
	public String createForm(Model model) {
		model.addAttribute("product", new Product());
		return "new_product";
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public String save(@Valid @ModelAttribute("product") Product product,
					 BindingResult result) {
		if (result.hasErrors()) return "new_product";
		productService.save(product);
		return "redirect:/";
	}

	@GetMapping("/edit/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String edit(@PathVariable Long id, Model model) {
		Product p = productService.findById(id);
		if (p == null) return "redirect:/";
		model.addAttribute("product", p);
		return "edit_product";
	}

	@PostMapping("/edit/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String update(@PathVariable Long id,
					  @Valid @ModelAttribute("product") Product product,
					  BindingResult result) {
		if (result.hasErrors()) return "edit_product";
		product.setId(id);
		productService.save(product);
		return "redirect:/";
	}

	@PostMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String delete(@PathVariable Long id) {
		productService.deleteById(id);
		return "redirect:/";
	}
}


