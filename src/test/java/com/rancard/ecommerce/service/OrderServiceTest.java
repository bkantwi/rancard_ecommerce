package com.rancard.ecommerce.service;

import com.rancard.ecommerce.model.*;
import com.rancard.ecommerce.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private OrderRepository orderRepository;

    @InjectMocks private OrderService orderService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldPlaceOrderAndClearCart() {
        // Arrange
        String username = "customer1";
        User user = new User();
        user.setUsername(username);

        Product product = new Product();
        product.setPrice(BigDecimal.valueOf(100));

        CartItem item = new CartItem();
        item.setUser(user);
        item.setProduct(product);
        item.setQuantity(2);

        List<CartItem> cartItems = List.of(item);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUser(user)).thenReturn(cartItems);
        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Order order = orderService.placeOrder(username);

        // Assert
        assertEquals(user, order.getUser());
        assertEquals(1, order.getItems().size());
        assertEquals(BigDecimal.valueOf(200), order.getTotalAmount());

        verify(cartItemRepository).deleteAll(cartItems); // cart should be cleared
        verify(orderRepository).save(any(Order.class));  // order should be saved
    }
}
