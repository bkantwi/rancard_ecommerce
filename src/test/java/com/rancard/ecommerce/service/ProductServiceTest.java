package com.rancard.ecommerce.service;

import com.rancard.ecommerce.dto.ProductDto;
import com.rancard.ecommerce.model.Product;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.ProductRepository;
import com.rancard.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks private ProductService productService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateProductForUser() {
        // Arrange
        String username = "owner1";
        ProductDto dto = new ProductDto("Laptop", BigDecimal.valueOf(1000), 5);
        User user = new User(1L, "owner1", "password", "ROLE_OWNER");

        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user));
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Product product = productService.createProduct(dto, username);

        // Assert
        assertEquals("Laptop", product.getName());
        assertEquals(5, product.getQuantity());
        assertEquals(user, product.getOwner());
    }
}
