package com.billetera.back.controller;



import javax.naming.InvalidNameException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.billetera.back.models.LoginDto;

import com.billetera.back.models.UserDTO;
import com.billetera.back.models.UserResponse;
import com.billetera.back.models.Usuario;

import com.billetera.back.services.UsuarioService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    UsuarioService userService;

    @PostMapping
    public ResponseEntity<Usuario> register(@RequestBody UserDTO userDTO) throws InvalidNameException{
        Usuario usuario = userService.registerUsuario(userDTO);
        return ResponseEntity.ok().body(usuario);
    }

    @PostMapping("/login")
    public UserResponse login(@RequestBody LoginDto loginDto){
        return userService.login(loginDto);
    }
    
    
}
