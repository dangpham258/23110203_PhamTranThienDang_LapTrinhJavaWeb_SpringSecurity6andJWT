package vn.iotstar.service;

import vn.iotstar.entity.Product;
import vn.iotstar.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> findAll() { return productRepository.findAll(); }

	public Product findById(Long id) { return productRepository.findById(id).orElse(null); }

	public Product save(Product p) { return productRepository.save(p); }

	public void deleteById(Long id) { productRepository.deleteById(id); }
}


