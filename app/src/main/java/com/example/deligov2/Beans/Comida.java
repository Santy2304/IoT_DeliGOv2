package com.example.deligov2.Beans;

public class Comida {
    private int idComida;
    private String nombreComida;
    private Integer cantidad;

    public int getIdComida() {
        return idComida;
    }

    public void setIdComida(int idComida) {
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

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    //Constructor
    public Comida(int idComida, String nombreComida, Integer cantidad) {
        this.idComida = idComida;
        this.nombreComida = nombreComida;
        this.cantidad = cantidad;
    }
}
