package com.rancard.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.CartItemRepository;
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
import java.util.Base64;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private ObjectMapper objectMapper;

    private Long productId;

    @BeforeEach
    void setup() {
        // Create a customer
        userRepository.findByUsername("customer1").orElseGet(() -> {
            User user = new User();
            user.setUsername("customer1");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_CUSTOMER");
            return userRepository.save(user);
        });

        // Create a product owner
        User owner = userRepository.findByUsername("owner1").orElseGet(() -> {
            User user = new User();
            user.setUsername("owner1");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_OWNER");
            return userRepository.save(user);
        });

        // Create product if needed
        if (productRepository.findAll().isEmpty()) {
            Product product = new Product();
            product.setName("Order Product");
            product.setPrice(BigDecimal.valueOf(250));
            product.setQuantity(50);
            product.setOwner(owner);
            productRepository.save(product);
        }

        productId = productRepository.findAll().get(0).getId();
    }

    @Test
    void shouldPlaceOrderAsCustomer() throws Exception {
        // First: Add item to cart
        Map<String, Object> body = Map.of("productId", productId, "quantity", 3);
        mockMvc.perform(post("/cart")
                        .header("Authorization", "Basic " + encode("customer1", "password"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk());

        // Then: Place order
        mockMvc.perform(post("/orders")
                        .header("Authorization", "Basic " + encode("customer1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.totalAmount").value(750.0));
    }

    @Test
    void shouldReturnProductInsightsAsOwner() throws Exception {
        mockMvc.perform(get("/orders/insights")
                        .header("Authorization", "Basic " + encode("owner1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNumber());
    }

    private String encode(String username, String password) {
        return Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
