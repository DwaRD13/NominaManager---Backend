package com.nomina_manager.nomina_api.tiposingresos;

import com.nomina_manager.exception.DoNotExistException;
import com.nomina_manager.exception.ExistObjectException;
import com.nomina_manager.tiposingresos.TiposIngresos;
import com.nomina_manager.tiposingresos.TiposIngresosDTO;
import com.nomina_manager.tiposingresos.TiposIngresosMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class TiposIngresosServiceImp implements TiposIngresosService {

    private final TiposIngresosRepository repository;
    private final TiposIngresosMapper mapper = new TiposIngresosMapper();

    @Override
    public List<TiposIngresos> encontrarTodosActivos() {
        List<TiposIngresos> tipos = repository.encontrarTodosActivos();

        if (tipos.isEmpty()) {
            throw new DoNotExistException("No se encontraron tipos de ingresos activos");
        }
        return tipos;
    }

    @Override
    public TiposIngresos encontrarPorId(Long id) {
        TiposIngresos tipo = repository.buscarPorId(id);

        if (Objects.isNull(tipo)) {
            throw new DoNotExistException("No se encontró el tipo de ingreso con el id: " + id);
        }
        return tipo;
    }

    @Override
    public TiposIngresos encontrarPorNombre(String nombre) {
        TiposIngresos tipo = repository.buscarPorNombre(nombre);

        if (Objects.isNull(tipo)) {
            throw new DoNotExistException("No se encontró el tipo de ingreso con el nombre: " + nombre);
        }
        return tipo;
    }

    @Override
    public TiposIngresos crearTipoDeIngreso(TiposIngresosDTO dto) {

        TiposIngresos existente = repository.buscarPorNombre(dto.getNombre());
        if (existente != null) {
            throw new ExistObjectException("Ya existe un tipo de ingreso con el nombre: " + dto.getNombre());
        }

        TiposIngresos entity = mapper.mapFromDTO(dto);
        entity.setId(null);

        if (entity.getEstado() == null || entity.getEstado().isBlank()) {
            entity.setEstado("Activo");
        }

        return repository.saveAndFlush(entity);
    }

    @Override
    public TiposIngresos modificarTipoDeIngreso(TiposIngresosDTO dto) {

        if (dto.getId() == null) {
            throw new IllegalArgumentException("El ID es obligatorio para modificar");
        }

        TiposIngresos existente = repository.findById(dto.getId())
                .orElseThrow(() -> new DoNotExistException("No se encontró el tipo de ingreso con ID: " + dto.getId()));

        if (!Objects.equals(existente.getNombre(), dto.getNombre())) {
            TiposIngresos otro = repository.buscarPorNombre(dto.getNombre());
            if (otro != null && !Objects.equals(otro.getId(), dto.getId())) {
                throw new ExistObjectException("Ya existe otro tipo de ingreso con el nombre: " + dto.getNombre());
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
    public String eliminarTipoDeIngreso(Long id) {

        repository.findById(id)
                .orElseThrow(() -> new DoNotExistException("No se encontró el tipo de ingreso con ID: " + id));

        repository.deleteTiposDeIngresoById(id);
        return "Tipo de ingreso eliminado con éxito";
    }
}