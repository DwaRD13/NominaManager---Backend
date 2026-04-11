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
    private Double montoTotal;
    private Boolean estado;

    List<RegistroTransaccion> registroTransaccion;
}
