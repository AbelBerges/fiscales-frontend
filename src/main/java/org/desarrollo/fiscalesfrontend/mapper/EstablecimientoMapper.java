package org.desarrollo.fiscalesfrontend.mapper;

import org.desarrollo.fiscalesfrontend.dto.EstablecimientoDetalleEstadoDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoListaDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.EstablecimientoResponseDTO;
import org.desarrollo.fiscalesfrontend.model.*;

public class EstablecimientoMapper {


    public static Establecimiento aEntidadCreacion(EstablecimientoRequestDTO dto) {
        Establecimiento est = new Establecimiento();
        est.setNombreEstablecimiento(dto.nombre());
        if (dto.descripcion() == null) {
            est.setDescripcion("Sin descripción");
        }
        est.setActivo(dto.activo());
        //Construimos la dirección
        Direccion dir = new Direccion();
        //La calle
        Calle calle = new Calle();
        calle.setIdCalle(dto.idCalle());
        dir.setCalle(calle);
        //La altura
        dir.setAltura(dto.altura());
        //si tiene piso lo construimos
        if (dto.idTipoPiso() != null) {
            TipoPiso tp = new TipoPiso();
            tp.setIdPiso(dto.idTipoPiso());
            dir.setTipoPiso(tp);
        }
        //Si tiene departamento lo consrtuimos
        if (dto.idTipoDepartamento() != null) {
            TipoDepartamento tpd = new TipoDepartamento();
            tpd.setIdDepartamento(dto.idTipoDepartamento());
            dir.setTipoDepartamento(tpd);
        }
        est.setDireccion(dir);
        //Armamos el tipo de Establecimiento
        TipoEstablecimiento tpe = new TipoEstablecimiento();
        est.setTipoEstablecimiento(tpe);
        return est;
    }

    public static Establecimiento aEntidadModelo(EstablecimientoResponseDTO dto) {
        Establecimiento e = new Establecimiento();
        e.setIdEstablecimiento(dto.idEstablecimiento());
        e.setNombreEstablecimiento(dto.nombre());
        e.setDescripcion(dto.descripcion());
        e.setActivo(dto.activo());
        //Armamos la Direccion
        Direccion dir = new Direccion();
        dir.setAltura(dto.altura());
        //Calle
        Calle calle = new Calle();
        calle.setIdCalle(dto.idCalle());
        dir.setCalle(calle);
        //Tipo de Piso
        if (dto.idTipoPiso() != null) {
            TipoPiso tp = new TipoPiso();
            tp.setIdPiso(dto.idTipoPiso());
            dir.setTipoPiso(tp);
        }
        if (dto.idTipoDepartamento() != null) {
            TipoDepartamento tpd = new TipoDepartamento();
            tpd.setIdDepartamento(dto.idTipoDepartamento());
            dir.setTipoDepartamento(tpd);
        }

        e.setDireccion(dir);

        TipoEstablecimiento tpe = new TipoEstablecimiento();
        tpe.setIdTipoEstablecimiento(dto.idTipoEstablecimiento());
        e.setTipoEstablecimiento(tpe);

        return e;
    }

    public static EstablecimientoListaDTO aEstablecimientoListaDTO(Establecimiento est) {
        Long cantidad = null;
        if (!est.getMesas().isEmpty()) {
            cantidad = (long) est.getMesas().size();
        }
        String calle = est.getDireccion() != null && est.getDireccion().getCalle().getNombre() != null
                ? est.getDireccion().getCalle().getNombre()
                :null;
        String piso = est.getDireccion() != null && est.getDireccion().getTipoPiso() != null
                ? est.getDireccion().getTipoPiso().getNombre()
                :null;
        String departamento = est.getDireccion() != null && est.getDireccion().getTipoDepartamento() != null
                ? est.getDireccion().getTipoDepartamento().getNombre()
                :null;
        return new EstablecimientoListaDTO(
                est.getIdEstablecimiento(),
                est.getNombreEstablecimiento(),
                est.getDescripcion(),
                cantidad,
                calle,
                est.getDireccion().getAltura(),
                piso,
                departamento,
                est.getTipoEstablecimiento().getTipo(),
                est.isActivo()
        );
    }

}
