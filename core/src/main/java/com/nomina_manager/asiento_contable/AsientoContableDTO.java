package com.nomina_manager.asiento_contable;


import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class AsientoContableDTO {
    private Long id;
    private String descripcion;
    private LocalDate fechaAsiento;
    private Double montoTotal;
    private Boolean estado;
    private Long idAsientoContable;
    private Double tasaCambio;
    private Double montoTotalCambio;

    private Auxiliar auxiliar;
    private List<CuentaContable> detalles;
    private Moneda moneda;

}
