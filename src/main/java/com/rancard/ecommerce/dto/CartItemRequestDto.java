package com.rancard.ecommerce.dto;

import lombok.Data;

@Data
public class CartItemRequestDto {
    private Long productId;
    private int quantity;
}
