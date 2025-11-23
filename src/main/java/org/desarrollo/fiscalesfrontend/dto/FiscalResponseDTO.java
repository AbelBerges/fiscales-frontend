package org.desarrollo.fiscalesfrontend.dto;

public record FiscalResponseDTO(
        Integer idFiscal,
        String nombreFiscal,
        String apellidoFiscal,
        Integer edadFiscal,
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
