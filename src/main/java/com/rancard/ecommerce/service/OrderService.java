package com.rancard.ecommerce.service;

import com.rancard.ecommerce.dto.ProductInsightDto;
import com.rancard.ecommerce.model.*;
import com.rancard.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OrderService {

    @Autowired private UserRepository userRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderRepository orderRepository;

    /**
     * Places an order for the authenticated user by converting all their cart items into an order.
     * Clears the cart afterward.
     */
    public Order placeOrder(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) throw new RuntimeException("Cart is empty");

        // Calculate total amount
        BigDecimal total = cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Build and save the order
        Order order = new Order();
        order.setUser(user);
        order.setItems(cartItems);
        order.setTotalAmount(total);
        order.setCreatedAt(LocalDateTime.now());

        Order saved = orderRepository.save(order);

        // Clear the cart after order is placed
        cartItemRepository.deleteAll(cartItems);
        return saved;
    }

    /**
     * Generates insight reports based on all orders placed.
     * For each product, aggregates total quantity sold and total revenue.
     */
    public List<ProductInsightDto> getInsights() {
        List<Order> orders = orderRepository.findAll();
        Map<String, ProductInsightDto> metrics = new HashMap<>();

        for (Order order : orders) {
            for (CartItem item : order.getItems()) {
                String name = item.getProduct().getName();
                int quantity = item.getQuantity();
                BigDecimal revenue = item.getProduct().getPrice().multiply(BigDecimal.valueOf(quantity));

                // Aggregate metrics per product
                metrics.compute(name, (k, v) -> {
                    if (v == null) {
                        return new ProductInsightDto(name, quantity, revenue);
                    } else {
                        v.setTotalSold(v.getTotalSold() + quantity);
                        v.setTotalRevenue(v.getTotalRevenue().add(revenue));
                        return v;
                    }
                });
            }
        }

        return new ArrayList<>(metrics.values());
    }
}
