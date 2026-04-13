package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.*;
import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.nomina_api.registro_transaccion.RegistroTransaccionRepository;
import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

        BigDecimal montoRetencionesDop = transacciones.stream()
                .filter(t -> Objects.nonNull(t.getTipoDeDeduccion()))
                .map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoNetoDop = transacciones.stream()
                .filter(t -> Objects.isNull(t.getTipoDeDeduccion()))
                .map(t -> t.getMonto() != null ? t.getMonto() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal montoGastoBrutoDop = montoNetoDop.add(montoRetencionesDop);

        AsientoContableRequest request = prepararRequest(moneda, descripcion, montoGastoBrutoDop,
                montoNetoDop, montoRetencionesDop, todasLasCuentas);

        AsientoContableRequest asientoContableRequest = enviarAsientoAApi(request);

        AsientoContable asientoLocal = mapearParaEntidadYGuardar(request, fechaInicio, fechaFin, asientoContableRequest.getId());
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
        asientoContableDTO.setMontoTotalDop(asientoContable.getMontoTotalDop());
        asientoContableDTO.setMoneda(asientoContable.getMoneda());
        asientoContableDTO.setMontoTotalTransaccion(asientoContable.getMontoTotalTransaccion());
        asientoContableDTO.setFechaInicio(asientoContable.getFechaInicio());
        asientoContableDTO.setFechaFin(asientoContable.getFechaFin());
        asientoContableDTO.setIdContabilidad(asientoContable.getIdContabilidad());

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

    private AsientoContableRequest prepararRequest(Moneda moneda, String desc, BigDecimal brutoDop,
                                                   BigDecimal netoDop, BigDecimal retencionesDop, List<Cuenta> cuentas) {

        BigDecimal tasa = (moneda.getTasaCambio() != null && moneda.getTasaCambio() > 0)
                ? BigDecimal.valueOf(moneda.getTasaCambio())
                : BigDecimal.ONE;

        AsientoContableRequest request = new AsientoContableRequest();
        request.setMoneda(moneda);
        request.setTasaCambio(tasa.doubleValue());
        request.setMontoTotalDop(brutoDop.doubleValue());

        BigDecimal brutoConvertido = brutoDop.divide(tasa, 2, RoundingMode.HALF_UP);
        BigDecimal retencionesConvertidas = retencionesDop.divide(tasa, 2, RoundingMode.HALF_UP);
        BigDecimal netoConvertido = brutoConvertido.subtract(retencionesConvertidas);

        request.setDescripcion(desc);
        request.setFechaAsiento(LocalDate.now());

        request.setMontoTotal(brutoConvertido.doubleValue());
        request.setMontoTotalCambio(brutoConvertido.doubleValue());

        request.setEstado(true);

        Auxiliar aux = new Auxiliar();
        aux.setId(2L);
        request.setAuxiliar(aux);

        List<CuentaContable> detalles = List.of(
                crearDetalle(brutoConvertido, "Debito", mapearCuentaPorCodigo(cuentas, "2101-02")),
                crearDetalle(netoConvertido, "Credito", mapearCuentaPorCodigo(cuentas, "2101-01")),
                crearDetalle(retencionesConvertidas, "Credito", mapearCuentaPorCodigo(cuentas, "2101-03"))
        );

        request.setDetalles(detalles);
        return request;
    }

    private CuentaContable crearDetalle(BigDecimal monto, String tipo, Cuenta cuenta) {
        CuentaContable detalle = new CuentaContable();
        detalle.setMonto(monto.doubleValue());
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

    public AsientoContable mapearParaEntidadYGuardar(AsientoContableRequest asientoContableRequest,
                                                     LocalDate fechaInicio,
                                                     LocalDate fechaFin,
                                                     Long asientoContableId) {
        AsientoContable asientoContable = new AsientoContable();
        asientoContable.setDescripcion(asientoContableRequest.getDescripcion());
        asientoContable.setFechaAsiento(LocalDate.now());
        asientoContable.setMoneda(asientoContableRequest.getMoneda().getCodigoIso() + asientoContableRequest.getMoneda().getSimbolo());
        asientoContable.setMontoTotalDop(asientoContableRequest.getMontoTotalDop());
        asientoContable.setEstado(asientoContableRequest.getEstado());
        asientoContable.setMontoTotalTransaccion(asientoContableRequest.getMontoTotal());
        asientoContable.setFechaInicio(fechaInicio);
        asientoContable.setFechaFin(fechaFin);
        asientoContable.setIdContabilidad(asientoContableId);
        repository.saveAndFlush(asientoContable);
        return asientoContable;
    }
}
