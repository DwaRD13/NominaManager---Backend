package com.nomina_manager.tiposingresos;

import lombok.Data;

@Data
public class TiposIngresosDTO {
    private Long id;
    private String nombre;
    private boolean dependeDeSalario;
    private String estado;
    private Double porcentaje;

}
