package com.nomina_manager.nomina_api.tipos_deducciones;

import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.exception.ExistObjectException;
import com.nomina_manager.tipos_deducciones.TiposDeducciones;
import com.nomina_manager.tipos_deducciones.TiposDeduccionesDTO;
import com.nomina_manager.tipos_deducciones.TiposDeduccionesMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TiposDeduccionesServiceImp implements TiposDeduccionesService {
    private final TiposDeduccionesRepository repository;
    private final TiposDeduccionesMapper mapper = new TiposDeduccionesMapper();

    @Override
    public List<TiposDeducciones> encontrarTodosActivos() {
        List<TiposDeducciones> tipos = repository.encontrarTodosActivos();
        if (tipos.isEmpty()) {
            throw new DoNotExistException("No se encontraron tipos de deducciones activos");
        }
        return tipos;
    }

    @Override
    public TiposDeducciones encontrarPorId(Long id) {
        TiposDeducciones tipo = repository.buscarPorId(id);
        if (Objects.isNull(tipo)) {
            throw new DoNotExistException("No se encontró el tipo de deducción con el id: " + id);
        }
        return tipo;
    }

    @Override
    public TiposDeducciones encontrarPorNombre(String nombre) {
        TiposDeducciones tipo = repository.buscarPorNombre(nombre);
        if (Objects.isNull(tipo)) {
            throw new DoNotExistException("No se encontró el tipo de deducción con el nombre: " + nombre);
        }
        return tipo;
    }

    @Override
    public TiposDeducciones crearTipoDeDeduccion(TiposDeduccionesDTO dto) {
        TiposDeducciones existente = repository.buscarPorNombre(dto.getNombre());
        if (existente != null) {
            throw new ExistObjectException("Ya existe un tipo de deducción con el nombre: " + dto.getNombre());
        }

        TiposDeducciones entity = mapper.mapFromDTO(dto);
        entity.setId(null);

        if (entity.getEstado() == null || entity.getEstado().isBlank()) {
            entity.setEstado("Activo");
        }

        return repository.saveAndFlush(entity);
    }

    @Override
    public TiposDeducciones modificarTipoDeDeduccion(TiposDeduccionesDTO dto) {
        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para modificar");
        }

        TiposDeducciones existente = repository.findById(dto.getId())
                .orElseThrow(() -> new DoNotExistException("No se encontró el tipo de deducción con ID: " + dto.getId()));

        if (!Objects.equals(existente.getNombre(), dto.getNombre())) {
            TiposDeducciones otro = repository.buscarPorNombre(dto.getNombre());
            if (otro != null && !Objects.equals(otro.getId(), dto.getId())) {
                throw new ExistObjectException("Ya existe otro tipo de deducción con el nombre: " + dto.getNombre());
            }
        }

        existente.setNombre(dto.getNombre());
        existente.setDependeDeSalario(dto.isDependeDeSalario());
        existente.setPorcentaje(dto.getPorcentaje());

        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            existente.setEstado(dto.getEstado());
        }

        return repository.saveAndFlush(existente);
    }

    @Override
    public String eliminarTipoDeDeduccion(Long id) {
        repository.findById(id)
                .orElseThrow(() -> new DoNotExistException("No se encontró el tipo de deducción con ID: " + id));

        repository.deleteTiposDeDeduccionById(id);
        return "Tipo de deducción eliminado con éxito";
    }
}
