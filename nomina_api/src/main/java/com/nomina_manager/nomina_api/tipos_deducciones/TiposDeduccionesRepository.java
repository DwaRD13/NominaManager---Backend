package com.nomina_manager.nomina_api.tipos_deducciones;

import com.nomina_manager.tipos_deducciones.TiposDeducciones;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TiposDeduccionesRepository extends JpaRepository<TiposDeducciones, Long> {
    @Query("SELECT t FROM TiposDeducciones t WHERE t.estado = 'Activo'")
    List<TiposDeducciones> encontrarTodosActivos();

    @Query("SELECT t FROM TiposDeducciones t WHERE t.nombre = :nombre")
    TiposDeducciones buscarPorNombre(@Param("nombre") String nombre);

    @Query("SELECT t FROM TiposDeducciones t WHERE t.id = :id")
    TiposDeducciones buscarPorId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE TiposDeducciones t SET t.estado = 'Eliminado' WHERE t.id = :id")
    void deleteTiposDeDeduccionById(@Param("id") Long id);
}
