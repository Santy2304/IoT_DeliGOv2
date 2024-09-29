package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.R;

import java.util.List;

public class RestaurantesClientesAdapter extends RecyclerView.Adapter<RestaurantesClientesAdapter.RestaurantViewHolder> {
    private List<Restaurante> listaRestaurantes;
    private Context context;

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_restaurantes_clientes, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        Restaurante r = listaRestaurantes.get(position);
        holder.restaurante = r;

        TextView textViewName = holder.itemView.findViewById(R.id.textName);
        textViewName.setText(r.getNombre());

        TextView textViewHorario = holder.itemView.findViewById(R.id.textAtention);
        textViewHorario.setText(r.getHorario());
    }

    @Override
    public int getItemCount() {
        return listaRestaurantes.size();
    }


    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        Restaurante restaurante;
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            Button button = itemView.findViewById(R.id.go_button);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClienteRestaurantActivity.class);
                intent.putExtra("idRestaurante",restaurante.getId());
                itemView.getContext().startActivity(intent);
            });
        }

    }

    public List<Restaurante> getListaRestaurantes() {
        return listaRestaurantes;
    }

    public void setListaRestaurantes(List<Restaurante> listaRestaurantes) {
        this.listaRestaurantes = listaRestaurantes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
