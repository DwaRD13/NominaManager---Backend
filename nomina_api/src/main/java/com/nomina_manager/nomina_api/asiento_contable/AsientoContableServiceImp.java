package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.*;
import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.registro_transaccion.RegistroTransaccionRepository;
import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import jakarta.transaction.Transactional;
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
        List<Moneda> monedas = this.webClient.get().uri("api/monedas").retrieve().
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
    @Transactional
    public AsientoContable crearAsientoContable(Moneda moneda, LocalDate fechaInicio, LocalDate fechaFin, String descripcion) {
        List<Cuenta> todasLasCuentas = fetchCuentasDesdeApi();
        List<RegistroTransaccion> transacciones = obtenerTransaccionesPendientes(fechaInicio, fechaFin);

        double montoRetenciones = transacciones.stream()
                .filter(t -> Objects.nonNull(t.getTipoDeDeduccion()))
                .mapToDouble(t -> t.getMonto().doubleValue())
                .sum();

        double montoNeto = transacciones.stream()
                .filter(t -> Objects.isNull(t.getTipoDeDeduccion()))
                .mapToDouble(t -> t.getMonto().doubleValue())
                .sum();

        double montoGastoBruto = montoNeto + montoRetenciones;

        AsientoContableRequest request = prepararRequest(moneda, descripcion, montoGastoBruto,
                montoNeto, montoRetenciones, todasLasCuentas);

        AsientoContableRequest respuestaApi = enviarAsientoAApi(request);

        AsientoContable asientoLocal = mapearParaEntidadYGuardar(request);
        vincularTransaccionesConAsiento(transacciones, asientoLocal.getId());

        return asientoLocal;
    }

    @Override
    public AsientoContableDTO getAsientoContableDetailsById(Long id) {
        AsientoContableDTO asientoContableDTO = new AsientoContableDTO();

        AsientoContable asientoContable = repository.findById(id).orElse(null);

        if(Objects.isNull(asientoContable)){
            throw new DoNotExistException("Ha ocurrido un error al encontrar el Asiento Contable. Intente más tarrde");
        }

        asientoContableDTO.setId(asientoContable.getId());
        asientoContableDTO.setEstado(asientoContable.getEstado());
        asientoContableDTO.setFechaAsiento(asientoContable.getFechaAsiento());
        asientoContableDTO.setDescripcion(asientoContable.getDescripcion());
        asientoContableDTO.setMontoTotal(asientoContable.getMontoTotal());

        List<RegistroTransaccion> registroTransaccions = registroTransaccionRepository.encontrarPorIdAsiento(id);

        if(Objects.isNull(registroTransaccions)){
            throw new DoNotExistException("No se han encontrado transaciones asociadas al Asiento Contable");
        }

        asientoContableDTO.setRegistroTransaccion(registroTransaccions);
        return asientoContableDTO;
    }


    private List<Cuenta> fetchCuentasDesdeApi() {
        List<Cuenta> cuentas = this.webClient.get().uri("api/cuentas-contables")
                .retrieve()
                .bodyToFlux(Cuenta.class)
                .collectList()
                .block();

        if (cuentas == null || cuentas.isEmpty()) {
            throw new DoNotExistException("No se han encontrado cuentas en la API");
        }
        return cuentas;
    }

    private List<RegistroTransaccion> obtenerTransaccionesPendientes(LocalDate inicio, LocalDate fin) {
        List<RegistroTransaccion> transacciones = registroTransaccionRepository
                .consultarTransaccionesPorFechaSinAsientoContable(inicio, fin);

        if (transacciones.isEmpty()) {
            throw new RuntimeException("No se han encontrado transacciones para registrar");
        }
        return transacciones;
    }

    private AsientoContableRequest prepararRequest(Moneda moneda, String desc, double bruto,
                                                   double neto, double retenciones, List<Cuenta> cuentas) {
        AsientoContableRequest request = new AsientoContableRequest();
        request.setMoneda(moneda);
        request.setDescripcion(desc);
        request.setFechaAsiento(LocalDate.now());
        request.setMontoTotal(bruto);
        request.setEstado(true);

        Auxiliar aux = new Auxiliar();
        aux.setId(2L);
        request.setAuxiliar(aux);

        List<CuentaContable> detalles = List.of(
                crearDetalle(bruto, "Debito", mapearCuentaPorCodigo(cuentas, "2101-02")), // Gasto
                crearDetalle(neto, "Credito", mapearCuentaPorCodigo(cuentas, "2101-01")),  // Salarios
                crearDetalle(retenciones, "Credito", mapearCuentaPorCodigo(cuentas, "2101-03")) // Retenciones
        );
        request.setDetalles(detalles);
        return request;
    }

    private CuentaContable crearDetalle(double monto, String tipo, Cuenta cuenta) {
        CuentaContable detalle = new CuentaContable();
        detalle.setMonto(monto);
        detalle.setTipoMovimiento(tipo);
        detalle.setCuenta(cuenta);
        return detalle;
    }

    private AsientoContableRequest enviarAsientoAApi(AsientoContableRequest request) {
        AsientoContableRequest response = webClient.post()
                .uri("/api/asientos")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(AsientoContableRequest.class)
                .block();

        if (response == null) {
            throw new RuntimeException("La API externa no devolvió una respuesta válida");
        }
        return response;
    }

    private void vincularTransaccionesConAsiento(List<RegistroTransaccion> transacciones, Long asientoId) {
        transacciones.forEach(t -> {
            t.setIdAsiento(asientoId);
            registroTransaccionRepository.save(t);
        });
    }
    public Cuenta mapearCuentaPorCodigo(List<Cuenta> cuentas, String codigo){
        return cuentas.stream().filter((cuenta) ->
                cuenta.getCodigo().equals(codigo)).
                findFirst().
                orElse(null);
    }

    public AsientoContable mapearParaEntidadYGuardar(AsientoContableRequest asientoContableRequest){
        AsientoContable asientoContable = new AsientoContable();
        asientoContable.setDescripcion(asientoContableRequest.getDescripcion());
        asientoContable.setFechaAsiento(LocalDate.now());
        asientoContable.setMontoTotal(asientoContableRequest.getMontoTotal());
        asientoContable.setEstado(asientoContableRequest.getEstado());

        repository.saveAndFlush(asientoContable);
        return asientoContable;
    }
}
