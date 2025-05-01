package com.rancard.ecommerce.repository;

import com.rancard.ecommerce.model.CartItem;
import com.rancard.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUser(User user);
}
