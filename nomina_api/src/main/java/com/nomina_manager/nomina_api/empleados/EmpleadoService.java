package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {

    public List<Empleado> encontrarTodosActivos();
    public List<Empleado> encontrarTodosActivosMenosTodos();

    public Empleado encontrarPorId(Long id);
    public Empleado encontrarPorNombre(String nombre);
    public Empleado crearEmpleado(EmpleadoDTO dto);
    public Empleado modificarEmpleado(EmpleadoDTO dto);
    public String eliminarEmpleado(Long id);
}
