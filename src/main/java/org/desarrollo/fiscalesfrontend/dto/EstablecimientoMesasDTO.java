package org.desarrollo.fiscalesfrontend.dto;

public class EstablecimientoMesasDTO {
    private Integer idEstablecimiento;
    private String nombre;
    private Integer cantidadMesas;
    private Integer mesaInicial;
    private Integer mesaFinal;

    public EstablecimientoMesasDTO() {}

    public EstablecimientoMesasDTO(String nombre, Integer cantidadMesas, Integer mesaInicial, Integer mesaFinal) {
        this.nombre = nombre;
        this.cantidadMesas = cantidadMesas;
        this.mesaInicial = mesaInicial;
        this.mesaFinal = mesaFinal;
    }

    public EstablecimientoMesasDTO(Integer idEstablecimiento, String nombre, Integer cantidadMesas, Integer mesaInicial, Integer mesaFinal) {
        this.idEstablecimiento = idEstablecimiento;
        this.nombre = nombre;
        this.cantidadMesas = cantidadMesas;
        this.mesaInicial = mesaInicial;
        this.mesaFinal = mesaFinal;
    }

    public Integer getIdEstablecimiento() {
        return idEstablecimiento;
    }

    public void setIdEstablecimiento(Integer idEstablecimiento) {
        this.idEstablecimiento = idEstablecimiento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getCantidadMesas() {
        return cantidadMesas;
    }

    public void setCantidadMesas(Integer cantidadMesas) {
        this.cantidadMesas = cantidadMesas;
    }

    public Integer getMesaInicial() {
        return mesaInicial;
    }

    public void setMesaInicial(Integer mesaInicial) {
        this.mesaInicial = mesaInicial;
    }

    public Integer getMesaFinal() {
        return mesaFinal;
    }

    public void setMesaFinal(Integer mesaFinal) {
        this.mesaFinal = mesaFinal;
    }
}
