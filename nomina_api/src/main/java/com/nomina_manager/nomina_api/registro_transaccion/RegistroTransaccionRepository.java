package com.nomina_manager.nomina_api.registro_transaccion;

import com.nomina_manager.registro_transaccion.RegistroTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface RegistroTransaccionRepository extends JpaRepository<RegistroTransaccion, Long> {

    @Query("SELECT r FROM RegistroTransaccion r WHERE r.estado = '1' " +
            "ORDER BY r.fechaCreacion DESC")
    List<RegistroTransaccion> encontrarTodosActivos();
    
    List<RegistroTransaccion> findByEmpleadoIdAndEstado(Long empleadoId, String estado);

    @Query("SELECT r FROM RegistroTransaccion r WHERE " +
            "(:empleadoId IS NULL OR r.empleado.id = :empleadoId) AND " +
            "(:tipoTransaccion IS NULL OR UPPER(TRIM(r.tipoTransaccion)) = UPPER(TRIM(:tipoTransaccion))) AND " +
            "(cast(:fechaInicio as date) IS NULL OR r.fecha >= :fechaInicio) AND " +
            "(cast(:fechaFin as date) IS NULL OR r.fecha <= :fechaFin) " +
            "ORDER BY r.fecha ASC") // <--- Aquí replicamos el OrderBy(t => t.Fecha)
    List<RegistroTransaccion> consultarDinamico(
            @Param("empleadoId") Long empleadoId,
            @Param("tipoTransaccion") String tipoTransaccion,
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT r FROM RegistroTransaccion r WHERE " +
            "r.estado = '1' AND" +
            "(cast(:fechaInicio as date) IS NULL OR r.fecha >= :fechaInicio) AND " +
            "(cast(:fechaFin as date) IS NULL OR r.fecha <= :fechaFin) AND " +
            "r.idAsiento IS NULL " +
            "ORDER BY r.fecha ASC")
    List<RegistroTransaccion> consultarTransaccionesPorFechaSinAsientoContable(
            @Param("fechaInicio") LocalDate fechaInicio,
            @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT r FROM RegistroTransaccion r WHERE r.idAsiento = :idAsiento " +
            "ORDER BY r.fecha DESC")
    List<RegistroTransaccion> encontrarPorIdAsiento(Long idAsiento);
}