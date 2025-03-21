package com.example.sims.service;

import com.example.sims.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {

        // Register a new user
        User registerUser(User user);

        // Find a user by email 
        Optional<User> findUserByEmail(String email); 

        // Get all users
        List<User> getAllUsers();

        // Delte a user by ID
        void deleteuser(Long userId);
    
} 