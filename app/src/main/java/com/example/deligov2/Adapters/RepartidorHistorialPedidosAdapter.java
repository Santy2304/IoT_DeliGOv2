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
import com.example.deligov2.Beans.NotificacionesRepartidor;
import com.example.deligov2.Beans.PedidoPorSolicitar;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepartidorHistorialPedidosAdapter extends RecyclerView.Adapter<RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder> {

    private List<Pedido> listaPedidos;
    private Context context;

    @NonNull
    @Override
    public RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_historial_pedidos, parent, false);
        return new RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder holder, int position) {
        Pedido e = listaPedidos.get(position) ;
        holder.pedidoRepartidor = e;
        //Acá seteamos los valores que iran en ls iterables de recyclerView
        holder.itemView.setContentDescription(e.getId());
        TextView idOrder = holder.itemView.findViewById(R.id.id_historial_pedido);
        TextView state = holder. itemView.findViewById(R.id.id_estado_pedido);
        TextView date = holder.itemView.findViewById(R.id.id_fecha_pedido);
        TextView price = holder.itemView.findViewById(R.id.id_costo);
        idOrder.setText("#" + e.getId());
        state.setText("Estado : " + e.getEstado());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormateada = dateFormat.format(e.getHora().toDate());
        date.setText("Fecha : " +fechaFormateada);
        price.setText(String.format("Costo delivery : S/.%.2f"  , e.getCostoEnvio()));
        //Ocultamos un lugar
        holder.itemView.setContentDescription(e.getId());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+e.getIdRestaurante()+"/logo.jpg");
        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into( (ImageView) holder.itemView.findViewById(R.id.img));
        }).addOnFailureListener(er -> {
        });
        if(e.getEstado().equals("Entregado")  && e.getId() .equals( holder.itemView.getContentDescription())){
            holder.itemView.findViewById(R.id.layout_mapa).findViewById(R.id.mapa).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.layout_mapa).findViewById(R.id.mapa).setClickable(false);
        }
        holder.itemView.findViewById(R.id.mapa).setContentDescription(e.getId());
        holder.itemView.findViewById(R.id.detail).setContentDescription(e.getId());
    }

    public class RepartidorHistorialPedidosViewHolder extends RecyclerView.ViewHolder {
        Pedido pedidoRepartidor;
        public RepartidorHistorialPedidosViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //Getter y setter lista
    public List<Pedido> getLista () {
        return listaPedidos;
    }
    public void setLista(List<Pedido> listaPedidos) {
        this.listaPedidos = listaPedidos;
    }
    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }
    //Getter y setter context
    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
