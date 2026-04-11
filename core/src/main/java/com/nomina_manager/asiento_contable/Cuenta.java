package com.nomina_manager.asiento_contable;

import lombok.Data;

@Data
public class Cuenta {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private Boolean permiteMovimiento;
    private Long nivel;
    private Long balance;
    private String cuentaMayor;
    private Boolean estado;

    private Tipo tipo;
}
