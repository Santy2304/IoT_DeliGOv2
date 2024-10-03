package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.ReporteComida;
import com.example.deligov2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdministradorReporteComidaAdapter extends RecyclerView.Adapter<AdministradorReporteComidaAdapter.AdministradorReportesViewHolder>{

    private List<ReporteComida> listaReportes;
    private Context context;

    @NonNull
    @Override
    public AdministradorReporteComidaAdapter.AdministradorReportesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_reporte_comida, parent, false);
        return new AdministradorReporteComidaAdapter.AdministradorReportesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorReporteComidaAdapter.AdministradorReportesViewHolder holder, int position) {
        ReporteComida r = listaReportes.get(position);
        holder.reporte = r;

        TextView textViewId = holder.itemView.findViewById(R.id.idReporteComida);
        textViewId.setText(r.getId());
        TextView textViewFood = holder.itemView.findViewById(R.id.nombrePlatoReporte);
        textViewFood.setText(r.getPlato());
        MaterialButton buttonCantidad = holder.itemView.findViewById(R.id.cantidadVendida);
        buttonCantidad.setText(r.getCantidadVendida());
        MaterialButton buttonGanancia = holder.itemView.findViewById(R.id.ganancia);
        buttonGanancia.setText(String.format("S/.%f", r.getGanancia()));
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public class AdministradorReportesViewHolder extends RecyclerView.ViewHolder{
        ReporteComida reporte;
        public AdministradorReportesViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<ReporteComida> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<ReporteComida> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
