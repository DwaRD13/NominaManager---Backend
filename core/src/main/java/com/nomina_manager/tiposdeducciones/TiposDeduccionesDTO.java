package com.nomina_manager.tiposdeducciones;

import lombok.Data;

@Data
public class TiposDeduccionesDTO {
    private Long id;
    private String nombre;
    private boolean dependeDeSalario;
    private String estado;
}
