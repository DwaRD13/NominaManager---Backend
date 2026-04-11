package com.nomina_manager.asiento_contable;

import com.nomina_manager.helpers.ColumnName;
import com.nomina_manager.helpers.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = TableName.ASIENTO_CONTABLE)
public class AsientoContable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ColumnName.ID)
    private Long id;

    @Column(name = ColumnName.DESCRIPCION)
    private String descripcion;

    @Column(name = ColumnName.FECHA_ASIENTO, columnDefinition = "DATETIME")
    private LocalDate fechaAsiento;

    @Column(name = ColumnName.MONTO_TOTAL_DOP)
    private Double montoTotalDop;

    @Column(name = ColumnName.ESTADO)
    private Boolean estado;

    @Column(name = ColumnName.MONTO_TOTAL_TRANSACCION)
    private Double montoTotalTransaccion;

    @Column(name = ColumnName.MONEDA)
    private String moneda;

    @Column(name = ColumnName.FECHA_INICIO)
    private LocalDate fechaInicio;

    @Column(name = ColumnName.FECHA_FIN)
    private LocalDate fechaFin;

}
