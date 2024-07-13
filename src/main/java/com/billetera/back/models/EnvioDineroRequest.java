package com.billetera.back.models;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EnvioDineroRequest {
    
    private Long usuarioId; 
    private String numeroTarjetaOrigen;
    private String numeroTarjetaDestino;
    private double monto;
    private String usuarioOrigen;
    private String usuarioDestino;
}