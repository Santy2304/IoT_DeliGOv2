package com.example.deligov2.DTO;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class LogSuper implements Serializable {
    private String info;
    private Timestamp fecha;
//    private String idCliente;
//    private String idRestaurante;
    private String idImage;
    private String tipo;


    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

//    public String getIdCliente() {
//        return idCliente;
//    }
//
//    public void setIdCliente(String idCliente) {
//        this.idCliente = idCliente;
//    }
//
//    public String getIdRestaurante() {
//        return idRestaurante;
//    }
//
//    public void setIdRestaurante(String idRestaurante) {
//        this.idRestaurante = idRestaurante;
//    }


    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }
}
