package com.rancard.ecommerce.service;

import com.rancard.ecommerce.dto.ProductDto;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.ProductRepository;
import com.rancard.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public Product createProduct(ProductDto dto, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        Product product = new Product(null, dto.getName(), dto.getPrice(), dto.getQuantity(), user);
        Product saved = productRepository.save(product);
        notifyClients(saved);
        return saved;
    }

    public List<Product> getAll() {
        return productRepository.findAll();
    }

    public SseEmitter subscribe() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        return emitter;
    }

    private void notifyClients(Product product) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event().name("product-update").data(product));
            } catch (Exception e) {
                emitters.remove(emitter);
            }
        }
    }

    public Product updateProduct(Long id, ProductDto dto, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Optional: Only allow the product owner to update
        if (!product.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setQuantity(dto.getQuantity());

        return productRepository.save(product);
    }

    public void deleteProduct(Long id, String username) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getOwner().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        productRepository.delete(product);
    }

}
