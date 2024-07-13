package com.billetera.back.exeptionHandler;

public class TarjetaDebitoExistenteException extends RuntimeException {

    public TarjetaDebitoExistenteException() {
        super("La tarjeta ya está vinculada a un usuario");
    }
}

