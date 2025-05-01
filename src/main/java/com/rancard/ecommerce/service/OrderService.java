package com.rancard.ecommerce.service;

import com.rancard.ecommerce.model.*;
import com.rancard.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderRepository orderRepository;

    public Order placeOrder(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order();
        order.setUser(user);
        order.setItems(cartItems);
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        cartItemRepository.deleteAll(cartItems); // clear cart
        return saved;
    }
}
