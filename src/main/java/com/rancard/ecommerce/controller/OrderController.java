package com.rancard.ecommerce.controller;

import com.rancard.ecommerce.dto.ProductInsightDto;
import com.rancard.ecommerce.model.Order;
import com.rancard.ecommerce.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Order placeOrder(Authentication auth) {
        return orderService.placeOrder(auth.getName());
    }

    @GetMapping("/insights")
    public List<ProductInsightDto> getInsights() {
        return orderService.getInsights();
    }
}
