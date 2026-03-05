package com.nomina_manager.nomina_api.tiposingresos;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;
import com.nomina_manager.tiposingresos.TiposIngresos;
import com.nomina_manager.tiposingresos.TiposIngresosDTO;

import java.util.List;

public interface TiposIngresosService {

    public List<TiposIngresos> encontrarTodosActivos();
    public TiposIngresos encontrarPorId(Long id);
    public TiposIngresos encontrarPorNombre(String nombre);
    public TiposIngresos crearTipoDeIngreso(TiposIngresosDTO dto);
    public TiposIngresos modificarTipoDeIngreso(TiposIngresosDTO dto);
    public String eliminarTipoDeIngreso(Long id);
}
