package com.nomina_manager.nomina_api.registrotransaccion;

import com.nomina_manager.registrotransaccion.RegistroTransaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistroTransaccionRepository extends JpaRepository<RegistroTransaccion, Long> {
    
    List<RegistroTransaccion> findByEmpleadoIdAndEstado(Long empleadoId, String estado);
    
    List<RegistroTransaccion> findByTipoDeIngresoIdAndEstado(Long tipoIngresoId, String estado);
    
    List<RegistroTransaccion> findByTipoDeDeduccionIdAndEstado(Long tipoDeduccionId, String estado);
}
