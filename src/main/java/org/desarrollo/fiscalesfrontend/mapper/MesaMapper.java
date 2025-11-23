package org.desarrollo.fiscalesfrontend.mapper;

import org.desarrollo.fiscalesfrontend.dto.MesaRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.MesaResponseDTO;
import org.desarrollo.fiscalesfrontend.model.Establecimiento;
import org.desarrollo.fiscalesfrontend.model.Mesa;

public class MesaMapper {

    public static Mesa aEntidadCreacion(MesaRequestDTO dto) {
        Mesa mesa = new Mesa();
        mesa.setNumeroMesa(dto.numeroMesa());
        Establecimiento est = new Establecimiento();
        if (dto.numeroMesa() != null) {
            est.setIdEstablecimiento(dto.idEstablecimiento());
        }
        mesa.setEstablecimiento(est);
        return mesa;
    }

    public static MesaResponseDTO aEntidadResponseDTO( Mesa mesa) {
        return new MesaResponseDTO(
                mesa.getIdMesa(),
                mesa.getNumeroMesa(),
                mesa.getEstablecimiento() != null ? mesa.getEstablecimiento().getIdEstablecimiento() : null
        );
    }

    public static Mesa aEntidadCompleta(MesaResponseDTO dto) {
        Mesa crear = new Mesa();
        crear.setIdMesa(dto.idMesa());
        crear.setNumeroMesa(dto.numeroMesa());
        if (dto.idEstablecimiento() != null) {
            Establecimiento est = new Establecimiento();
            est.setIdEstablecimiento(dto.idEstablecimiento());
            crear.setEstablecimiento(est);
        }

        return crear;
    }
}
