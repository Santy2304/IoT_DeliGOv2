package com.example.deligov2.DTO;

import java.io.Serializable;
import java.util.ArrayList;

public class Carrito implements Serializable {

    private String idUsuario;
    private ArrayList<String> idListaPlatos;
    private ArrayList<Integer> listaCantidades;
    private String idRestaurante;

    public String getIdRestaurante() {
        return idRestaurante;
    }

    public void setIdRestaurante(String idRestaurante) {
        this.idRestaurante = idRestaurante;
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


    public ArrayList<Integer> getListaCantidades() {
        return listaCantidades;
    }

    public void setListaCantidades(ArrayList<Integer> listaCantidades) {
        this.listaCantidades = listaCantidades;
    }
}
