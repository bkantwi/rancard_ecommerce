package com.rancard.ecommerce.controller;

import com.rancard.ecommerce.dto.ProductDto;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public Product create(@RequestBody ProductDto dto, Authentication auth) {
        return productService.createProduct(dto, auth.getName());
    }

    @GetMapping
    public List<Product> getAll() {
        return productService.getAll();
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody ProductDto dto, Authentication auth) {
        return productService.updateProduct(id, dto, auth.getName());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        productService.deleteProduct(id, auth.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/stream")
    public SseEmitter stream() {
        return productService.subscribe();
    }
}
