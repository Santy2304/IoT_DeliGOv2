package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Administrador.AdministradorHistorialActivity;
import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.R;

import java.util.List;

public class AdministradorHistorialAdapter extends RecyclerView.Adapter<AdministradorHistorialAdapter.AdministradorSolicitudViewHolder>{

    private List<Solicitud> listaSolicitudes;
    private Context context;

    @NonNull
    @Override
    public AdministradorHistorialAdapter.AdministradorSolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_solicitud, parent, false);
        return new AdministradorHistorialAdapter.AdministradorSolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorHistorialAdapter.AdministradorSolicitudViewHolder holder, int position) {
        Solicitud s = listaSolicitudes.get(position);
        holder.solicitud = s;

        TextView textViewName = holder.itemView.findViewById(R.id.idSolicitud);
        textViewName.setText(s.getId());
        TextView textViewEstado = holder.itemView.findViewById(R.id.estadoSolicitud);
        textViewEstado.setText(String.format("Estado: %s", s.getEstado()));
        TextView textViewFecha = holder.itemView.findViewById(R.id.fechaSolicitud);
        textViewFecha.setText(String.format("Fecha: %s", s.getFecha()));
        TextView textViewCosto = holder.itemView.findViewById(R.id.costoDeliverySolicitud);
        textViewCosto.setText(String.format("Costo delivery: S/.%.2f", s.getPrecioDelivery()));
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public class AdministradorSolicitudViewHolder extends RecyclerView.ViewHolder{
        Solicitud solicitud;
        public AdministradorSolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<Solicitud> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Solicitud> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
