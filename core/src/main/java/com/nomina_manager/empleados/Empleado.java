package com.nomina_manager.empleados;

import com.nomina_manager.helpers.ColumnName;
import com.nomina_manager.helpers.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = TableName.EMPLEADO)
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ColumnName.ID)
    private Long id;

    @Column(name = ColumnName.NOMBRE)
    private String nombre;

    @Column(name = ColumnName.CEDULA)
    private String cedula;

    @Column(name = ColumnName.DEPARTAMENTO)
    private String departamento;

    @Column(name = ColumnName.PUESTO)
    private String puesto;

    @Column(name = ColumnName.SALARIO_MENSUAL)
    private BigDecimal salarioMensual;

    @Column(name = ColumnName.ID_NOMINA)
    private Long idNomina;

    @Column(name = ColumnName.ESTADO)
    private String estado;

    @Column(name = ColumnName.FECHA_CREACION)
    private LocalDateTime fechaCreacion;
}
