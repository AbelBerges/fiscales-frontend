package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MesaResponseDTO(
        @JsonAlias({"id_mesa", "idMesa"})
        Integer idMesa,
        @JsonAlias({"numero_mesa", "numeroMesa"})
        Integer numeroMesa,
        Integer idEstablecimiento
) {
}
