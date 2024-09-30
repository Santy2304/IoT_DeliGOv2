package com.example.deligov2.Beans;

public class RestauranteSA {

    private String nombre;
    private String horario;
    private String[] categorias;
    private int idAdmin;
    private float monto;
    private boolean estado;
    private int id;


    public RestauranteSA(String nombre, String horario, String[] categorias, int idAdmin, float monto, int id, boolean estado) {
        this.nombre = nombre;
        this.horario = horario;
        this.categorias = categorias;
        this.idAdmin = idAdmin;
        this.monto = monto;
        this.id = id;
        this.estado = estado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public float getMonto() {
        return monto;
    }

    public void setMonto(float monto) {
        this.monto = monto;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
