package com.nomina_manager.tiposdeducciones;

public class TiposDeduccionesMapper {
    public TiposDeduccionesDTO mapToDTO(TiposDeducciones entity) {
        TiposDeduccionesDTO dto = new TiposDeduccionesDTO();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDependeDeSalario(entity.isDependeDeSalario());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public TiposDeducciones mapFromDTO(TiposDeduccionesDTO dto) {
        TiposDeducciones entity = new TiposDeducciones();
        entity.setId(dto.getId());
        entity.setNombre(dto.getNombre());
        entity.setDependeDeSalario(dto.isDependeDeSalario());
        entity.setEstado(dto.getEstado());
        return entity;
    }
}
