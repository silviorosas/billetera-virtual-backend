package com.billetera.back.exeptionHandler;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException() {
        super("Usuario no encontrado");
    }
}
