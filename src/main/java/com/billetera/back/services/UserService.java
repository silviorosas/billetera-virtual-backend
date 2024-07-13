package com.billetera.back.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.billetera.back.config.JwtTokenProvider;
import com.billetera.back.models.LoginDto;
import com.billetera.back.models.Role;
import com.billetera.back.models.User;
import com.billetera.back.models.UserDTO;
import com.billetera.back.models.UserResponse;
import com.billetera.back.repository.*;

@Service
public class UserService {

     @Autowired
    private UserRepository usuarioRepository;

     @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
     JwtTokenProvider jwtTokenProvider;

    public User crearUsuario(UserDTO usuarioDTO) {
        User usuario = User.builder()
            .nombre(usuarioDTO.getNombre())
            .apellido(usuarioDTO.getApellido())
            .email(usuarioDTO.getEmail())
            .password(passwordEncoder.encode(usuarioDTO.getPassword()))
            .role(Role.ROLE_USER)
            .build();
        
        return usuarioRepository.save(usuario);
    }
    

    public UserResponse login(LoginDto loginDto){
        Authentication authentication = null;
        authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
       

        return UserResponse.builder()
                .responseCode("Loggin Success")
                .responseMessage(jwtTokenProvider.generateToken(authentication))
                .build();
}
}
