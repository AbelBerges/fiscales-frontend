package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Fiscal {
    @JsonAlias({"id_fiscal", "idFiscal"})
    private Integer idFiscal;
    @JsonAlias({"nombre_fiscal", "nombreFiscal"})
    private String nombreFiscal;
    @JsonAlias({"apellido_fiscal", "apellidoFiscal"})
    private String apellidoFiscal;
    @JsonAlias({"edad_fiscal", "edad"})
    private int edad;
    @JsonAlias({"correo_fiscal", "correoFiscal"})
    private String correoFiscal;
    private String telefono;
    private TipoFiscal tipoFiscal;
    private Boolean activo;
    private Establecimiento establecimientoVotacion;
    private Establecimiento establecimientoAsignado;
    private Direccion direccion;
    private Jornada jornada;
    private Mesa mesa;


    public Fiscal() {}

    public Fiscal(String nombreFiscal, String apellidoFiscal, Jornada jornada, int edadFiscal, String correoFiscal, String telefono,
                  TipoFiscal tipoFiscal, boolean activo,
                  Establecimiento establecimientoVotacion, Establecimiento establecimientoAsignado, Direccion direccion, Mesa mesa) {
        this.nombreFiscal = nombreFiscal;
        this.apellidoFiscal = apellidoFiscal;
        this.jornada = jornada;
        this.edad = edadFiscal;
        this.correoFiscal = correoFiscal;
        this.telefono = telefono;
        this.tipoFiscal = tipoFiscal;
        this.activo = activo;
        this.establecimientoVotacion = establecimientoVotacion;
        this.establecimientoAsignado = establecimientoAsignado;
        this.direccion = direccion;
        this.mesa = mesa;
    }

    public Fiscal(int idFiscal, String nombreFiscal, String apellidoFiscal, Jornada jornada, int edadFiscal, String correoFiscal,
                  String telefono, TipoFiscal tipoFiscal, boolean activo,
                  Establecimiento establecimientoVotacion, Establecimiento establecimientoAsignado, Direccion direccion, Mesa mesa) {
        this.idFiscal = idFiscal;
        this.nombreFiscal = nombreFiscal;
        this.apellidoFiscal = apellidoFiscal;
        this.jornada = jornada;
        this.edad = edadFiscal;
        this.correoFiscal = correoFiscal;
        this.telefono = telefono;
        this.tipoFiscal = tipoFiscal;
        this.activo = activo;
        this.establecimientoVotacion = establecimientoVotacion;
        this.establecimientoAsignado = establecimientoAsignado;
        this.direccion = direccion;
        this.mesa = mesa;
    }

    public Integer getIdFiscal() {
        return idFiscal;
    }

    public void setIdFiscal(Integer idFiscal) {
        this.idFiscal = idFiscal;
    }

    public String getNombreFiscal() {
        return nombreFiscal;
    }

    public void setNombreFiscal(String nombreFiscal) {
        this.nombreFiscal = nombreFiscal;
    }

    public Jornada getJornada() {
        return jornada;
    }

    public void setJornada(Jornada jornada) {
        this.jornada = jornada;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getApellidoFiscal() {
        return apellidoFiscal;
    }

    public void setApellidoFiscal(String apellidoFiscal) {
        this.apellidoFiscal = apellidoFiscal;
    }

    public int getEdadFiscal() {
        return edad;
    }

    public void setEdadFiscal(int edadFiscal) {
        this.edad = edadFiscal;
    }

    public String getCorreoFiscal() {
        return correoFiscal;
    }

    public void setCorreoFiscal(String correoFiscal) {
        this.correoFiscal = correoFiscal;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public TipoFiscal getTipoFiscal() {
        return tipoFiscal;
    }

    public void setTipoFiscal(TipoFiscal tipoFiscal) {
        this.tipoFiscal = tipoFiscal;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Establecimiento getEstablecimientoVotacion() {
        return establecimientoVotacion;
    }

    public void setEstablecimientoVotacion(Establecimiento establecimientoVotacion) {
        this.establecimientoVotacion = establecimientoVotacion;
    }

    public Establecimiento getEstablecimientoAsignado() {
        return establecimientoAsignado;
    }

    public void setEstablecimientoAsignado(Establecimiento establecimientoAsignado) {
        this.establecimientoAsignado = establecimientoAsignado;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }
}
