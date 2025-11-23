package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EstablecimientoRequestDTO(
        @JsonAlias("nombre")
        String nombre,
        String descripcion,
        Integer idCalle,
        Integer altura,
        Integer idTipoPiso,
        Integer idTipoDepartamento,
        Integer idTipoEstablecimiento,
        Boolean activo
) {
}
