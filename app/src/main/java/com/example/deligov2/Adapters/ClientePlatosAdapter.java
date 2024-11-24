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
import com.example.deligov2.Beans.Platillo;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientePlatosAdapter extends RecyclerView.Adapter<ClientePlatosAdapter.ClientePlatosViewHolder> {
    private List<Platillo> listaPlatos;
    private Context context;
    private OnPlatoClickListener onPlatoClickListener;

    @NonNull
    @Override
    public ClientePlatosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_food, parent, false);
        return new ClientePlatosAdapter.ClientePlatosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientePlatosViewHolder holder, int position) {
        Platillo p = listaPlatos.get(position);
        holder.plato = p;

        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(p.getNombre());
        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrecio);
        textViewPrice.setText(String.format("S/ %.2f", p.getPrecio()));

        ExtendedFloatingActionButton btnAgregar = holder.itemView.findViewById(R.id.btnAgregar);
        btnAgregar.setOnClickListener(v -> {
            if (onPlatoClickListener != null) {
                onPlatoClickListener.onPlatoClick(p); // Notifica el click a la actividad
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }


    public class ClientePlatosViewHolder extends RecyclerView.ViewHolder{
        Platillo plato;
        public ClientePlatosViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface OnPlatoClickListener {
        void onPlatoClick(Platillo plato);
    }

    public void setOnPlatoClickListener(OnPlatoClickListener listener) {
        this.onPlatoClickListener = listener;
    }
    public List<Platillo> getListaPlatos() {
        return listaPlatos;
    }

    public void setListaPlatos(List<Platillo> listaPlatos) {
        this.listaPlatos = listaPlatos;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
