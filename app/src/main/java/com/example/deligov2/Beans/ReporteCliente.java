package com.example.deligov2.Beans;

public class ReporteCliente {

    private int id;
    private String nombre;
    private String ultimoPedido;
    private int cantidadPedidos;
    private float gasto;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUltimoPedido() {
        return ultimoPedido;
    }

    public void setUltimoPedido(String ultimoPedido) {
        this.ultimoPedido = ultimoPedido;
    }

    public int getCantidadPedidos() {
        return cantidadPedidos;
    }

    public void setCantidadPedidos(int cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }

    public float getGasto() {
        return gasto;
    }

    public void setGasto(float gasto) {
        this.gasto = gasto;
    }
}
