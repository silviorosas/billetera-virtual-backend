package com.billetera.back.exeptionHandler;

public class NumeroTarjetaInvalidoException extends IllegalArgumentException {
    public NumeroTarjetaInvalidoException() {
        super("El número de tarjeta debe tener exactamente 16 dígitos numéricos");
    } 
}
