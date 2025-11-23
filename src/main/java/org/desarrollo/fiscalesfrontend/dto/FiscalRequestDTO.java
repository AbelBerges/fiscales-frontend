package org.desarrollo.fiscalesfrontend.dto;

public record FiscalRequestDTO(
        String nombreFiscal,
        String apellidoFiscal,
        Integer edad,
        String correoFiscal,
        String telefono,
        Integer idTipoFiscal,
        Boolean activo,
        Integer idEstablecimientoVotacion,
        Integer idCalle,
        Integer altura,
        Integer idTipoPiso,
        Integer idTipoDepartamento,
        Integer idJornada,
        Integer idMesa
) {
}
