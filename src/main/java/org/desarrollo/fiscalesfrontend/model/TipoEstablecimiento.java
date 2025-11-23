package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoEstablecimiento {

    @JsonAlias({"id_tipo_establecimiento", "idTipoEstablecimiento"})
    private Integer idTipoEstablecimiento;
    private String tipo;
    private boolean activo;

    public TipoEstablecimiento() {}

    public TipoEstablecimiento(String tipo, boolean activo) {
        this.tipo = tipo;
        this.activo = activo;
    }

    public TipoEstablecimiento(Integer idTipoEstablecimiento, String tipo, boolean activo) {
        this.idTipoEstablecimiento = idTipoEstablecimiento;
        this.tipo = tipo;
        this.activo = activo;
    }

    public Integer getIdTipoEstablecimiento() {
        return idTipoEstablecimiento;
    }

    public void setIdTipoEstablecimiento(Integer idTipoEstablecimiento) {
        this.idTipoEstablecimiento = idTipoEstablecimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    @Override
    public String toString() {
        return this.tipo;
    }
}
