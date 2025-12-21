package org.desarrollo.fiscalesfrontend.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.ArrayList;
import java.util.List;

public class Establecimiento {
    @JsonAlias({"id_establecimiento", "idEstablecimiento"})
    private Integer idEstablecimiento;
    @JsonAlias({"nombre_establecimiento", "nombre"})
    private String nombre;
    private String descripcion;
    private List<Mesa> mesas = new ArrayList<>();
    private Direccion direccion;
    private TipoEstablecimiento tipoEstablecimiento;
    private boolean activo;

    public Establecimiento() {}

    public Establecimiento(String nombreEstablecimiento, String descripcion, Direccion direccion,
                           TipoEstablecimiento tipoEstablecimiento, boolean activo) {
        this.nombre = nombreEstablecimiento;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.tipoEstablecimiento = tipoEstablecimiento;
        this.activo =activo;
    }

    public Establecimiento(int idEstablecimiento, String nombreEstablecimiento, String descripcion, Direccion direccion,
                           TipoEstablecimiento tipoEstablecimiento, boolean activo) {
        this.idEstablecimiento = idEstablecimiento;
        this.nombre = nombreEstablecimiento;
        this.descripcion = descripcion;
        this.direccion = direccion;
        this.tipoEstablecimiento = tipoEstablecimiento;
        this.activo = activo;
    }

    public Integer getIdEstablecimiento() {
        return idEstablecimiento;
    }

    public void setIdEstablecimiento(Integer idEstablecimiento) {
        this.idEstablecimiento = idEstablecimiento;
    }

    public String getNombreEstablecimiento() {
        return nombre;
    }

    public void setNombreEstablecimiento(String nombreEstablecimiento) {
        this.nombre = nombreEstablecimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Mesa> getMesas() {
        return mesas;
    }

    public void setMesas(List<Mesa> mesas) {
        this.mesas = mesas;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

    public TipoEstablecimiento getTipoEstablecimiento() {
        return tipoEstablecimiento;
    }

    public void setTipoEstablecimiento(TipoEstablecimiento tipoEstablecimiento) {
        this.tipoEstablecimiento = tipoEstablecimiento;
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
