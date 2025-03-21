package com.example.sims.repository;

import com.example.sims.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;



@Repository

public interface  UserRepository extends JpaRepository<User,Long>{
     
    // Custom query to find user by email
    Optional<User> findByEmail(String email);

    // Check if email is already exists
    boolean existsByEmail(String email);
}