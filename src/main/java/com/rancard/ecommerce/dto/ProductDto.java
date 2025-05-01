package com.rancard.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDto {

    private String name;
    private BigDecimal price;
    private int quantity;

    // ✅ Add this constructor
    public ProductDto(String name, BigDecimal price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    // Optional: no-args constructor (needed for JSON or frameworks)
    public ProductDto() {}
}
