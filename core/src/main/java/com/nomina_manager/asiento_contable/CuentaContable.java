package com.nomina_manager.asiento_contable;

import lombok.Data;

@Data
public class CuentaContable {
    private Long id;
    private String tipoMovimiento;
    private Double monto;

    private Cuenta cuenta;
}
