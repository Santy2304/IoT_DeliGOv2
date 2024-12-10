package com.example.deligov2.Beans;

import java.io.Serializable;

public class Comida implements Serializable {
    private String idComida;
    private String nombreComida;
    private Integer cantidad;
    private String idRestaurante;

    public String getIdComida() {
        return idComida;
    }

    public void setIdComida(String idComida) {
        this.idComida = idComida;
    }

    public String getNombreComida() {
        return nombreComida;
    }

    public void setNombreComida(String nombreComida) {
        this.nombreComida = nombreComida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    //Constructor
    public Comida(String idComida, String nombreComida, Integer cantidad) {
        this.idComida = idComida;
        this.nombreComida = nombreComida;
        this.cantidad = cantidad;
    }
    public Comida() {
    }
}
