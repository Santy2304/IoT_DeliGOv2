package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionesViewHolder> {

    private List<Notificaciones> listaNotificaciones;
    private Context context;

    @NonNull
    @Override
    public NotificacionesAdapter.NotificacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_notificaciones, parent, false);
        return new NotificacionesAdapter.NotificacionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionesAdapter.NotificacionesViewHolder holder, int position) {
        Notificaciones n = listaNotificaciones.get(position);
        holder.notificaciones = n;

        TextView textViewOrder = holder.itemView.findViewById(R.id.orderId);
        textViewOrder.setText(n.getIdCompra() +"");

        LocalDateTime fechaHora = n.getFecha();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraFormateada = fechaHora.format(formatter);

        TextView textViewHorario = holder.itemView.findViewById(R.id.notiDate);
        textViewHorario.setText(fechaHoraFormateada);

        TextView textViewContent = holder.itemView.findViewById(R.id.contentId);
        textViewContent.setText(n.getContenido());
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public List<Notificaciones> getListaNotificaciones() {
        return listaNotificaciones;
    }

    public void setListaNotificaciones(List<Notificaciones> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public class NotificacionesViewHolder extends RecyclerView.ViewHolder{
        Notificaciones notificaciones;
        public NotificacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView button = itemView.findViewById(R.id.goToDetails);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClienteTrackingActivity.class);
                intent.putExtra("idCompra",notificaciones.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }
}
