package com.colak.springtutorial;

import com.colak.springtutorial.jpa.Product;
import com.colak.springtutorial.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SpringTutorialApplication implements CommandLineRunner {

    private ProductService productService;

    public static void main(String[] args) {
        SpringApplication.run(SpringTutorialApplication.class, args);
    }

    @Autowired
    public void setProductService(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void run(String... args) {
        Product product = new Product();
        product.setId(1L);
        product.setName("Product 1");
        product.setDescription("Description of Product 1");
        product.setPrice(19.99);
        productService.indexProducts(List.of(product));
    }
}
