package com.example.deligov2.DTO;

import java.util.Date;

public class Logs {
    private String idLog;
    private String info;
    private Date fecha;

    public Logs(String idLog, String info, Date fecha) {
        this.idLog = idLog;
        this.info = info;
        this.fecha = fecha;
    }

    public String getIdLog() {
        return idLog;
    }

    public void setIdLog(String idLog) {
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
