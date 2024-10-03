package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Plato;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminPlatillosDescription;
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilRepartidor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SuperAdminRestauranteCartaAdapter  extends RecyclerView.Adapter<SuperAdminRestauranteCartaAdapter.RestaurantViewHolder> {
    private List<Plato> listaPlato;
    private Context context;

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_sup_admin_carta_list, parent, false);
        return new SuperAdminRestauranteCartaAdapter.RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Plato carta = listaPlato.get(position);
        holder.plato = carta;

        TextView tvName1 = holder.itemView.findViewById(R.id.tv_nameR);
        tvName1.setText(carta.getNombre());

        TextView tvPrice1 = holder.itemView.findViewById(R.id.tv_priceR);
        tvPrice1.setText(String.format("S/ %.2f", carta.getPrecio()));

    }

    @Override
    public int getItemCount() {
        return listaPlato.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        Plato plato;
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            /*
            FloatingActionButton button1 = itemView.findViewById(R.id.button1);
            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), SuperAdminPlatillosDescription.class);
                    itemView.getContext().startActivity(intent);
                }
            });

             */
        }
    }

    public List<Plato> getListaPlato() {
        return listaPlato;
    }

    public void setListaPlato(List<Plato> listaPlato) {
        this.listaPlato = listaPlato;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

}

