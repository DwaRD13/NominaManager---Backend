package com.nomina_manager.tiposingresos;


public class TiposIngresosMapper {
    public TiposIngresosDTO mapToDTO(TiposIngresos entity) {
        TiposIngresosDTO dto = new TiposIngresosDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDependeDeSalario(entity.isDependeDeSalario());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public TiposIngresos mapFromDTO(TiposIngresosDTO dto) {
        TiposIngresos entity = new TiposIngresos();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setDependeDeSalario(dto.isDependeDeSalario());
        entity.setEstado(dto.getEstado());
        return entity;
    }

}
