package org.desarrollo.fiscalesfrontend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record JornadaResponseDTO(
        @JsonAlias({"id_jornada", "idJornada"})
        Integer idJornada,
        @JsonAlias({"tipo_jornada", "tipoJornada"})
        String tipoJornada
) {
}
