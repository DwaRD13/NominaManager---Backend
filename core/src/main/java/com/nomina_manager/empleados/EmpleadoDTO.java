package com.nomina_manager.empleados;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class EmpleadoDTO {
    private String nombre;
    private String cedula;
    private String departamento;
    private String puesto;
    private BigDecimal salarioMensual;
    private Long idNomina;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}
