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
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class RepartidorPedidosAdapter extends RecyclerView.Adapter<RepartidorPedidosAdapter.RepartidorPedidosViewHolder> {

    private List<Pedido> listaPedidos;
    private Context context;
    @NonNull
    @Override
    public RepartidorPedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_pedido, parent, false);
        return new RepartidorPedidosViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorPedidosViewHolder holder, int position) {
        try{
            Pedido e = listaPedidos.get(position) ;
            holder.pedido = e;
            TextView idOrder = holder.itemView.findViewById(R.id.orderIdPedidos);
            idOrder.setText("#" + e.getId()) ;
            TextView state = holder. itemView.findViewById(R.id.statePedido);
            state.setText("Estado: " + e.getEstado());
            TextView price = holder.itemView.findViewById(R.id.pricesPedidos);
            price.setText("Precio : S/."+ e.getCostoEnvio() );
            FloatingActionButton button = holder.itemView.findViewById(R.id.mapa2);
            button.setContentDescription(e.getId());
            FloatingActionButton button2 = holder.itemView.findViewById(R.id.detalles);
            button2.setContentDescription(e.getId());
            FloatingActionButton button3 = holder.itemView.findViewById(R.id.aceptacionRepartidor2);
            button3.setContentDescription(e.getId());
            //Ahora afectamos a los botones
            holder.itemView.setContentDescription(e.getId());
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference fileRef = storage.getReference().child("restaurantes/"+e.getIdRestaurante()+"/logo.jpg");
            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .placeholder(R.drawable.camara_icon)
                        .error(R.drawable.camara_icon)
                        .into(holder.logo);
            }).addOnFailureListener(error -> {
                holder.logo.setImageResource(R.drawable.camara_icon);
            });
        }catch(Exception error){
            Log.d("ola" , error.getStackTrace().toString());
        }
    }
    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class RepartidorPedidosViewHolder extends RecyclerView.ViewHolder {
        Pedido pedido;
        ImageView logo;
        public RepartidorPedidosViewHolder(@NonNull View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.img);
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public List<Pedido> getListaPedidosRepartidor() {
        return listaPedidos;
    }

    public void setListaPedidosRepartidor(List<Pedido> listaPedidosRepartidor) {
        this.listaPedidos = listaPedidosRepartidor;
    }

}
