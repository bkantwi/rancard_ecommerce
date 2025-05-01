package com.rancard.ecommerce.controller;

import com.rancard.ecommerce.dto.UserDto;
import com.rancard.ecommerce.model.User;
import com.rancard.ecommerce.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User register(@RequestBody UserDto userDto) {
        return userService.register(userDto);
    }

//    @GetMapping("/test")
//    public String test() {
//        return "Public test OK";
//    }
}
