package com.billetera.back.exeptionHandler;

public class TarjetaDebitoNotFoundException extends RuntimeException {

    public TarjetaDebitoNotFoundException(String message) {
        super(message);
    }
}
