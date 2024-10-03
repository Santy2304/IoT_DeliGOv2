package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.R;

import java.util.List;

public class AdministradorDetalleCompraAdapter extends RecyclerView.Adapter<AdministradorDetalleCompraAdapter.AdministradorDetalleViewHolder>{

    private List<DetalleCompra> listaDetalles;
    private Context context;

    @NonNull
    @Override
    public AdministradorDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_detalle_compra, parent, false);
        return new AdministradorDetalleCompraAdapter.AdministradorDetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorDetalleCompraAdapter.AdministradorDetalleViewHolder holder, int position) {
        DetalleCompra d = listaDetalles.get(position);
        holder.detalleCompra = d;

        TextView textViewName = holder.itemView.findViewById(R.id.nombrePlato);
        textViewName.setText(d.getPlato());
        TextView textViewCantidad = holder.itemView.findViewById(R.id.cantidad);
        textViewCantidad.setText(String.format("%d unidades", d.getCantidad()));
    }

    @Override
    public int getItemCount() {
        return listaDetalles.size();
    }

    public class AdministradorDetalleViewHolder extends RecyclerView.ViewHolder{
        DetalleCompra detalleCompra;
        public AdministradorDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<DetalleCompra> getListaDetalles() {
        return listaDetalles;
    }

    public void setListaDetalles(List<DetalleCompra> listaDetalles) {
        this.listaDetalles = listaDetalles;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
