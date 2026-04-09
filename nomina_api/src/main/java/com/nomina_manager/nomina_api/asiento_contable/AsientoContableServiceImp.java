package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.*;

import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.registro_transaccion.RegistroTransaccionRepository;
import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AsientoContableServiceImp implements AsientoContableService {

    private final WebClient webClient;
    private final AsientoContableRepository repository;
    private final RegistroTransaccionRepository registroTransaccionRepository;

    public AsientoContableServiceImp(WebClient.Builder webClientBuilder, AsientoContableRepository repository,
                                     RegistroTransaccionRepository registroTransaccionRepository) {
        this.webClient = webClientBuilder.baseUrl("http://151.242.194.24").build();
        this.repository = repository;
        this.registroTransaccionRepository = registroTransaccionRepository;
    }

    @Override
    public List<Moneda> getAllMoneda() {
        List<Moneda> monedas = this.webClient.get().uri("api/moneda").retrieve().
                bodyToFlux(Moneda.class).
                collectList().
                block();

        if(Objects.isNull(monedas)){
            throw new DoNotExistException("No se han encontrado monedas");
        }
        return monedas;
    }

    @Override
    public List<AsientoContable> getAllAsientoContable() {
        List<AsientoContable> asientoContables = repository.encontrarTodasAsientoContable();

        if(asientoContables.isEmpty()){
            throw new DoNotExistException("No se encontraron asientos contables activos");
        }
        return asientoContables;
    }

    @Override
    public AsientoContable crearAsientoContable(Moneda moneda, LocalDate fechaInicio, LocalDate fechaFin, String descripcion){

        AsientoContableDTO dto = new AsientoContableDTO();
        dto.setMoneda(moneda);
        dto.setDescripcion(descripcion);
        dto.setFechaAsiento(LocalDate.now());

        Auxiliar aux = new Auxiliar();
        aux.setId(2L);
        dto.setAuxiliar(aux);

        // Buscar transacciones
        List<RegistroTransaccion> registroTransacciones = registroTransaccionRepository.consultarTransaccionesPorFecha(fechaInicio, fechaFin);

        double montoDebito;
        double montoCredito;

        for(RegistroTransaccion registroTransaccion : registroTransacciones){
            if(Objects.nonNull(registroTransaccion.getTipoDeDeduccion())){
                montoCredito = registroTransaccion.getMonto().doubleValue();
            }else{
                montoDebito = registroTransaccion.getMonto().doubleValue();
        }



        return as;
    }
}
