package com.rancard.ecommerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rancard.ecommerce.dto.ProductDto;
import com.rancard.ecommerce.model.User;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setup() {
        // Ensure test user exists before test runs
        if (userRepository.findByUsername("owner1").isEmpty()) {
            User user = new User();
            user.setUsername("owner1");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole("ROLE_OWNER");
            userRepository.save(user);
        }
    }

    @Test
    void shouldCreateProductWhenAuthenticatedAsOwner() throws Exception {
        ProductDto dto = new ProductDto();
        dto.setName("Test Product");
        dto.setPrice(BigDecimal.valueOf(199.99));
        dto.setQuantity(10);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Basic " + encodeBasicAuth("owner1", "password")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.price").value(199.99))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    // Helper method to encode Basic Auth header
    private String encodeBasicAuth(String username, String password) {
        return java.util.Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
    }
}
