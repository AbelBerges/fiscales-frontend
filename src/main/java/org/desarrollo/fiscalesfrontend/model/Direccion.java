package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Direccion {
    @JsonAlias({"id_direccion", "idDireccion"})
    private int idDireccion;
    private Calle calle;
    private int altura;
    private TipoPiso tipoPiso;
    private TipoDepartamento tipoDepartamento;

    public Direccion() {}

    public Direccion(Calle calle, int altura, TipoPiso tipoPiso, TipoDepartamento departamento) {
        this.calle = calle;
        this.altura = altura;
        this.tipoPiso = tipoPiso;
        this.tipoDepartamento = departamento;
    }

    public Direccion(int idDireccion, Calle calle, int altura, TipoPiso tipoPiso, TipoDepartamento tipoDepartamento) {
        this.idDireccion = idDireccion;
        this.calle = calle;
        this.altura = altura;
        this.tipoPiso = tipoPiso;
        this.tipoDepartamento = tipoDepartamento;
    }

    public int getIdDireccion() {
        return idDireccion;
    }

    public void setIdDireccion(int idDireccion) {
        this.idDireccion = idDireccion;
    }

    public Calle getCalle() {
        return calle;
    }

    public void setCalle(Calle calle) {
        this.calle = calle;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public TipoPiso getTipoPiso() {
        return tipoPiso;
    }

    public void setTipoPiso(TipoPiso tipoPiso) {
        this.tipoPiso = tipoPiso;
    }

    public TipoDepartamento getTipoDepartamento() {
        return tipoDepartamento;
    }

    public void setTipoDepartamento(TipoDepartamento tipoDepartamento) {
        this.tipoDepartamento = tipoDepartamento;
    }

    @Override
    public String toString() {
        String calle = (this.getCalle() != null) ? this.getCalle().getNombre() : "";
        String piso = (this.getTipoPiso() != null) ? " Piso: " + this.getTipoPiso().getNombre() : "";
        String dpto = (this.getTipoDepartamento() != null) ? " Dpto: " + this.getTipoDepartamento().getNombre() : "";
        return calle + " " + this.getAltura() + piso + dpto;
    }
}
