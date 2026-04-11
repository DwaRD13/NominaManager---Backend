package com.nomina_manager.nomina_api.tipos_ingresos;

import com.nomina_manager.tipos_ingresos.TiposIngresos;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiposIngresosRepository extends JpaRepository<TiposIngresos, Long> {

    @Query("SELECT t FROM TiposIngresos t WHERE t.estado = 'Activo'")
    List<TiposIngresos> encontrarTodosActivos();

    @Query("SELECT t FROM TiposIngresos t WHERE t.nombre = :nombre")
    TiposIngresos buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT t FROM TiposIngresos t WHERE t.id = :id")
    TiposIngresos buscarPorId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE TiposIngresos t SET t.estado = 'Eliminado' WHERE t.id = :id")
    void deleteTiposDeIngresoById(@Param("id") Long id);

}
