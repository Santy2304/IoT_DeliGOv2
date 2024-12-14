package com.example.deligov2.DTO;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ReporteCliente implements Serializable {
    private String idCliente;
    private String idRestaurante;
    private int cantidadPedidos;
    private float totalGastado;
    private com.google.firebase.Timestamp ultimoPedido;

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public int getCantidadPedidos() {
        return cantidadPedidos;
    }

    public void setCantidadPedidos(int cantidadPedidos) {
        this.cantidadPedidos = cantidadPedidos;
    }

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public float getTotalGastado() {
        return totalGastado;
    }

    public void setTotalGastado(float totalGastado) {
        this.totalGastado = totalGastado;
    }

    public Timestamp getUltimoPedido() {
        return ultimoPedido;
    }

    public void setUltimoPedido(Timestamp ultimoPedido) {
        this.ultimoPedido = ultimoPedido;
    }
}
