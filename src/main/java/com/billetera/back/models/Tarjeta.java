package com.billetera.back.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String numeroTarjeta;
    private String banco;
    private Double saldo;
    

    // Relación con Usuario
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private Usuario usuario;

   

    public Tarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }
    
}
