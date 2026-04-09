package com.nomina_manager.nomina_api.asiento_contable;

import com.nomina_manager.asiento_contable.AsientoContable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {

    @Query(value = "SELECT ac FROM AsientoContable ac WHERE ac.estado = true")
    List<AsientoContable> encontrarTodasAsientoContable();
}
