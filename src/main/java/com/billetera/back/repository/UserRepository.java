package com.billetera.back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billetera.back.models.User;



public interface UserRepository extends JpaRepository<User,Long>{

    Boolean existsByEmail (String email);    

    Optional<User> findByEmail(String email);
    
}
