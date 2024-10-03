package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Administrador.AdministradorReporteClientesActivity;
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdministradorReporteClientesAdapter extends RecyclerView.Adapter<AdministradorReporteClientesAdapter.AdministradorReportesViewHolder>{

    private List<ReporteCliente> listaReportes;
    private Context context;

    @NonNull
    @Override
    public AdministradorReporteClientesAdapter.AdministradorReportesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_reporte_cliente, parent, false);
        return new AdministradorReporteClientesAdapter.AdministradorReportesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorReporteClientesAdapter.AdministradorReportesViewHolder holder, int position) {
        ReporteCliente r = listaReportes.get(position);
        holder.reporte = r;

        TextView textViewId = holder.itemView.findViewById(R.id.idReporteCliente);
        textViewId.setText(r.getId());
        TextView textViewName = holder.itemView.findViewById(R.id.nombreCliente);
        textViewName.setText(r.getNombre());
        TextView textViewFecha = holder.itemView.findViewById(R.id.fechaUltimoPedido);
        textViewFecha.setText(String.format("Fecha: %f", r.getUltimoPedido()));
        MaterialButton buttonCantidad = holder.itemView.findViewById(R.id.cantidadPedidos);
        buttonCantidad.setText(r.getCantidadPedidos());
        MaterialButton buttonGasto = holder.itemView.findViewById(R.id.gastoTotal);
        buttonGasto.setText(String.format("S/.%f", r.getGasto()));
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public class AdministradorReportesViewHolder extends RecyclerView.ViewHolder{
        ReporteCliente reporte;
        public AdministradorReportesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<ReporteCliente> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<ReporteCliente> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
