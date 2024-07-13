package com.billetera.back.controller;


import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;


import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.billetera.back.exeptionHandler.MontoInvalidoException;
import com.billetera.back.exeptionHandler.SaldoInsuficienteException;
import com.billetera.back.exeptionHandler.TarjetaDebitoNotFoundException;
import com.billetera.back.models.EnvioDineroRequest;
import com.billetera.back.models.Transaccion;
import com.billetera.back.models.TransaccionDTO;
import com.billetera.back.models.UserCardInfoResponse;
import com.billetera.back.models.Usuario;
import com.billetera.back.services.TransaccionService;
import com.billetera.back.services.UsuarioService;
import com.google.zxing.BinaryBitmap;

import com.google.zxing.FormatException;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;



@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:4200")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @Autowired
    private UsuarioService usuarioService;


    @PostMapping("/enviar-dinero")
    public ResponseEntity<Object> enviarDinero(@RequestBody EnvioDineroRequest request) {
       Transaccion transaccion = transaccionService.enviarDinero(request);
        return new ResponseEntity<>(transaccion,HttpStatus.CREATED);
    }

    @PostMapping("/validar-datos")
    public ResponseEntity<?> validarDatos(@RequestBody EnvioDineroRequest request) {
        try {
            transaccionService.validarDatos(request);
            return ResponseEntity.ok().build(); // Si la validación es exitosa, retornamos un ResponseEntity con estado OK
        } catch (TarjetaDebitoNotFoundException | SaldoInsuficienteException | MontoInvalidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // Si hay algún error, retornamos un ResponseEntity con estado BAD_REQUEST y el mensaje de error
        }
    }

     
    @GetMapping("/info-tarjeta/{numeroTarjeta}")
    public ResponseEntity<UserCardInfoResponse> obtenerInfoPorNumeroTarjeta(@PathVariable String numeroTarjeta) {
    UserCardInfoResponse response = transaccionService.obtenerInfoPorNumeroTarjeta(numeroTarjeta);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/agregar-saldo")
    public ResponseEntity<String> agregarSaldo(@RequestBody Map<String, Object> requestParams) {
    Long tarjetaId = Long.valueOf(requestParams.get("tarjetaId").toString());
    Double monto = Double.valueOf(requestParams.get("monto").toString());

    transaccionService.agregarSaldo(tarjetaId, monto);
    return ResponseEntity.status(HttpStatus.OK).body("Saldo agregado correctamente");
    }


    @PostMapping("/decodificar-qr")
    public ResponseEntity<String> decodificarQR(@RequestParam("file") MultipartFile file) throws FormatException {
        try {
            // Convertir el archivo MultipartFile a un BufferedImage
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            BufferedImage bufferedImage = ImageIO.read(bis);

            // Decodificar el código QR
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(bufferedImage)));
            Result result = new MultiFormatReader().decode(binaryBitmap);

            // Devolver el texto del código QR decodificado en formato JSON
            return ResponseEntity.ok("{\"qrText\": \"" + result.getText() + "\"}");
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al decodificar el código QR", e);
        }
    }
        /* Haz clic en el botón "Form Fields" para agregar los campos necesarios.
        Agrega un campo con la clave file y selecciona el tipo "File". 
        Luego, haz clic en "Choose File" para seleccionar el archivo de imagen
         que contiene el código QR en tu computadora. */

   

        

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Transaccion>> obtenerTransaccionesPorUsuarioId(@PathVariable Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
        List<Transaccion> transacciones = transaccionService.obtenerTransaccionesPorUsuario(usuario);
        return ResponseEntity.ok(transacciones);
    }

/* @GetMapping("/usuario2/{usuarioId}")
public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorUsuarioIdNew(@PathVariable Long usuarioId) {
    Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
    List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorUsuarioNew(usuario);
    return ResponseEntity.ok(transacciones);
} */


    @GetMapping("/usuario2/{usuarioId}")
    public ResponseEntity<List<TransaccionDTO>> obtenerTransaccionesPorUsuarioIdNew(@PathVariable Long usuarioId) {
    Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
    List<TransaccionDTO> transacciones = transaccionService.obtenerTransaccionesPorUsuarioNew(usuario);
    return ResponseEntity.ok(transacciones);
    }


    }






