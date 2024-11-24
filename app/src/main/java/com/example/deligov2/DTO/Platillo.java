package com.example.deligov2.DTO;

import java.io.Serializable;

public class Platillo implements Serializable {

    private String id;
    private String nombre;
    private String descripcion;
    private float precio;
    private String idRestaurante;
    private int cantVentaTotal;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public float getPrecio() {
        return precio;
    }

    public void setPrecio(float precio) {
        this.precio = precio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
