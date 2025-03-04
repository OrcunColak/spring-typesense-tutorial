package com.colak.springtutorial.controller;

import com.colak.springtutorial.jpa.Product;
import com.colak.springtutorial.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET http://localhost:8080/api/products/search?query=product
    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String query) {

        List<Product> products = productService.searchProducts(query);

        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(products);
    }
}
