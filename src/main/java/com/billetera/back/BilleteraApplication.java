package com.billetera.back;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// import org.springframework.context.annotation.Bean;
// import org.springframework.boot.CommandLineRunner;
// import com.billetera.back.models.Usuario;
// import com.billetera.back.services.UsuarioService;

@SpringBootApplication
public class BilleteraApplication {

	public static void main(String[] args) {
		SpringApplication.run(BilleteraApplication.class, args);
	}
/* 
@Bean
public CommandLineRunner crearUsuario(UsuarioService usuarioService) {
    return args -> {
        // Crear un usuario de ejemplo
        Usuario usuario = new Usuario();
        usuario.setNombre("Pedro");
        usuario.setApellido("Navaja");
        usuario.setEmail("pe@email.com");
        usuario.setClave("123456");

        // Guardar el usuario en la base de datos
        usuarioService.guardarUsuario(usuario);


        System.out.println("Usuario  creado: " + usuario.toString() );
    }; 
}*/

}
