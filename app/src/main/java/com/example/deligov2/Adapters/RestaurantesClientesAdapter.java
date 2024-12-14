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

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        textViewHorario.setText("Atención: "+r.getHorario());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+r.getId()+"/logo.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.logo);
        }).addOnFailureListener(e -> {
            holder.logo.setImageResource(R.drawable.camara_icon);
        });

        StorageReference storageReference1 = storage.getReference().child("restaurantes/"+r.getId()+"/banner.jpg");

        storageReference1.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.banner);
        }).addOnFailureListener(e -> {
            holder.banner.setImageResource(R.drawable.camara_icon);
        });

    }

    @Override
    public int getItemCount() {
        return listaRestaurantes.size();
    }


    public class RestaurantViewHolder extends RecyclerView.ViewHolder {
        Restaurante restaurante;
        ImageView banner, logo;
        public RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            Button button = itemView.findViewById(R.id.go_button);
            banner = itemView.findViewById(R.id.banner);
            logo = itemView.findViewById(R.id.imgMsg);
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
