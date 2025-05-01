package com.rancard.ecommerce.service;

import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Loads user credentials from the database for Spring Security authentication.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // Map to Spring Security's User object
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword()) // BCrypt-encoded password
                .roles(user.getRole().replace("ROLE_", "")) // Strip ROLE_ prefix
                .build();
    }
}
