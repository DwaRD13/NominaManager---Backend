package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query(value = "SELECT Empleado FROM Empleado")
    List<Empleado> obtenerTodos();
}
