package com.billetera.back.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PruebaController {

    @GetMapping
    public String prueba(){
        return "Hola Billetera";
    }
    
}
