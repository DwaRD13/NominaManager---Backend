package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import com.nomina_manager.empleados.EmpleadoDTO;
import com.nomina_manager.empleados.EmpleadoMapper;
import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.exception.ExistObjectException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class EmpleadoServiceImp implements EmpleadoService{

    private final EmpleadoRepository repository;

    @Override
    public List<Empleado> encontrarTodosActivos() {
        List<Empleado> empleados = repository.encontrarTodosActivos();

        if(empleados.isEmpty()){
            throw new DoNotExistException("No se encontraron empleados activos");
        }
        return empleados;
    }

    @Override
    public Empleado encontrarPorId(Long id) {
        Empleado empleado = repository.buscarPorId(id);

        if (Objects.isNull(empleado)){
            throw new DoNotExistException("No se encontro el empleado con el id: " + id);
        }
        return empleado;
    }

    @Override
    public Empleado encontrarPorNombre(String nombre) {
        Empleado empleado = repository.buscarPorNombre(nombre);

        if (Objects.isNull(empleado)){
            throw new DoNotExistException("No se encontro el empleado con el nombre: " + nombre);
        }
        return empleado;
    }

    @Override
    public Empleado crearEmpleado(EmpleadoDTO dto) {

        if(repository.existsByCedula(dto.getCedula())){
            throw new ExistObjectException("Ya existe un empleado registrado con esa cedula. ");
        }
        if(repository.existsByCedulaAndNombre(dto.getCedula(), dto.getNombre())){
            throw new ExistObjectException("Este empleado ya se encuentra registrado con esa cedula y nombre. ");
        }

        EmpleadoMapper mapper = new EmpleadoMapper();
        Empleado empleado = mapper.mapFromDTO(dto);
        empleado.setFechaCreacion(LocalDateTime.now());
        repository.saveAndFlush(empleado);

        return repository.buscarPorId(empleado.getId());
    }


    @Override
    public Empleado modificarEmpleado(EmpleadoDTO dto) {

        Empleado empleadoExistente = repository.findById(dto.getId())
                .orElseThrow(() -> new DoNotExistException("No se encontró el empleado con ID: " + dto.getId()));


        if (!empleadoExistente.getCedula().equals(dto.getCedula())) {
            boolean cedulaEnUso = repository.existsByCedulaAndIdNot(dto.getCedula(), dto.getId());
            if (cedulaEnUso) {
                throw new IllegalArgumentException("La cédula proporcionada ya está registrada en otro empleado.");
            }
        }

        empleadoExistente.setNombre(dto.getNombre());
        empleadoExistente.setCedula(dto.getCedula());
        empleadoExistente.setDepartamento(dto.getDepartamento());
        empleadoExistente.setPuesto(dto.getPuesto());
        empleadoExistente.setSalarioMensual(dto.getSalarioMensual());
        empleadoExistente.setEstado(dto.getEstado());
        empleadoExistente.setIdNomina(dto.getIdNomina());

        return repository.saveAndFlush(empleadoExistente);
    }

    @Override
    public String eliminarEmpleado(Long id) {
        repository.deleteEmpleadoById(id);
        return "Empleado eliminado con exito";
    }
}
