package com.rancard.ecommerce.service;

import com.rancard.ecommerce.dto.ProductDto;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.ProductRepository;
import com.rancard.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    // Thread-safe list to store active SSE connections
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    /**
     * Create a new product, associate it with the user, save to DB, and notify subscribers.
     */
    public Product createProduct(ProductDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = new Product(null, dto.getName(), dto.getPrice(), dto.getQuantity(), user);
        Product saved = productRepository.save(product);
        notifyClients(saved); // Send real-time update
        return saved;
    }

    /**
     * Retrieve all products (cached using Caffeine).
     */
    @Cacheable("products")
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    /**
     * Subscribe a client to product updates using Server-Sent Events (SSE).
     */
    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        // Clean up disconnected emitters
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));

        return emitter;
    }

    /**
     * Notify all SSE subscribers about a new product.
     */
    private void notifyClients(Product product) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("product-update").data(product));
            } catch (Exception e) {
                emitters.remove(emitter); // Remove failed connections
            }
        }
    }

    /**
     * Update a product's details and clear cache to keep data fresh.
     */
    @CacheEvict(value = "products", allEntries = true)
    public Product updateProduct(Long id, ProductDto dto, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Only the product owner can update
        if (!product.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());

        return productRepository.save(product);
    }

    /**
     * Delete a product (only by its owner).
     */
    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        productRepository.delete(product);
    }
}
