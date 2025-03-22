package com.example.sims.repository;

import com.example.sims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;



public interface  UserRepository extends JpaRepository<User,Long>{
     
    // Custom query to find user by email
    Optional<User> findByEmail(String email);
}