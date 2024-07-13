package com.billetera.back.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.billetera.back.models.Tarjeta;
import com.billetera.back.models.TarjetaInfoDTO;
import com.billetera.back.services.TarjetaService;

@RestController
@RequestMapping("/api/tarjeta")
@CrossOrigin("*")
public class TarjetaController {

    @Autowired
    private TarjetaService tarjetaService;

    @PostMapping("/usuario/{usuarioId}")
    public ResponseEntity<Tarjeta> agregarTarjeta(@RequestBody Tarjeta tarjetaDebito, @PathVariable Long usuarioId) {
        Tarjeta nuevaTarjeta = tarjetaService.agregarTarjetaDebito(tarjetaDebito, usuarioId);
        return new ResponseEntity<>(nuevaTarjeta, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarjetaInfoDTO> obtenerInfoTarjeta(@PathVariable Long id) {
        TarjetaInfoDTO tarjetaInfo = tarjetaService.obtenerInfoTarjeta(id);
        return ResponseEntity.ok(tarjetaInfo);
    }
    
}
