package com.nomina_manager.asiento_contable;

import lombok.Data;

@Data
public class Tipo {
    private Long id;
    private String nombre;
    private String descripcion;
    private String origen;
    private Boolean estado;
}
