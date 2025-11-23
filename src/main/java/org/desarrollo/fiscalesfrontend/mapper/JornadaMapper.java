package org.desarrollo.fiscalesfrontend.mapper;

import org.desarrollo.fiscalesfrontend.dto.JornadaRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.JornadaResponseDTO;
import org.desarrollo.fiscalesfrontend.model.Jornada;

public class JornadaMapper {

    public static Jornada aEntidadCreacion(JornadaRequestDTO dto) {
        return new Jornada(dto.tipoJornada());
    }

    public static JornadaResponseDTO aJornadaResponseDTO(Jornada jornada) {
        return new JornadaResponseDTO(
                jornada.getIdJornada(),
                jornada.getTipoJornada()
        );
    }

    public static Jornada aEntidadCompleta(JornadaResponseDTO dto) {
        Jornada nueva = new Jornada();
        nueva.setIdJornada(dto.idJornada());
        nueva.setTipoJornada(dto.tipoJornada());
        return nueva;
    }
}
