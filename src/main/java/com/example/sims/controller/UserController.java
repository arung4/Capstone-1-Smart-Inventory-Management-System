package com.example.sims.controller;

import com.example.sims.dto.LoginRequest;
import com.example.sims.model.Response;
import com.example.sims.util.JwtUtil;
import com.example.sims.model.User;
import com.example.sims.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


//@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public Response<User> registerUser(@RequestBody User user) {
        User registeredUser = userService.registerUser(user);
        return new Response<>(HttpStatus.CREATED.value(), "User registered successfully", registeredUser);
    }

    @PostMapping("/login")
    public Response<String> loginUser(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = userService.loginUser(loginRequest.getEmail(), loginRequest.getPassword());
        if (userDetails != null) {
            String token = jwtUtil.generateToken(userDetails);
            return new Response<>(200, "Login successful", token);
        } else {
            return new Response<>(401, "Invalid email or password", null);
        }
    }
}