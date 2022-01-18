package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Coupon;
import com.example.model.Product;
import com.example.repo.ProductRepository;
import com.example.restclient.CouponClient;

import io.github.resilience4j.retry.annotation.Retry;

@RestController
@RequestMapping("/productapi")
@RefreshScope
public class ProductRestController {
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CouponClient couponClient;

	@Value("${com.saipriya.springcloud.prep}")
	private String prop;

	@PostMapping("/products")
	@Retry(name = "product-api", fallbackMethod = "handleError")
	public Product saveProduct(@RequestBody Product product) {
		Coupon coupon = couponClient.getcoupon(product.getCouponCode());
		product.setPrice(product.getPrice().subtract(coupon.getDiscount()));
		return productRepository.save(product);
	}

	@GetMapping("/prop")
	public String getProp() {
		return this.prop;
	}

	public Product handleError(Product product, Exception exception) {
		System.out.println("handle error");
		return product;
	}
}
