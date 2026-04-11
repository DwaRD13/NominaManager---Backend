package com.nomina_manager.tipos_ingresos;

import lombok.Data;

@Data
public class TiposIngresosDTO {
    private Long id;
    private String nombre;
    private boolean dependeDeSalario;
    private String estado;
    private Double porcentaje;

}
