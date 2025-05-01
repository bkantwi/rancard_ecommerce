package com.rancard.ecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class ProductInsightDto {
    private String productName;
    private int totalSold;
    private BigDecimal totalRevenue;
}
