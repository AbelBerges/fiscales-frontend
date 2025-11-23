package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Calle {
    @JsonAlias({"idCalle", "id_calle"})
    private int idCalle;
    private String nombre;
    private boolean activa;

    public Calle() {}

    public Calle(String nombre, boolean activa) {
        this.nombre = nombre;
        this.activa = activa;
    }

    public Calle(int idCalle, String nombre, boolean activa) {
        this.idCalle = idCalle;
        this.nombre = nombre;
        this.activa = activa;
    }

    public int getIdCalle() {
        return idCalle;
    }

    public void setIdCalle(int idCalle) {
        this.idCalle = idCalle;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
