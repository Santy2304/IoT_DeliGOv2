package com.example.deligov2.Beans;

public class PedidoPorRestaurante {

    private int idOrder;
    private String nombreRestaurante;
    private String state;
    private float price; //suma de todos los precios de los pedidos
    private String direccion;
    private int idRepartidor; //0 por defecto, luego se le asigna cuando el repartidor acepte

    public PedidoPorRestaurante(int idOrder, String nombreRestaurante, String state, float price, String direccion, int idRepartidor) {
        this.idOrder = idOrder;
        this.nombreRestaurante = nombreRestaurante;
        this.state = state;
        this.price = price;
        this.direccion = direccion;
        this.idRepartidor = idRepartidor;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public String getNombreRestaurante() {
        return nombreRestaurante;
    }

    public void setNombreRestaurante(String nombreRestaurante) {
        this.nombreRestaurante = nombreRestaurante;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(int idRepartidor) {
        this.idRepartidor = idRepartidor;
    }
}
