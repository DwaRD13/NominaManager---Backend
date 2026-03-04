package com.nomina_manager.empleados;

public class EmpleadoMapper {

    public static EmpleadoDTO mapToDTO(Empleado empleado) {
        EmpleadoDTO empleadoDTO = new EmpleadoDTO();
        empleadoDTO.setNombre(empleadoDTO.getNombre());
        empleadoDTO.setCedula(empleadoDTO.getCedula());
        empleadoDTO.setSalarioMensual(empleadoDTO.getSalarioMensual());
        empleadoDTO.setFechaCreacion(empleadoDTO.getFechaCreacion());
        empleadoDTO.setEstado(empleadoDTO.getEstado());
        empleadoDTO.setIdNomina(empleado.getIdNomina());
        empleadoDTO.setDepartamento(empleadoDTO.getDepartamento());
        empleadoDTO.setPuesto(empleadoDTO.getPuesto());
        return empleadoDTO;
    }

    public static Empleado mapFromDTO(EmpleadoDTO empleadoDTO) {
        Empleado empleado = new Empleado();
        empleado.setNombre(empleadoDTO.getNombre());
        empleado.setCedula(empleadoDTO.getCedula());
        empleado.setSalarioMensual(empleadoDTO.getSalarioMensual());
        empleado.setDepartamento(empleadoDTO.getDepartamento());
        empleado.setPuesto(empleadoDTO.getPuesto());
        empleado.setIdNomina(empleadoDTO.getIdNomina());
        empleado.setEstado(empleadoDTO.getEstado());
        empleado.setFechaCreacion(empleadoDTO.getFechaCreacion());
        return empleado;
    }
}
