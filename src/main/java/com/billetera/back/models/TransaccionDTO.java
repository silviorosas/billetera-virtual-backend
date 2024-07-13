package com.billetera.back.models;

import java.time.LocalDateTime;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TransaccionDTO {
   
    private Long id;    
    private Double monto;
    private LocalDateTime fecha;
    private String nombreUsuarioOrigen;
    private String nombreUsuarioDestino;
    private String tipoTransaccion; // Nuevo atributo para indicar si el dinero fue recibido o enviado

    // Constructor que acepta un objeto Transaccion y los nombres de usuario
    public TransaccionDTO(Transaccion transaccion, String nombreUsuarioOrigen, String nombreUsuarioDestino, String tipoTransaccion) {
        this.id = transaccion.getId();       
        this.monto = transaccion.getMonto();
        this.fecha = transaccion.getFecha();
        this.nombreUsuarioOrigen = nombreUsuarioOrigen;
        this.nombreUsuarioDestino = nombreUsuarioDestino;
        this.tipoTransaccion = tipoTransaccion;
    }
}

