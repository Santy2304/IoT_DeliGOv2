package com.example.deligov2.Beans;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Notificaciones {
    private int id;
    private String contenido;
    private LocalDateTime fecha;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }
}
