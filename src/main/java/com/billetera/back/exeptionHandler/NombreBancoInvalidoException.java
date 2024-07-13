package com.billetera.back.exeptionHandler;

public class NombreBancoInvalidoException extends RuntimeException {

    public NombreBancoInvalidoException(){
        super("El nombre del banco no puede estar vacío y solo acepta letras");
    }
    
}
