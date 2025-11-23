package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Mesa {
    @JsonAlias({"id_mesa", "idMesa"})
    private Integer idMesa;
    @JsonAlias({"numero_mesa", "numeroMesa"})
    private Integer numeroMesa;
    @JsonAlias({"id_establecimiento", "idEstablecimiento"})
    private Establecimiento establecimiento;

    public Mesa() {}
    public Mesa(Integer idMesa, Integer numeroMesa, Establecimiento establecimiento) {
        this.idMesa = idMesa;
        this.numeroMesa = numeroMesa;
        this.establecimiento = establecimiento;
    }

    public Integer getIdMesa() {
        return this.idMesa;
    }
    public void setIdMesa(Integer idMesa) {
        this.idMesa = idMesa;
    }

    public Integer getNumeroMesa() {
        return numeroMesa;
    }
    public void setNumeroMesa(Integer numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public Establecimiento getEstablecimiento() {
        return this.establecimiento;
    }
    public void setEstablecimiento(Establecimiento establecimiento) {
        this.establecimiento = establecimiento;
    }
}
