package com.nomina_manager.asiento_contable;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AsientoContableRequest {
    private Long id;
    private String descripcion;
    private LocalDate fechaAsiento;
    private Double montoTotal;
    private Boolean estado;
    private Double tasaCambio;
    private Double montoTotalCambio;

    private Auxiliar auxiliar;
    private List<CuentaContable> detalles;
    private Moneda moneda;

}
