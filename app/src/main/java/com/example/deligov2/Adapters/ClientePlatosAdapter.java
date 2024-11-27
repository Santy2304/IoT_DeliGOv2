package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Cliente.ClientePlatoActivity;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class ClientePlatosAdapter extends RecyclerView.Adapter<ClientePlatosAdapter.ClientePlatosViewHolder> {
    private List<Platillo> listaPlatos;
    private Context context;
    private OnPlatoClickListener onPlatoClickListener;
    private List<Boolean> estadoBotones;

    public ClientePlatosAdapter() {
        // Inicializar la lista de estados vacía
        this.estadoBotones = new ArrayList<>();
    }


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

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+p.getIdRestaurante()+"/"+p.getId()+"/plato.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.ImageView);
        }).addOnFailureListener(e -> {
            holder.ImageView.setImageResource(R.drawable.camara_icon);
        });



        ExtendedFloatingActionButton btnAgregar = holder.itemView.findViewById(R.id.btnAgregar);

        if (estadoBotones.get(position)) {
            btnAgregar.setEnabled(false);
        } else {
            btnAgregar.setEnabled(true);
        }

        btnAgregar.setOnClickListener(v -> {
            if (onPlatoClickListener != null) {
                onPlatoClickListener.onPlatoClick(p); // Notifica el click a la actividad

                estadoBotones.set(position, true);
                btnAgregar.setEnabled(false);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }


    public class ClientePlatosViewHolder extends RecyclerView.ViewHolder{
        Platillo plato;
        android.widget.ImageView ImageView;

        public ClientePlatosViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageView = itemView.findViewById(R.id.foodImage);
            ImageView.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClientePlatoActivity.class);
                intent.putExtra("idRestaurante",plato.getId());
                itemView.getContext().startActivity(intent);
            });

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

        this.estadoBotones.clear();
        for (int i = 0; i < listaPlatos.size(); i++) {
            this.estadoBotones.add(false);
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
