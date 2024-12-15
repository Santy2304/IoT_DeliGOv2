package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Administrador.AdministradorEditarPlatoActivity;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdministradorRestauranteAdapter extends RecyclerView.Adapter<AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder>{

    private List<Platillo> listaPlatos;
    private Context context;

    @NonNull
    @Override
    public AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_plato, parent, false);
        return new AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorRestauranteAdapter.AdministradorRestauranteViewHolder holder, int position) {
        Platillo plato = listaPlatos.get(position);

        // Nombre y precio del plato
        holder.textViewNombre.setText(plato.getNombre());
        holder.textViewPrecio.setText(String.format("S/.%.2f", plato.getPrecio()));

        // Cagar imagen del plato desde Storage
        String rutaImagen = "restaurantes/" + plato.getIdRestaurante() + "/" + plato.getId() + "/plato.jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(rutaImagen);

        Glide.with(context)
                .load(storageReference)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_errorimg)
                .into(holder.imageViewPlato);

        // Cambiar el color del botón según la visibilidad
        if (plato.isVisibilidad()) {
            holder.buttonVisibilidad.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.light_yellow)
            );
        } else {
            holder.buttonVisibilidad.setBackgroundTintList(
                    ContextCompat.getColorStateList(context, R.color.gray)
            );
        }

        // Boton para modificar la visibilidad del plato
        holder.buttonVisibilidad.setOnClickListener(v -> {
            boolean nuevaVisibilidad = !plato.isVisibilidad(); // Cambiar el valor actual
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            db.collection("Platos").document(plato.getId())
                    .update("visibilidad", nuevaVisibilidad)
                    .addOnSuccessListener(aVoid -> {
                        plato.setVisibilidad(nuevaVisibilidad); // Actualizar localmente

                        // Cambiar el color del botón según el nuevo estado
                        holder.buttonVisibilidad.setBackgroundTintList(
                                ContextCompat.getColorStateList(context, nuevaVisibilidad ? R.color.light_yellow : R.color.gray)
                        );

                        Toast.makeText(context, "Visibilidad actualizada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, "Error al actualizar visibilidad", Toast.LENGTH_SHORT).show();
                    });
        });

        // Botón para editar el plato
        holder.buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AdministradorEditarPlatoActivity.class);
            intent.putExtra("plato", plato); // Enviar el objeto Plato
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return listaPlatos.size();
    }

    public class AdministradorRestauranteViewHolder extends RecyclerView.ViewHolder{
        TextView textViewNombre, textViewPrecio;
        Button buttonVisibilidad, buttonEdit;
        ImageView imageViewPlato;

        public AdministradorRestauranteViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewNombre = itemView.findViewById(R.id.platoRestaurante);
            textViewPrecio = itemView.findViewById(R.id.precioRestaurante);

            buttonVisibilidad = itemView.findViewById(R.id.visibilidadPlato);
            buttonEdit = itemView.findViewById(R.id.editarPlato);

            imageViewPlato = itemView.findViewById(R.id.imagenPlato);

        }
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
