package com.nomina_manager.nomina_api.tipos_ingresos;

import com.nomina_manager.tipos_ingresos.TiposIngresos;
import com.nomina_manager.tipos_ingresos.TiposIngresosDTO;

import java.util.List;

public interface TiposIngresosService {

    public List<TiposIngresos> encontrarTodosActivos();
    public TiposIngresos encontrarPorId(Long id);
    public TiposIngresos encontrarPorNombre(String nombre);
    public TiposIngresos crearTipoDeIngreso(TiposIngresosDTO dto);
    public TiposIngresos modificarTipoDeIngreso(TiposIngresosDTO dto);
    public String eliminarTipoDeIngreso(Long id);
}
