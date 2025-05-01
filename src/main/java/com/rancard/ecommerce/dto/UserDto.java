package com.rancard.ecommerce.dto;

import lombok.Data;

@Data
public class UserDto {
    private String username;
    private String password;
    private String role; // e.g., ROLE_USER or ROLE_CUSTOMER
}
