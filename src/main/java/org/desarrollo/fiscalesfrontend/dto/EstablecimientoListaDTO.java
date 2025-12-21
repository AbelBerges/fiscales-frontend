package org.desarrollo.fiscalesfrontend.dto;

public record EstablecimientoListaDTO(
        Integer idEstablecimiento,
        String nombreEstablecimiento,
        String descripcion,
        Long cantidadMesas,
        String calle,
        Integer altura,
        String piso,
        String departamento,
        String tipoEstablecimiento,
        Boolean activo
) {
}
