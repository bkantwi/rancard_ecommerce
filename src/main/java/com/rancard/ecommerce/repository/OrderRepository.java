package com.rancard.ecommerce.repository;

import com.rancard.ecommerce.model.Order;
import com.rancard.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);
}
