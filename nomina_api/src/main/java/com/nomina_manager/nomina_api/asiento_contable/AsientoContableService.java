package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.AsientoContable;
import com.nomina_manager.asiento_contable.Moneda;

import java.time.LocalDate;
import java.util.List;

public interface AsientoContableService {

    List<Moneda> getAllMoneda();
    List<AsientoContable> getAllAsientoContable();
    AsientoContable crearAsientoContable(Moneda moneda, LocalDate fechaInicio, LocalDate fechaFin, String descripcion);

}
