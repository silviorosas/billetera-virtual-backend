package com.billetera.back.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDTO {

    private String nombre;
    private String apellido;
    private String email;
    private String password; 
    
}
