package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record MesaRequestDTO(
        @JsonAlias("numero_mesa")
        Integer numeroMesa,
        Integer idEstablecimiento
) {
}
