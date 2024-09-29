package com.example.deligov2.Beans;

public class Administrador {
    private int idAdmin;
    private String nombre;
    private String apellido;
    private String correo;
    private boolean estado;
    private String restaurante;
    private String ubicacionRestaurante;
    private String numDocumento;

    public Administrador(int idAdmin, String nombre, String apellido, String correo, boolean estado, String restaurante, String ubicacionRestaurante, String numDocumento) {
        this.idAdmin = idAdmin;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.estado = estado;
        this.restaurante = restaurante;
        this.ubicacionRestaurante = ubicacionRestaurante;
        this.numDocumento = numDocumento;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getRestaurante() {
        return restaurante;
    }

    public void setRestaurante(String restaurante) {
        this.restaurante = restaurante;
    }

    public String getUbicacionRestaurante() {
        return ubicacionRestaurante;
    }

    public void setUbicacionRestaurante(String ubicacionRestaurante) {
        this.ubicacionRestaurante = ubicacionRestaurante;
    }

    public String getNumDocumento() {
        return numDocumento;
    }

    public void setNumDocumento(String numDocumento) {
        this.numDocumento = numDocumento;
    }
}
