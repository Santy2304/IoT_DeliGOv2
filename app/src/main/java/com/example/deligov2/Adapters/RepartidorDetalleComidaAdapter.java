package com.example.deligov2.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RepartidorDetalleComidaAdapter extends RecyclerView.Adapter<RepartidorDetalleComidaAdapter.RepartidorDetalleComidaViewHolder>{

    private List<Comida> lista;
    private Context context;

    @NonNull
    @Override
    public RepartidorDetalleComidaAdapter.RepartidorDetalleComidaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_detalle_comida_pedido, parent, false);
        return new RepartidorDetalleComidaAdapter.RepartidorDetalleComidaViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorDetalleComidaAdapter.RepartidorDetalleComidaViewHolder holder, int position) {
        Comida e = lista.get(position) ;
        holder.elemento = e;
        TextView nombreComida = holder.itemView.findViewById(R.id.nombreComida);
        TextView cantidad = holder.itemView.findViewById(R.id.cantidad_comida);
        nombreComida.setText(e.getNombreComida());
        cantidad.setText( e.getCantidad() + " Unidades");
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+e.getIdRestaurante()+"/"+e.getIdComida()+"/plato.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.imageView);
        }).addOnFailureListener(ec -> {
            holder.imageView.setImageResource(R.drawable.camara_icon);
        });
    }
    @Override
    public int getItemCount() {
        return lista.size();
    }
    public class RepartidorDetalleComidaViewHolder extends RecyclerView.ViewHolder {
        Comida elemento;
        ImageView imageView;
        public RepartidorDetalleComidaViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ImageFood);

        }}

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public List<Comida> getLista() {
        return lista;
    }
    public void setLista(List<Comida> lista) {
        this.lista = lista;
    }
    public void buscarComidaPorId(String idComida, Runnable runnable){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Platos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Platillo.class)).getId()).equals(idComida)){
                                runnable.run();
                            }
                        }
                    }
                });
    }
}
