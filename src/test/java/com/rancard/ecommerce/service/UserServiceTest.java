package com.rancard.ecommerce.service;

import com.rancard.ecommerce.dto.UserDto;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldRegisterUserWithEncodedPassword() {
        // Arrange
        UserDto dto = new UserDto();
        dto.setUsername("johndoe");
        dto.setPassword("plain123");
        dto.setRole("ROLE_CUSTOMER");

        // Mock behavior
        when(passwordEncoder.encode("plain123")).thenReturn("encodedPass");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        User result = userService.register(dto);

        // Assert
        assertEquals("johndoe", result.getUsername());
        assertEquals("encodedPass", result.getPassword());
        assertEquals("ROLE_CUSTOMER", result.getRole());

        verify(passwordEncoder).encode("plain123");
        verify(userRepository).save(any(User.class));
    }
}
