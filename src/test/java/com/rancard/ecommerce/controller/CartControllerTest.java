package com.rancard.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.ProductRepository;
import com.rancard.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    private Long productId;

    @BeforeEach
    void setup() {
        // Create a customer user
        if (userRepository.findByUsername("customer1").isEmpty()) {
            User user = new User();
            user.setUsername("customer1");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_CUSTOMER");
            userRepository.save(user);
        }

        // Create a product to add to cart
        if (productRepository.findAll().isEmpty()) {
            Product product = new Product();
            product.setName("Cart Product");
            product.setPrice(BigDecimal.valueOf(150));
            product.setQuantity(20);
            product.setOwner(userRepository.findByUsername("customer1").get());
            productRepository.save(product);
        }

        productId = productRepository.findAll().get(0).getId();
    }

    @Test
    void shouldAddProductToCartAsCustomer() throws Exception {
        // Prepare payload
        Map<String, Object> body = Map.of(
                "productId", productId,
                "quantity", 2
        );

        mockMvc.perform(post("/cart")
                        .header("Authorization", "Basic " + encodeBasicAuth("customer1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value(productId))
                .andExpect(jsonPath("$.quantity").value(2));
    }

    private String encodeBasicAuth(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
