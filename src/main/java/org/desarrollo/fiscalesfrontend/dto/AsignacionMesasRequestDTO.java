package org.desarrollo.fiscalesfrontend.dto;

import java.util.List;

public record AsignacionMesasRequestDTO(
        Integer idEstablecimiento,
        List<Integer> numerosMesa
) {
}
