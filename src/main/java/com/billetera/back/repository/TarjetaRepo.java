package com.billetera.back.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billetera.back.models.Tarjeta;
import com.billetera.back.models.Usuario;

public interface TarjetaRepo extends JpaRepository<Tarjeta,Long>{

    boolean existsByUsuarioAndNumeroTarjeta(Usuario usuario, String numeroTarjeta);

    boolean existsByNumeroTarjeta(String numeroTarjeta);

    Optional<Tarjeta> findByNumeroTarjeta(String numeroTarjeta);

    List<Tarjeta> findByUsuarioId(Long usuarioId);
    
}
