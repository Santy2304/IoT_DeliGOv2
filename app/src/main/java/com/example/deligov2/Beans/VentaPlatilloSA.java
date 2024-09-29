package com.example.deligov2.Beans;

public class VentaPlatilloSA {
    private int idPlatillo;
    private String nombre;
    private float price;
    private int cantidad;
    private int idRestaurante;

    public VentaPlatilloSA(int idPlatillo, String nombre, float price, int cantidad, int idRestaurante) {
        this.idPlatillo = idPlatillo;
        this.nombre = nombre;
        this.price = price;
        this.cantidad = cantidad;
        this.idRestaurante = idRestaurante;
    }

    public int getIdPlatillo() {
        return idPlatillo;
    }

    public void setIdPlatillo(int idPlatillo) {
        this.idPlatillo = idPlatillo;
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

    public int getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(int idRestaurante) {
        this.idRestaurante = idRestaurante;
    }
}
