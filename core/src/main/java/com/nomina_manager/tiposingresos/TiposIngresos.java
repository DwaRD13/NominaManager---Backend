package com.nomina_manager.tiposingresos;


import com.nomina_manager.helpers.ColumnName;
import com.nomina_manager.helpers.TableName;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = TableName.TIPO_INGRESOS)
public class TiposIngresos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = ColumnName.ID)
    private Long id;

    @Column(name = ColumnName.NOMBRE)
    private String nombre;

    @Column(name = ColumnName.DEPENDE_DE_SALARIO)
    private boolean dependeDeSalario;

    @Column(name = ColumnName.ESTADO)
    private String estado;
}
