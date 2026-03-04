package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmpleadoServiceImp implements EmpleadoService{

    private EmpleadoRepository repository;

    @Override
    public List<Empleado> encontrarTodos() {
        return repository.findAll();
    }

    @Override
    public Empleado encontrarPorId(long id) {
        return null;
    }

    @Override
    public Empleado guardarEmpleado(EmpleadoDTO dto) {
        return null;
    }

    @Override
    public Empleado encontrarPorNombre(String nombre) {
        return null;
    }

    @Override
    public Empleado modificarEmpleado(EmpleadoDTO dto) {
        return null;
    }

    @Override
    public void eliminarEmpleado(long id) {

    }
}
