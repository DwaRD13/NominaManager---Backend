package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;

import java.util.List;

public interface EmpleadoService {

    public List<Empleado> encontrarTodos();
    public Empleado encontrarPorId(long id);
    public Empleado guardarEmpleado(EmpleadoDTO dto);
    public Empleado encontrarPorNombre(String nombre);
    public Empleado modificarEmpleado(EmpleadoDTO dto);
    void eliminarEmpleado(long id);
}
