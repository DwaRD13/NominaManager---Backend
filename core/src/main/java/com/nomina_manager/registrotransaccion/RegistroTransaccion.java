package com.nomina_manager.registrotransaccion;

import com.nomina_manager.helpers.ColumnName;
import com.nomina_manager.helpers.TableName;
import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.tiposdeducciones.TiposDeducciones;
import com.nomina_manager.tiposingresos.TiposIngresos;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = TableName.REGISTRO_TRANSACCION)
public class RegistroTransaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ColumnName.ID)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnName.EMPLEADO_ID, nullable = false)
    private Empleado empleado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnName.TIPO_DE_DEDUCCION_ID)
    private TiposDeducciones tipoDeDeduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = ColumnName.TIPO_DE_INGRESO_ID)
    private TiposIngresos tipoDeIngreso;

    @Column(name = ColumnName.TIPO_TRANSACCION)
    private String tipoTransaccion;

    @Column(name = ColumnName.FECHA)
    private LocalDate fecha;

    @Column(name = ColumnName.MONTO)
    private BigDecimal monto;

    @Column(name = ColumnName.ESTADO)
    private String estado;

    @Column(name = ColumnName.FECHA_CREACION)
    private LocalDateTime fechaCreacion;
}
