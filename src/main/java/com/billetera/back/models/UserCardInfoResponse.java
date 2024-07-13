package com.billetera.back.models;

import lombok.*;

@Getter
@Setter
public class UserCardInfoResponse {
    private String nombre;
    private String apellido;
    private String banco;

    // para metodo obtener info por numero de tarjeta
}
