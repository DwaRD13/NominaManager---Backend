package com.nomina_manager.nomina_api.tiposdeducciones;

import com.nomina_manager.tiposdeducciones.TiposDeducciones;
import com.nomina_manager.tiposdeducciones.TiposDeduccionesDTO;

import java.util.List;

public interface TiposDeduccionesService {
    List<TiposDeducciones> encontrarTodosActivos();
    TiposDeducciones encontrarPorId(Long id);
    TiposDeducciones encontrarPorNombre(String nombre);
    TiposDeducciones crearTipoDeDeduccion(TiposDeduccionesDTO dto);
    TiposDeducciones modificarTipoDeDeduccion(TiposDeduccionesDTO dto);
    String eliminarTipoDeDeduccion(Long id);
}
