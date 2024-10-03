package com.example.deligov2.Beans;

public class Solicitud {

    private int id;//Número de solicitud
    private String estado;
    private String fecha;
    private float precioDelivery;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public float getPrecioDelivery() {
        return precioDelivery;
    }

    public void setPrecioDelivery(float precioDelivery) {
        this.precioDelivery = precioDelivery;
    }
}
