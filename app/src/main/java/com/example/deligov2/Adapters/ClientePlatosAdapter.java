package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientePlatosAdapter extends RecyclerView.Adapter<ClientePlatosAdapter.ClientePlatosViewHolder> {
    private List<Plato> listaPlatos;
    private Context context;

    @NonNull
    @Override
    public ClientePlatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_food, parent, false);
        return new ClientePlatosAdapter.ClientePlatosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientePlatosViewHolder holder, int position) {
        Plato p = listaPlatos.get(position);
        holder.plato = p;

        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(p.getNombre());
        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrecio);
        textViewPrice.setText(String.format("S/ %.2f", p.getPrecio()));
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }


    public class ClientePlatosViewHolder extends RecyclerView.ViewHolder{
        Plato plato;
        public ClientePlatosViewHolder(@NonNull View itemView) {
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
