package com.billetera.back.models;



import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaccion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoTransaccion tipo;

    private Double monto;
    private LocalDateTime fecha;

     // Atributos de tarjetas de origen y destino
     @JsonIgnore
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "tarjeta_origen_id", referencedColumnName = "id")
     private Tarjeta tarjetaOrigen;
 

     @JsonIgnore
     @ManyToOne(fetch = FetchType.LAZY)
     @JoinColumn(name = "tarjeta_destino_id", referencedColumnName = "id")
     private Tarjeta tarjetaDestino;

     private String usuarioOrigen;
    private String usuarioDestino;
    
}
