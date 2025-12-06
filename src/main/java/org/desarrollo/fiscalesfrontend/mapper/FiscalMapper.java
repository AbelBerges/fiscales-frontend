package org.desarrollo.fiscalesfrontend.mapper;

import org.desarrollo.fiscalesfrontend.dto.FiscalRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.FiscalResponseDTO;
import org.desarrollo.fiscalesfrontend.model.*;

public class FiscalMapper {

    public static Fiscal aEntidadCreacion(FiscalRequestDTO dto) {
        //creamos los objetos para crear el fiscal
        Fiscal nuevo = new Fiscal();
        Direccion dir = new Direccion();
        Calle tomoCalle = new Calle();
        TipoPiso tipoPiso = new TipoPiso();
        TipoDepartamento tipoDepartamento = new TipoDepartamento();
        TipoFiscal tipoFiscal = new TipoFiscal();
        Establecimiento establecimientoVotacion = new Establecimiento();
        //Construyo la dirección
        tomoCalle.setIdCalle(dto.idCalle());
        dir.setCalle(tomoCalle);
        dir.setAltura(dto.altura());
        if (dto.idTipoPiso() != null) {
            tipoPiso.setIdPiso(dto.idTipoPiso());
            dir.setTipoPiso(tipoPiso);
        }
        if (dto.idTipoDepartamento() != null) {
            tipoDepartamento.setIdDepartamento(dto.idTipoDepartamento());
            dir.setTipoDepartamento(tipoDepartamento);
        }
        //Construimos los datos de la Jorndada si existe
        Jornada nuevaJornada = new Jornada();
        if (dto.idJornada() != null) {
            nuevaJornada.setIdJornada(dto.idJornada());
        }
        Mesa mesa = new Mesa();
        if (dto.idMesa() != null) {
            mesa.setIdMesa(dto.idMesa());
        }
        //constuyo el tipo de fiscal
        tipoFiscal.setIdTipoFiscal(dto.idTipoFiscal());
        //contruyo el establecimiento
        if (dto.idEstablecimientoVotacion() != null) {
            establecimientoVotacion.setIdEstablecimiento(dto.idEstablecimientoVotacion());
        }


        //Ya podemos construir el fiscal
        nuevo.setNombreFiscal(dto.nombreFiscal());
        nuevo.setApellidoFiscal(dto.apellidoFiscal());
        nuevo.setEdadFiscal(dto.edad());
        nuevo.setCorreoFiscal(dto.correoFiscal());
        nuevo.setTelefono(dto.telefono());
        nuevo.setActivo(dto.activo());
        nuevo.setEstablecimientoVotacion(establecimientoVotacion);
        nuevo.setTipoFiscal(tipoFiscal);
        nuevo.setDireccion(dir);
        nuevo.setJornada(nuevaJornada);
        nuevo.setMesa(mesa);
        return nuevo;
    }

    public static Fiscal aFiscalDeResponseDTO(FiscalResponseDTO dto) {
        Fiscal crear = new Fiscal();
        Direccion laDir = new Direccion();
        Calle laCalle = new Calle();
        Establecimiento elEst = new Establecimiento();
        TipoFiscal tipoFiscal = new TipoFiscal();
        //Construimos la dirección
        laCalle.setIdCalle(dto.idCalle());
        laDir.setAltura(dto.altura());
        if (dto.idTipoPiso() != null) {
            TipoPiso tipoPiso = new TipoPiso();
            tipoPiso.setIdPiso(dto.idTipoPiso());
            laDir.setTipoPiso(tipoPiso);
        }
        if (dto.idTipoDepartamento() != null) {
            TipoDepartamento tipoDepartamento = new TipoDepartamento();
            tipoDepartamento.setIdDepartamento(dto.idTipoDepartamento());
            laDir.setTipoDepartamento(tipoDepartamento);
        }
        if (dto.idJornada() != null) {
            Jornada jornada = new Jornada();
            jornada.setIdJornada(dto.idJornada());
            crear.setJornada(jornada);
        }
        if (dto.idMesa() != null) {
            Mesa mesa = new Mesa();
            mesa.setIdMesa(dto.idMesa());
            crear.setMesa(mesa);
        }
        laDir.setCalle(laCalle);
        if (dto.idEstablecimientoVotacion() != null) {
            elEst.setIdEstablecimiento(dto.idEstablecimientoVotacion());
        }
        tipoFiscal.setIdTipoFiscal(dto.idTipoFiscal());
        //armamos el fiscal
        crear.setIdFiscal(dto.idFiscal());
        crear.setNombreFiscal(dto.nombreFiscal());
        crear.setApellidoFiscal(dto.apellidoFiscal());
        crear.setEdadFiscal(dto.edadFiscal());
        crear.setDireccion(laDir);
        crear.setEstablecimientoVotacion(elEst);
        crear.setTipoFiscal(tipoFiscal);
        crear.setCorreoFiscal(dto.correoFiscal());
        crear.setTelefono(dto.telefono());
        crear.setActivo(dto.activo());
        return crear;
    }


    public static FiscalResponseDTO aEntidadResponseDTO(Fiscal fiscal) {
        return new FiscalResponseDTO(
                fiscal.getIdFiscal(),
                fiscal.getNombreFiscal(),
                fiscal.getApellidoFiscal(),
                fiscal.getEdadFiscal(),
                fiscal.getCorreoFiscal(),
                fiscal.getTelefono(),
                fiscal.getTipoFiscal().getIdTipoFiscal(),
                fiscal.isActivo(),
                fiscal.getEstablecimientoVotacion() != null ? fiscal.getEstablecimientoVotacion().getIdEstablecimiento(): null,
                fiscal.getDireccion().getCalle().getIdCalle(),
                fiscal.getDireccion().getAltura(),
                fiscal.getDireccion().getTipoPiso() != null ? fiscal.getDireccion().getTipoPiso().getIdPiso() : null,
                fiscal.getDireccion().getTipoDepartamento() != null ? fiscal.getDireccion().getTipoDepartamento().getIdDepartamento() : null,
                fiscal.getJornada() != null ? fiscal.getJornada().getIdJornada() : null,
                fiscal.getMesa() != null ? fiscal.getMesa().getIdMesa() : null
        );
    }
}
