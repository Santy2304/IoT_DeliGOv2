package com.example.deligov2.Adapters;

import android.content.Context;

import com.example.deligov2.Beans.NotificacionesRepartidor;
import com.example.deligov2.Beans.PedidoPorSolicitar;

import java.util.ArrayList;
import java.util.List;

public class RepartidorHistorialPedidosAdapter {

    ArrayList<PedidoPorSolicitar>;

    //Getter y setter lista
    public List<PedidoPorSolicitar> getListaNotificaciones() {
        return listaPedidosPorSolicitar;
    }

    public void setListaNotificaciones(List<PedidoPorSolicitar> listaPedidosPorSolicitar) {
        this.listaPedidosPorSolicitar = listaPedidosPorSolicitar;
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    //Getter y setter context
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
