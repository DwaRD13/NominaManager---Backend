package com.nomina_manager.nomina_api.registrotransaccion;

import com.nomina_manager.registrotransaccion.RegistroTransaccion;
import com.nomina_manager.registrotransaccion.RegistroTransaccionDTO;

import java.util.List;

public interface RegistroTransaccionService {
    List<RegistroTransaccion> encontrarTodos();
    List<RegistroTransaccion> encontrarTodosActivos();
    RegistroTransaccion encontrarPorId(Long id);
    List<RegistroTransaccion> encontrarPorEmpleadoId(Long empleadoId);
    RegistroTransaccion crearRegistroTransaccion(RegistroTransaccionDTO dto);
    RegistroTransaccion modificarRegistroTransaccion(RegistroTransaccionDTO dto);
    String eliminarRegistroTransaccion(Long id);
}
