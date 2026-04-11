package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.*;

import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.registro_transaccion.RegistroTransaccionRepository;
import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class AsientoContableServiceImp implements AsientoContableService {

    private final WebClient webClient;
    private final AsientoContableRepository repository;
    private final RegistroTransaccionRepository registroTransaccionRepository;

    public AsientoContableServiceImp(WebClient.Builder webClientBuilder, AsientoContableRepository repository,
                                     RegistroTransaccionRepository registroTransaccionRepository) {
        this.webClient = webClientBuilder.baseUrl("http://151.242.194.24:8080/").build();
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
        List<CuentaContable> cuentaContables = this.webClient.get().uri("api/cuentas-contables").retrieve().
                bodyToFlux(CuentaContable.class).
                collectList().
                block();

        double montoDebito = 0;
        double montoCredito = 0;

        for(RegistroTransaccion registroTransaccion : registroTransacciones) {
            if (Objects.nonNull(registroTransaccion.getTipoDeDeduccion())) {
                montoCredito = registroTransaccion.getMonto().doubleValue();
            } else {
                montoDebito = registroTransaccion.getMonto().doubleValue();
            }
        }

        if(montoCredito > montoDebito){
            montoDebito = montoCredito;
        } else{
            montoCredito = montoDebito;
        }

        CuentaContable cuentaDebito = new CuentaContable();
        cuentaDebito.setTipoMovimiento("Debito");
        cuentaDebito.setMonto(montoDebito);

        CuentaContable cuentaCredito = new CuentaContable();
        cuentaCredito.setTipoMovimiento("Credito");
        cuentaCredito.setMonto(montoCredito);

        Cuenta cuentaAuxDeb = new Cuenta();


        cuentaDebito.setCuenta(cuentaAuxDeb);


        Cuenta cuentaAuxCred = new Cuenta();


        cuentaDebito.setCuenta(cuentaAuxCred);


        System.out.println("Monto debito: " + montoDebito);
        System.out.println("Monto credito: " + montoCredito);
        System.out.println("Cuentas contable: " + cuentaContables);


        return null;
    }


}
