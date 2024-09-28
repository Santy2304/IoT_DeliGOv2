package com.example.deligov2.Beans;

public class Restaurante {

    private String nombbre;
    private String horario;
    private String[] categorias;


    public String getNombre() {
        return nombbre;
    }

    public void setNombre(String nombbre) {
        this.nombbre = nombbre;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String[] getCategorias() {
        return categorias;
    }

    public void setCategorias(String[] categorias) {
        this.categorias = categorias;
    }
}
