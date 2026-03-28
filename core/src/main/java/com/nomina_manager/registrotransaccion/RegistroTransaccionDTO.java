package com.nomina_manager.registrotransaccion;

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
}
