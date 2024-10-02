package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.NotificacionesRepartidor;
import com.example.deligov2.Beans.PedidoPorSolicitar;
import com.example.deligov2.R;

import java.util.List;

public class RepartidorNotificacionesAdapter extends RecyclerView.Adapter<RepartidorNotificacionesAdapter.RepartidorNotificacionesViewHolder>{
    //Inicializamos la lista
    private List<NotificacionesRepartidor> listaNotificaciones;
    private Context context;

    @NonNull
    @Override
    public RepartidorNotificacionesAdapter.RepartidorNotificacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_pedido, parent, false);
        return new RepartidorNotificacionesAdapter.RepartidorNotificacionesViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorNotificacionesAdapter.RepartidorNotificacionesViewHolder holder, int position) {
        NotificacionesRepartidor e = listaNotificaciones.get(position) ;
        holder.notificacion = e;
        //Acá seteamos los valores que iran en ls iterables de recyclerView
        TextView idOrder = holder.itemView.findViewById(R.id.orderIdPedidos);
        TextView state = holder. itemView.findViewById(R.id.statePedido);
        TextView price = holder.itemView.findViewById(R.id.pricesPedidos);
    }


    public class RepartidorNotificacionesViewHolder extends RecyclerView.ViewHolder {
        NotificacionesRepartidor notificacion;
        public RepartidorNotificacionesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    //Getter y setter lista
    public List<NotificacionesRepartidor> getListaNotificaciones() {
        return listaNotificaciones;
    }

    public void setListaNotificaciones(List<NotificacionesRepartidor> listaPedidosPorSolicitar) {
        this.listaNotificaciones = listaPedidosPorSolicitar;
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
