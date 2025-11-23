package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record JornadaRequestDTO(
        @JsonAlias({"tipo_jornada", "tipoJornada"})
        String tipoJornada
) {
}
