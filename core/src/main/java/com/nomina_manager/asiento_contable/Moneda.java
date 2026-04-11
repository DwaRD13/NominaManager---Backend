package com.nomina_manager.asiento_contable;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Moneda {

    private Long id;
    private String codigoIso;
    private String nombre;
    private String simbolo;
    private String descripcion;
    private Double tasaCambio;
    private Boolean estado;
    private LocalDateTime fechaCreacion;
}
