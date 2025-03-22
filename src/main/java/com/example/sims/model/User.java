package com.example.sims.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")

public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

   private String name;
   private String email;
   private String password;
   private String phoneNumber;
   private String role; // ADMIN OR STAFF
}
