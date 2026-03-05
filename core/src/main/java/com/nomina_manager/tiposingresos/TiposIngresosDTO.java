package com.nomina_manager.tiposingresos;

import com.nomina_manager.helpers.ColumnName;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
public class TiposIngresosDTO {
    private Long id;
    private String nombre;
    private boolean dependeDeSalario;
    private String estado;
}
