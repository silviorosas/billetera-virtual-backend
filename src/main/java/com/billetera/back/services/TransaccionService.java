package com.billetera.back.services;




import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.billetera.back.exeptionHandler.MontoInvalidoException;
import com.billetera.back.exeptionHandler.SaldoInsuficienteException;
import com.billetera.back.exeptionHandler.TarjetaDebitoNotFoundException;

import com.billetera.back.models.EnvioDineroRequest;
import com.billetera.back.models.Tarjeta;
import com.billetera.back.models.TipoTransaccion;
import com.billetera.back.models.Transaccion;
import com.billetera.back.models.TransaccionDTO;
import com.billetera.back.models.UserCardInfoResponse;
import com.billetera.back.models.Usuario;
import com.billetera.back.repository.TarjetaRepo;
import com.billetera.back.repository.TransaccionRepo;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import jakarta.transaction.Transactional;


@Service
public class TransaccionService {


    
    @Autowired
    private TarjetaRepo tarjetaRepository;

    @Autowired
    private TransaccionRepo transaccionRepository;
  


    
    @Transactional
    public Transaccion enviarDinero(EnvioDineroRequest request) {
        Tarjeta tarjetaOrigen = tarjetaRepository.findByNumeroTarjeta(request.getNumeroTarjetaOrigen())
            .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta de débito de origen no encontrada"));

        Tarjeta tarjetaDestino = tarjetaRepository.findByNumeroTarjeta(request.getNumeroTarjetaDestino())
            .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta de débito destino no encontrada"));

        if (tarjetaOrigen.getSaldo() < request.getMonto()) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la tarjeta de débito de origen");
        }

        if (request.getMonto() <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor que cero");
        }

        double nuevoSaldoOrigen = tarjetaOrigen.getSaldo() - request.getMonto();
        double nuevoSaldoDestino = tarjetaDestino.getSaldo() + request.getMonto();

        tarjetaOrigen.setSaldo(nuevoSaldoOrigen);
        tarjetaDestino.setSaldo(nuevoSaldoDestino);

        tarjetaRepository.save(tarjetaOrigen);
        tarjetaRepository.save(tarjetaDestino);

        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(TipoTransaccion.ENVIO);
        transaccion.setMonto(request.getMonto());
        transaccion.setTarjetaOrigen(tarjetaOrigen);
        transaccion.setTarjetaDestino(tarjetaDestino);
        transaccion.setUsuarioOrigen(tarjetaOrigen.getUsuario().getNombre());
        transaccion.setUsuarioDestino(tarjetaDestino.getUsuario().getNombre());

        // Asignar la fecha y hora actual
        transaccion.setFecha(LocalDateTime.now());

        transaccionRepository.save(transaccion);
        return transaccion;
    }

    

    public void validarDatos(EnvioDineroRequest request) {
        Tarjeta tarjetaOrigen = tarjetaRepository.findByNumeroTarjeta(request.getNumeroTarjetaOrigen())
            .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta de débito de origen no encontrada"));

        Tarjeta tarjetaDestino = tarjetaRepository.findByNumeroTarjeta(request.getNumeroTarjetaDestino())
            .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta de débito destino no encontrada"));

        if (tarjetaOrigen.getSaldo() < request.getMonto()) {
            throw new SaldoInsuficienteException("Saldo insuficiente en la tarjeta de débito de origen");
        }

        if (request.getMonto() <= 0) {
            throw new MontoInvalidoException("El monto debe ser mayor que cero");
        }
         double nuevoSaldoOrigen = tarjetaOrigen.getSaldo() - request.getMonto();
        double nuevoSaldoDestino = tarjetaDestino.getSaldo() + request.getMonto();

        tarjetaOrigen.setSaldo(nuevoSaldoOrigen);
        tarjetaDestino.setSaldo(nuevoSaldoDestino);

    }
    
    public void agregarSaldo(Long tarjetaId, Double monto) {
        // Buscar la tarjeta por su ID
        Tarjeta tarjeta = tarjetaRepository.findById(tarjetaId)
                .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta no encontrada con el ID: " + tarjetaId));
        
        // Verificar que el monto a agregar sea positivo
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a agregar debe ser mayor que cero");
        }

        // Incrementar el saldo de la tarjeta
        tarjeta.setSaldo(tarjeta.getSaldo() + monto);

        // Guardar la tarjeta actualizada en la base de datos
        tarjetaRepository.save(tarjeta);

        // Crear una nueva instancia de Transaccion
        Transaccion transaccion = new Transaccion();
        transaccion.setTipo(TipoTransaccion.ACRED_HABERES); // Establecer el tipo de transacción como RECIBIDO
        transaccion.setMonto(monto);
        transaccion.setTarjetaDestino(tarjeta); // Asignar la tarjeta como destino
        transaccion.setFecha(LocalDateTime.now()); // Asignar la fecha y hora actual

        // Guardar la transacción en la base de datos
        transaccionRepository.save(transaccion);
    }


    public void pagarConQR(InputStream qrInputStream) throws ChecksumException, FormatException {
        try {
            // Leer la imagen del InputStream
            BufferedImage image = ImageIO.read(qrInputStream);

            // Convertir la imagen en una fuente de luminosidad
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Crear un lector de códigos QR
            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bitmap);

            // Obtener el texto decodificado del código QR
            String qrText = result.getText();

            // Aquí puedes procesar el texto del código QR según tu lógica de negocio
            System.out.println("Texto del código QR: " + qrText);
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }


    public List<Transaccion> obtenerTransaccionesPorUsuario(Usuario usuario) {
    // Obtener todas las tarjetas asociadas al usuario
    List<Tarjeta> tarjetasUsuario = usuario.getTarjetasDebito();
    
    // Crear una lista para almacenar todas las transacciones
    List<Transaccion> transaccionesUsuario = new ArrayList<>();
    
    // Iterar sobre todas las tarjetas del usuario
    for (Tarjeta tarjeta : tarjetasUsuario) {
        // Obtener todas las transacciones donde la tarjeta de origen esté asociada a la tarjeta del usuario
        List<Transaccion> transaccionesOrigen = transaccionRepository.findByTarjetaOrigen(tarjeta);
        transaccionesUsuario.addAll(transaccionesOrigen);
        
        // Obtener todas las transacciones donde la tarjeta de destino esté asociada a la tarjeta del usuario
        List<Transaccion> transaccionesDestino = transaccionRepository.findByTarjetaDestino(tarjeta);
        transaccionesUsuario.addAll(transaccionesDestino);
    }
    
    return transaccionesUsuario;
}

/* 
public List<TransaccionDTO> obtenerTransaccionesPorUsuarioNew(Usuario usuario) {
    List<TransaccionDTO> transaccionesUsuario = new ArrayList<>();
    
    List<Tarjeta> tarjetasUsuario = usuario.getTarjetasDebito();
    for (Tarjeta tarjeta : tarjetasUsuario) {
        List<Transaccion> transaccionesOrigen = transaccionRepository.findByTarjetaOrigen(tarjeta);
        for (Transaccion transaccion : transaccionesOrigen) {
            transaccionesUsuario.add(new TransaccionDTO(transaccion, transaccion.getTarjetaOrigen().getUsuario().getNombre()));
        }
        
        List<Transaccion> transaccionesDestino = transaccionRepository.findByTarjetaDestino(tarjeta);
        for (Transaccion transaccion : transaccionesDestino) {
            transaccionesUsuario.add(new TransaccionDTO(transaccion, transaccion.getTarjetaDestino().getUsuario().getNombre()));
        }
    }
    
    return transaccionesUsuario;
} */

public List<TransaccionDTO> obtenerTransaccionesPorUsuarioNew(Usuario usuario) {
    List<TransaccionDTO> transaccionesUsuario = new ArrayList<>();

    // Obtener todas las tarjetas asociadas al usuario
    List<Tarjeta> tarjetasUsuario = usuario.getTarjetasDebito();
    
    // Recorrer todas las tarjetas del usuario
    for (Tarjeta tarjeta : tarjetasUsuario) {
        // Obtener todas las transacciones donde la tarjeta de origen esté asociada a la tarjeta del usuario
        List<Transaccion> transaccionesOrigen = transaccionRepository.findByTarjetaOrigen(tarjeta);
        
        // Recorrer todas las transacciones de origen
        for (Transaccion transaccion : transaccionesOrigen) {
            // Obtener el nombre del usuario de origen si existe
            String nombreUsuarioOrigen = obtenerNombreUsuario(transaccion.getTarjetaOrigen());
            if (nombreUsuarioOrigen == null) {
                nombreUsuarioOrigen = "Crédito de Haberes";
            }
            
            // Obtener el nombre del usuario de destino si existe
            String nombreUsuarioDestino = obtenerNombreUsuario(transaccion.getTarjetaDestino());
            if (nombreUsuarioDestino == null) {
                nombreUsuarioDestino = "Crédito de Haberes";
            }
            
            // Determinar el tipo de transacción
            String tipoTransaccion = determinarTipoTransaccion(usuario, nombreUsuarioOrigen, nombreUsuarioDestino);

            // Crear el DTO con la información obtenida y agregarlo a la lista
            transaccionesUsuario.add(new TransaccionDTO(transaccion, nombreUsuarioOrigen, nombreUsuarioDestino, tipoTransaccion));
        }
        
        // Obtener todas las transacciones donde la tarjeta de destino esté asociada a la tarjeta del usuario
        List<Transaccion> transaccionesDestino = transaccionRepository.findByTarjetaDestino(tarjeta);
        
        // Recorrer todas las transacciones de destino
        for (Transaccion transaccion : transaccionesDestino) {
            // Obtener el nombre del usuario de origen si existe
            String nombreUsuarioOrigen = obtenerNombreUsuario(transaccion.getTarjetaOrigen());
            if (nombreUsuarioOrigen == null) {
                nombreUsuarioOrigen = "Crédito de Haberes";
            }
            
            // Obtener el nombre del usuario de destino si existe
            String nombreUsuarioDestino = obtenerNombreUsuario(transaccion.getTarjetaDestino());
            if (nombreUsuarioDestino == null) {
                nombreUsuarioDestino = "Crédito de Haberes";
            }
            
            // Determinar el tipo de transacción
            String tipoTransaccion = determinarTipoTransaccion(usuario, nombreUsuarioOrigen, nombreUsuarioDestino);

            // Crear el DTO con la información obtenida y agregarlo a la lista
            transaccionesUsuario.add(new TransaccionDTO(transaccion, nombreUsuarioOrigen, nombreUsuarioDestino, tipoTransaccion));
        }
    }
    
    // Devolver la lista de transacciones con la información completa de los usuarios
    return transaccionesUsuario;
}


// Método para obtener el nombre de usuario de una tarjeta
private String obtenerNombreUsuario(Tarjeta tarjeta) {
    if (tarjeta != null && tarjeta.getUsuario() != null) {
        return tarjeta.getUsuario().getNombre();
    } else {
        return null;
    }
}

// Método para determinar el tipo de transacción
private String determinarTipoTransaccion(Usuario usuario, String nombreUsuarioOrigen, String nombreUsuarioDestino) {
    if (nombreUsuarioOrigen != null && nombreUsuarioOrigen.equals(usuario.getNombre())) {
        return "Dinero enviado"; // Si el nombre de usuario de origen coincide con el del usuario actual, entonces es dinero enviado
    } else if (nombreUsuarioDestino != null && nombreUsuarioDestino.equals(usuario.getNombre())) {
        return "Dinero recibido"; // Si el nombre de usuario de destino coincide con el del usuario actual, entonces es dinero recibido
    } else {
        return ""; // En otros casos, dejar vacío
    }
}


 public UserCardInfoResponse obtenerInfoPorNumeroTarjeta(String numeroTarjeta) {
        Tarjeta tarjeta = tarjetaRepository.findByNumeroTarjeta(numeroTarjeta)
            .orElseThrow(() -> new TarjetaDebitoNotFoundException("Tarjeta no encontrada"));

        Usuario usuario = tarjeta.getUsuario();

        UserCardInfoResponse response = new UserCardInfoResponse();
        response.setNombre(usuario.getNombre());
        response.setApellido(usuario.getApellido());
        response.setBanco(tarjeta.getBanco());

        return response;
    }




}
