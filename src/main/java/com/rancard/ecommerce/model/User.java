package com.rancard.ecommerce.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users") // ✅ avoids SQL keyword conflict
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String role;
}

