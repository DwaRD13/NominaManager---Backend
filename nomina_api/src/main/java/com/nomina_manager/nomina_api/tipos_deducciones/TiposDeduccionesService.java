package com.nomina_manager.nomina_api.tipos_deducciones;

import com.nomina_manager.tipos_deducciones.TiposDeducciones;
import com.nomina_manager.tipos_deducciones.TiposDeduccionesDTO;

import java.util.List;

public interface TiposDeduccionesService {
    List<TiposDeducciones> encontrarTodosActivos();
    TiposDeducciones encontrarPorId(Long id);
    TiposDeducciones encontrarPorNombre(String nombre);
    TiposDeducciones crearTipoDeDeduccion(TiposDeduccionesDTO dto);
    TiposDeducciones modificarTipoDeDeduccion(TiposDeduccionesDTO dto);
    String eliminarTipoDeDeduccion(Long id);
}
