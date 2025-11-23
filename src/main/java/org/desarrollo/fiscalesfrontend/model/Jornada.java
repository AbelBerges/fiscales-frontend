package org.desarrollo.fiscalesfrontend.model;


import com.fasterxml.jackson.annotation.JsonAlias;

public class Jornada {
    @JsonAlias({"id_jornada", "idJornada"})
    private Integer idJornada;
    @JsonAlias({"tipo_jornada", "tipoJornada"})
    private String tipoJornada;

    public Jornada() {
    }
    public Jornada(String tipoJornada) {
        this.tipoJornada = tipoJornada;
    }

    public Jornada(Integer idJornada, String tipoJornada) {
        this.idJornada = idJornada;
        this.tipoJornada = tipoJornada;
    }

    public Integer getIdJornada() {
        return idJornada;
    }

    public void setIdJornada(Integer idJornada) {
        this.idJornada = idJornada;
    }

    public String getTipoJornada() {
        return tipoJornada;
    }

    public void setTipoJornada(String tipoJornada) {
        this.tipoJornada = tipoJornada;
    }

    @Override
    public String toString() {
        return this.tipoJornada;
    }
}
