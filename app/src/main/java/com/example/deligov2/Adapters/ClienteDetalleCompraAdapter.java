package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClienteDetalleCompraAdapter extends RecyclerView.Adapter<ClienteDetalleCompraAdapter.ClienteDetalleViewHolder>{
    private List<Platillo> listafood;
    private Context context;
    private ArrayList<Integer> listaCantidades = new ArrayList<>();
    private ArrayList<Float> listaPrecios = new ArrayList<>();
    @NonNull
    @Override
    public ClienteDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_boughtfood, parent, false);
        return new ClienteDetalleCompraAdapter.ClienteDetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteDetalleViewHolder holder, int position) {
        Platillo v = listafood.get(position);
        holder.plato = v;

        TextView textViewCant = holder.itemView.findViewById(R.id.cantFood);
        textViewCant.setText("Cantidad: "+listaCantidades.get(position));

        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(v.getNombre());

        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrice);
        textViewPrice.setText(String.format("S/ %.2f",listaPrecios.get(position)));

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+v.getIdRestaurante()+"/"+v.getId()+"/plato.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.imageView);
        }).addOnFailureListener(e -> {
            holder.imageView.setImageResource(R.drawable.camara_icon);
        });

    }

    @Override
    public int getItemCount() {
        return listafood.size();
    }


    public class ClienteDetalleViewHolder extends RecyclerView.ViewHolder{
        Platillo plato;
        ImageView imageView;
        public ClienteDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imagen);

        }
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Platillo> getListafood() {
        return listafood;
    }

    public void setListafood(List<Platillo> listafood) {
        this.listafood = listafood;
    }

    public ArrayList<Integer> getListaCantidades() {
        return listaCantidades;
    }

    public void setListaCantidades(ArrayList<Integer> listaCantidades) {
        this.listaCantidades = listaCantidades;
    }

    public ArrayList<Float> getListaPrecios() {
        return listaPrecios;
    }

    public void setListaPrecios(ArrayList<Float> listaPrecios) {
        this.listaPrecios = listaPrecios;
    }
}
