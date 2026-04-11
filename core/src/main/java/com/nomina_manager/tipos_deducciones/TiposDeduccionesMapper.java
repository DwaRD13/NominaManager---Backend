package com.nomina_manager.tipos_deducciones;

public class TiposDeduccionesMapper {
    public TiposDeduccionesDTO mapToDTO(TiposDeducciones entity) {
        TiposDeduccionesDTO dto = new TiposDeduccionesDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDependeDeSalario(entity.isDependeDeSalario());
        dto.setEstado(entity.getEstado());
        dto.setPorcentaje(entity.getPorcentaje());
        return dto;
    }

    public TiposDeducciones mapFromDTO(TiposDeduccionesDTO dto) {
        TiposDeducciones entity = new TiposDeducciones();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setDependeDeSalario(dto.isDependeDeSalario());
        entity.setEstado(dto.getEstado());
        entity.setPorcentaje(dto.getPorcentaje());
        return entity;
    }
}
