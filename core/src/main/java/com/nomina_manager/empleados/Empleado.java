package com.nomina_manager.empleados;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "empleado")
@Data
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "cedula")
    private String cedula;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "puesto")
    private String puesto;

    @Column(name = "salario_mensual")
    private BigDecimal salarioMensual;

    @Column(name = "id_nomina")
    private Long idNomina;

    @Column(name = "estado")
    private Boolean estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}
