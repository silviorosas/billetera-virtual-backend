package com.billetera.back.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.billetera.back.models.Tarjeta;
import com.billetera.back.models.Transaccion;



public interface TransaccionRepo extends JpaRepository<Transaccion,Long> {

    List<Transaccion> findByTarjetaOrigen(Tarjeta tarjeta);

    List<Transaccion> findByTarjetaDestino(Tarjeta tarjeta);

       


    
}
