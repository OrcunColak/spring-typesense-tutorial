package com.colak.springtutorial.service;

import com.colak.springtutorial.jpa.Product;
import com.colak.springtutorial.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final GenericSearchService<Product> searchService;

    private final ProductRepository productRepository;

    @Transactional
    public List<Product> searchProducts(String searchTerm) {
        try {
            List<Map<String, Object>> searchedContent = searchProductsInternal(searchTerm);

            if (searchedContent.isEmpty()) {
                return Collections.emptyList();
            }

            List<Long> productIds = searchedContent.stream()
                    .map(doc -> doc.get("id"))
                    .filter(Objects::nonNull)
                    .map(id -> {
                        try {
                            return Long.parseLong(id.toString());
                        } catch (NumberFormatException e) {
                            log.error("Invalid id format found: " + id);
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return productIds.isEmpty() ? Collections.emptyList() : productRepository.findAllById(productIds);

        } catch (Exception e) {
            log.error("Error searching products: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void indexProducts(List<Product> products) {
        searchService.indexDocuments("products_collection", products);
    }

    private List<Map<String, Object>> searchProductsInternal(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return Collections.emptyList();
        }
        return searchService.search(searchTerm, "products_collection");
    }
}
