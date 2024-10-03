package com.example.deligov2.Beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class PedidoRepartidor implements Serializable {
    private Integer idPedidoRepartidor;
    private String estado;
    private float precio;
    private String direccionDelivery;
    private String direccionRestaurante;
    private float precioDelivery;
    private ArrayList<Comida> comida;
    private Date fecha;

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

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
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

    public float getPrecioDelivery() {
        return precioDelivery;
    }

    public void setPrecioDelivery(float precioDelivery) {
        this.precioDelivery = precioDelivery;
    }

    public ArrayList<Comida> getComida() {
        return comida;
    }

    public void setComida(ArrayList<Comida> comida) {
        this.comida = comida;
    }
    // Constructor

    public PedidoRepartidor(Integer idPedidoRepartidor, String estado, Float precio, String direccionDelivery, String direccionRestaurante, Float precioDelivery, ArrayList<Comida> comida) {
        this.idPedidoRepartidor = idPedidoRepartidor;
        this.estado = estado;
        this.precio = precio;
        this.direccionDelivery = direccionDelivery;
        this.direccionRestaurante = direccionRestaurante;
        this.precioDelivery = precioDelivery;
        this.comida = comida;
    }

    public PedidoRepartidor(){};

    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
