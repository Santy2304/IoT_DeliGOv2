package com.example.deligov2.Beans;

public class VentaPlatilloSA {
    private int idVentaPlatillo;
    private String nombre;
    private float price;
    private int cantidad;
    private int idPlato;

    public VentaPlatilloSA(int idVentaPlatillo, String nombre, float price, int cantidad, int idPlato) {
        this.idVentaPlatillo = idVentaPlatillo;
        this.nombre = nombre;
        this.price = price;
        this.cantidad = cantidad;
        this.idPlato = idPlato;
    }


    public int getIdVentaPlatillo() {
        return idVentaPlatillo;
    }

    public void setIdPlatillo(int idPlatillo) {
        this.idVentaPlatillo = idPlatillo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getIdPlato() {
        return idPlato;
    }

    public void setIdPlato(int idRestaurante) {
        this.idPlato = idRestaurante;
    }
}
