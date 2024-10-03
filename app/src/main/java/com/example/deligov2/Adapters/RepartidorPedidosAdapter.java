package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.PedidoPorSolicitar;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.R;

import java.util.List;

public class RepartidorPedidosAdapter extends RecyclerView.Adapter<RepartidorPedidosAdapter.RepartidorPedidosViewHolder> {

    private List<PedidoRepartidor> listaPedidos;
    private Context context;

    @NonNull
    @Override
    public RepartidorPedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_pedido, parent, false);
        return new RepartidorPedidosViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorPedidosViewHolder holder, int position) {
        PedidoRepartidor e = listaPedidos.get(position) ;
        holder.pedido = e;
        TextView idOrder = holder.itemView.findViewById(R.id.orderIdPedidos);
        idOrder.setText("#" + e.getIdPedidoRepartidor()) ;
        TextView state = holder. itemView.findViewById(R.id.statePedido);
        state.setText("Estado: " + e.getEstado());
        TextView price = holder.itemView.findViewById(R.id.pricesPedidos);
        if(price!=null){
            price.setText("Precio : S/."+ e.getPrecio());
            price.setId(e.getIdPedidoRepartidor());
        }
    }
    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class RepartidorPedidosViewHolder extends RecyclerView.ViewHolder {
        PedidoRepartidor pedido;
        public RepartidorPedidosViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    public List<PedidoRepartidor> getListaPedidosRepartidor() {
        return listaPedidos;
    }

    public void setListaPedidosRepartidor(List<PedidoRepartidor> listaPedidosRepartidor) {
        this.listaPedidos = listaPedidosRepartidor;
    }
}
