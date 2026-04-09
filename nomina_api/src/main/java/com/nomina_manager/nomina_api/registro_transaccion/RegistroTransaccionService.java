package com.nomina_manager.nomina_api.registro_transaccion;

import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import com.nomina_manager.registro_transaccion.RegistroTransaccionDTO;

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
