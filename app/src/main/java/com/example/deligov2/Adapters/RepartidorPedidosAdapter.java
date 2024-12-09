package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    }
    @Override
    public int getItemCount() {
        return listaPedidos.size();
    }

    public class RepartidorPedidosViewHolder extends RecyclerView.ViewHolder {
        Pedido pedido;
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


    public List<Pedido> getListaPedidosRepartidor() {
        return listaPedidos;
    }

    public void setListaPedidosRepartidor(List<Pedido> listaPedidosRepartidor) {
        this.listaPedidos = listaPedidosRepartidor;
    }
}
