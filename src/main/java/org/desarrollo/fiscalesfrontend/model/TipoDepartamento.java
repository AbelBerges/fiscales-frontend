package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TipoDepartamento {
    @JsonAlias({"id_departamento", "idDepartamento"})
    private Integer idDepartamento;
    private String nombre;
    private boolean activo;


    public TipoDepartamento() {}

    public TipoDepartamento(String nombre, boolean activo) {
        this.nombre = nombre;
        this.activo = activo;
    }

    public TipoDepartamento(Integer idDepartamento, String nombre, boolean activo) {
        this.idDepartamento = idDepartamento;
        this.nombre = nombre;
        this.activo = activo;
    }

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
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
