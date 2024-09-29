package com.example.deligov2.Beans;

import java.util.Date;

public class Logs {
    private int idLog;
    private String info;
    private Date fecha;

    public Logs(int idLog, String info, Date fecha) {
        this.idLog = idLog;
        this.info = info;
        this.fecha = fecha;
    }

    public int getIdLog() {
        return idLog;
    }

    public void setIdLog(int idLog) {
        this.idLog = idLog;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
}
