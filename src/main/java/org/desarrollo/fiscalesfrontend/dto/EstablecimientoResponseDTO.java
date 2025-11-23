package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record EstablecimientoResponseDTO(
        @JsonAlias({"id_establecimiento", "idEstablecimiento"})
        Integer idEstablecimiento,
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
