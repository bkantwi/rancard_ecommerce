package com.rancard.ecommerce.controller;

import com.rancard.ecommerce.dto.CartItemRequestDto;
import com.rancard.ecommerce.model.CartItem;
import com.rancard.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping
    public CartItem addToCart(@RequestBody CartItemRequestDto dto, Authentication auth) {
        return cartService.addToCart(dto.getProductId(), dto.getQuantity(), auth.getName());
    }
}
