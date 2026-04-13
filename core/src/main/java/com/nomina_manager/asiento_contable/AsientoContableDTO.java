package com.nomina_manager.asiento_contable;

import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AsientoContableDTO {
    private Long id;
    private String descripcion;
    private LocalDate fechaAsiento;
    private Double montoTotalDop;
    private Boolean estado;
    private String moneda;
    private Double montoTotalTransaccion;
    private Long idContabilidad;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;

    List<RegistroTransaccion> registroTransaccion;
}
