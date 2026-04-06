package com.nomina_manager.nomina_api.registrotransaccion;

import com.nomina_manager.registrotransaccion.RegistroTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroTransaccionRepository extends JpaRepository<RegistroTransaccion, Long> {
    
    @Query("SELECT r FROM RegistroTransaccion r WHERE r.estado = '1'")
    List<RegistroTransaccion> encontrarTodosActivos();
    
    List<RegistroTransaccion> findByEmpleadoIdAndEstado(Long empleadoId, String estado);
    
    List<RegistroTransaccion> findByTipoDeIngresoIdAndEstado(Long tipoIngresoId, String estado);
    
    List<RegistroTransaccion> findByTipoDeDeduccionIdAndEstado(Long tipoDeduccionId, String estado);
}
