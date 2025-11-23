package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoPiso {
    @JsonAlias({"idPiso", "id_piso"})
    private Integer idPiso;
    private String nombre;
    private boolean activo;

    public TipoPiso() {}

    public TipoPiso(String nombre, boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public TipoPiso(Integer idPiso, String nombre, boolean activo) {
        this.idPiso = idPiso;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Integer getIdPiso() {
        return idPiso;
    }

    public void setIdPiso(Integer idPiso) {
        this.idPiso = idPiso;
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
