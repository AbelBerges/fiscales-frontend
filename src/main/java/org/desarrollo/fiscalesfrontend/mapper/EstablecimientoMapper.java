package org.desarrollo.fiscalesfrontend.mapper;

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


}
