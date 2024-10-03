package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Plato;
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class AdministradorRestauranteAdapter extends RecyclerView.Adapter<AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder>{

    private List<Plato> listaPlatos;
    private Context context;

    @NonNull
    @Override
    public AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_plato, parent, false);
        return new AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder holder, int position) {
        Plato p = listaPlatos.get(position);
        holder.plato = p;

        TextView textViewId = holder.itemView.findViewById(R.id.platoRestaurante);
        textViewId.setText(p.getNombre());
        TextView textViewPrecio = holder.itemView.findViewById(R.id.precioRestaurante);
        textViewPrecio.setText(String.format("S/.%.2f", p.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }

    public class AdministradorRestauranteViewHolder extends RecyclerView.ViewHolder{
        Plato plato;
        public AdministradorRestauranteViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<Plato> getListaPlatos() {
        return listaPlatos;
    }

    public void setListaPlatos(List<Plato> listaPlatos) {
        this.listaPlatos = listaPlatos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
