package com.nomina_manager.empleados;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Empleado {
    private Long id;
    private String nombre;
    private String cedula;
    private String departamento;
    private String puesto;
    private float salarioMensual;
    private Long idNomina;
    private String estado;
    private LocalDate fechaCreacion;
}
