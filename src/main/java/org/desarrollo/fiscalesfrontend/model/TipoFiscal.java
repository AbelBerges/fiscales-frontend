package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoFiscal {
    @JsonAlias({"id_tipo_fiscal", "idTipoFiscal"})
    private Integer idTipoFiscal;
    private String nombre;
    private boolean activo;

    public TipoFiscal() {}
    public TipoFiscal(String nombre, boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public TipoFiscal(Integer idTipoFiscal, String nombre, boolean activo) {
        this.idTipoFiscal = idTipoFiscal;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Integer getIdTipoFiscal() {
        return idTipoFiscal;
    }

    public void setIdTipoFiscal(Integer idTipoFiscal) {
        this.idTipoFiscal = idTipoFiscal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return this.nombre;
    }
}
