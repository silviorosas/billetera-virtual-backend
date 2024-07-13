package com.billetera.back.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billetera.back.models.Usuario;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario,Long> {
    
    Boolean existsByEmail(String email);
    
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findById(Long id);
}
