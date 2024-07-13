package com.billetera.back.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billetera.back.exeptionHandler.NombreBancoInvalidoException;
import com.billetera.back.exeptionHandler.NumeroTarjetaInvalidoException;
import com.billetera.back.exeptionHandler.TarjetaDebitoExistenteException;
import com.billetera.back.exeptionHandler.TarjetaDebitoNotFoundException;
import com.billetera.back.exeptionHandler.UsuarioNotFoundException;
import com.billetera.back.models.Tarjeta;
import com.billetera.back.models.TarjetaInfoDTO;
import com.billetera.back.models.Usuario;
import com.billetera.back.repository.TarjetaRepo;
import com.billetera.back.repository.UsuarioRepo;

@Service
public class TarjetaService {

     
    @Autowired
    private TarjetaRepo tarjetaDebitoRepository;

    @Autowired
    private UsuarioRepo usuarioRepo;
    
    public Tarjeta agregarTarjetaDebito(Tarjeta tarjetaDebito, Long usuarioId) {
        // Obtener el usuario correspondiente
        Usuario usuario = usuarioRepo.findById(usuarioId)
                .orElseThrow(UsuarioNotFoundException::new);
        
        
        // Validar que el número de tarjeta tenga 16 dígitos
        if (!tarjetaDebito.getNumeroTarjeta().matches("\\d{16}")) {
            throw new NumeroTarjetaInvalidoException();
        }

        // Validar que el campo banco no esté vacío y contenga solo letras
        if(tarjetaDebito.getBanco() == null || tarjetaDebito.getBanco().isEmpty() || !tarjetaDebito.getBanco().matches("^[a-zA-Z]+(?:\\s+[a-zA-Z]+)*$") ){
            throw new NombreBancoInvalidoException();
        }

        // Verificar si la tarjeta ya está vinculada al usuario
        if (tarjetaDebitoRepository.existsByUsuarioAndNumeroTarjeta(usuario, tarjetaDebito.getNumeroTarjeta())) {
            throw new TarjetaDebitoExistenteException();
        }

           // Verificar si la tarjeta ya está vinculada a alguien
           if (tarjetaDebitoRepository.existsByNumeroTarjeta(tarjetaDebito.getNumeroTarjeta())) {
            throw new TarjetaDebitoExistenteException();
        }
        
        // Vincular la tarjeta de débito al usuario
        tarjetaDebito.setUsuario(usuario);

         // Inicializar el saldo si es null
         if (tarjetaDebito.getSaldo() == null) {
            tarjetaDebito.setSaldo(0.0);
        }
        
        // Guardar la tarjeta de débito
        return tarjetaDebitoRepository.save(tarjetaDebito);
    }


    public TarjetaInfoDTO obtenerInfoTarjeta(Long idTarjeta) {
        Tarjeta tarjeta = tarjetaDebitoRepository.findById(idTarjeta)
                .orElseThrow(() -> new TarjetaDebitoNotFoundException("No se encontró la tarjeta con el ID: " + idTarjeta));
        Long id = idTarjeta;
        String nombreUsuario = tarjeta.getUsuario().getNombre(); // Obtener el nombre del usuario vinculado
        Double saldo = tarjeta.getSaldo(); // Obtener el saldo de la tarjeta
        String banco = tarjeta.getBanco();

        return new TarjetaInfoDTO(id,tarjeta.getNumeroTarjeta(),banco, nombreUsuario, saldo);
    }
    
}
