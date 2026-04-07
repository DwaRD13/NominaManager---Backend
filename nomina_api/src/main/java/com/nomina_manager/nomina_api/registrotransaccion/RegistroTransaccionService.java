package com.nomina_manager.nomina_api.registrotransaccion;

import com.nomina_manager.registrotransaccion.RegistroTransaccion;
import com.nomina_manager.registrotransaccion.RegistroTransaccionDTO;

import java.time.LocalDate;
import java.util.List;

public interface RegistroTransaccionService {
    List<RegistroTransaccion> encontrarTodos();
    List<RegistroTransaccion> encontrarTodosActivos();
    RegistroTransaccion encontrarPorId(Long id);
    List<RegistroTransaccion> encontrarPorEmpleadoId(Long empleadoId);
    List<RegistroTransaccion> consultar(
            Long empleadoId,
            String tipoTransaccion,
            LocalDate fechaInicio,
            LocalDate fechaFin);
    RegistroTransaccion crearRegistroTransaccion(RegistroTransaccionDTO dto);
    RegistroTransaccion modificarRegistroTransaccion(RegistroTransaccionDTO dto);
    String eliminarRegistroTransaccion(Long id);
}
