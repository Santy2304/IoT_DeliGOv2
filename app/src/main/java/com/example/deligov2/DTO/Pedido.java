package com.example.deligov2.DTO;

import com.google.type.DateTime;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {

    private String idRestaurante;
    private String id;
    private ArrayList<String> idListaPlatos;
    private ArrayList<Float> preciosActuales;
    private ArrayList<Integer> listaCantidades;
    private String idRepartidor;
    private String idUsuario;
    private String estado;
    private String hora;


    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
    }

    public ArrayList<Integer> getListaCantidades() {
        return listaCantidades;
    }

    public void setListaCantidades(ArrayList<Integer> listaCantidades) {
        this.listaCantidades = listaCantidades;
    }

    public String getIdRepartidor() {
        return idRepartidor;
    }

    public void setIdRepartidor(String idRepartidor) {
        this.idRepartidor = idRepartidor;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }


    public ArrayList<Float> getPreciosActuales() {
        return preciosActuales;
    }

    public void setPreciosActuales(ArrayList<Float> preciosActuales) {
        this.preciosActuales = preciosActuales;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public ArrayList<String> getIdListaPlatos() {
        return idListaPlatos;
    }

    public void setIdListaPlatos(ArrayList<String> idListaPlatos) {
        this.idListaPlatos = idListaPlatos;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
