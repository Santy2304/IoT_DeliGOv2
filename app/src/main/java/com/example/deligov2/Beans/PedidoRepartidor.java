package com.example.deligov2.Beans;

import java.util.ArrayList;

public class PedidoRepartidor {
    private Integer idPedidoRepartidor;
    private String estado;
    private Float precio;
    private String direccionDelivery;
    private String direccionRestaurante;
    private Float precioDelivery;
    private ArrayList<Comida> comida;

    public Integer getIdPedidoRepartidor() {
        return idPedidoRepartidor;
    }

    public void setIdPedidoRepartidor(Integer idPedidoRepartidor) {
        this.idPedidoRepartidor = idPedidoRepartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public String getDireccionDelivery() {
        return direccionDelivery;
    }

    public void setDireccionDelivery(String direccionDelivery) {
        this.direccionDelivery = direccionDelivery;
    }

    public String getDireccionRestaurante() {
        return direccionRestaurante;
    }

    public void setDireccionRestaurante(String direccionRestaurante) {
        this.direccionRestaurante = direccionRestaurante;
    }

    public Float getPrecioDelivery() {
        return precioDelivery;
    }

    public void setPrecioDelivery(Float precioDelivery) {
        this.precioDelivery = precioDelivery;
    }

    public ArrayList<Comida> getComida() {
        return comida;
    }

    public void setComida(ArrayList<Comida> comida) {
        this.comida = comida;
    }
}
