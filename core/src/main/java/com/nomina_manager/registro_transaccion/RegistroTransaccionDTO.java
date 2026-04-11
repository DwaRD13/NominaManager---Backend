package com.nomina_manager.registro_transaccion;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class RegistroTransaccionDTO {
    private Long id;
    private Long empleadoId;
    private Long tipoDeDeduccionId;
    private Long tipoDeIngresoId;
    private LocalDate fecha;
    private BigDecimal monto;
    private Long idAsientoContable;
}
