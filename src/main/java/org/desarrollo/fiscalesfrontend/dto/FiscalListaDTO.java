package org.desarrollo.fiscalesfrontend.dto;

public record FiscalListaDTO(
        Integer idFiscal,
        String nombreFiscal,
        String apellidoFiscal,
        Integer edadFiscal,
        String correoFiscal,
        String telefono,
        String tipoFiscal,
        String jornada,
        String establecimientoVoto,
        String establecimientoAsignado,
        String calle,
        Integer altura,
        String piso,
        String departamento,
        Integer numeroMesa,
        Boolean activo
) {
}
