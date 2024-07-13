package com.billetera.back.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billetera.back.models.Tarjeta;
import com.billetera.back.services.UsuarioService;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UsuarioService userService;

    @GetMapping("/{id}/tarjetas")
    public ResponseEntity<List<Tarjeta>> obtenerTarjetasPorUsuarioId(@PathVariable Long id) {
        List<Tarjeta> tarjetas = userService.obtenerTarjetasPorUsuarioId(id);
        if (tarjetas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(tarjetas);
    }
}

