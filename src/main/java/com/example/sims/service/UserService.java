package com.example.sims.service;

import com.example.sims.model.User;
import com.example.sims.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {
        @Autowired

        private UserRepository userRepository;

        public User registerUser(User user){
                return userRepository.save(user);
        }
        
       public User loginUser(String email, String password){
             System.out.println("Email: " + email);
           return userRepository.findByEmail(email)
                  .filter(u -> u.getPassword().equals(password))
                  .orElse(null);
       }
} 