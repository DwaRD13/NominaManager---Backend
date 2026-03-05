package com.nomina_manager.nomina_api.empleados;

import com.nomina_manager.empleados.Empleado;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {

    @Query(value = "SELECT e FROM Empleado e WHERE e.estado = 'Activo' ")
    List<Empleado> encontrarTodosActivos();

    @Query(value = "SELECT e FROM Empleado e WHERE e.nombre = :nombre")
    Empleado buscarPorNombre(@Param("nombre") String nombre);

    @Query(value = "SELECT e FROM Empleado e WHERE e.id = :id")
    Empleado buscarPorId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "UPDATE Empleado e " +
            "       SET e.estado = 'Eliminado'" +
            "       WHERE e.id = :id")
    void deleteEmpleadoById(@Param("id") Long id);

    boolean existsByCedula(String cedula);

    boolean existsByCedulaAndNombre(String cedula, String nombre);

    boolean existsByCedulaAndIdNot(String cedula, Long id);
}
