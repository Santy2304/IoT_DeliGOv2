package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.NotificacionesRepartidor;
import com.example.deligov2.Beans.PedidoPorSolicitar;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class RepartidorHistorialPedidosAdapter extends RecyclerView.Adapter<RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder> {

    private List<PedidoRepartidor> listaPedidos;
    private Context context;

    @NonNull
    @Override
    public RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_repartidor_historial_pedidos, parent, false);
        return new RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull RepartidorHistorialPedidosAdapter.RepartidorHistorialPedidosViewHolder holder, int position) {
        PedidoRepartidor e = listaPedidos.get(position) ;
        holder.pedidoRepartidor = e;
        //Acá seteamos los valores que iran en ls iterables de recyclerView
        holder.itemView.setId(e.getIdPedidoRepartidor());
        TextView idOrder = holder.itemView.findViewById(R.id.id_historial_pedido);
        TextView state = holder. itemView.findViewById(R.id.id_estado_pedido);
        TextView date = holder.itemView.findViewById(R.id.id_fecha_pedido);
        TextView price = holder.itemView.findViewById(R.id.id_costo);
        idOrder.setText("#" + e.getIdPedidoRepartidor());
        state.setText("Estado : " + e.getEstado());
        SimpleDateFormat sdf = new SimpleDateFormat("yy/MM/dd");
        date.setText("Fecha : " +sdf.format(e.getFecha()));
        price.setText("Costo delivery : S/."  + e.getPrecioDelivery());
        //Ocultamos un lugar

        holder.itemView.setId(e.getIdPedidoRepartidor());

        if(e.getEstado().equals("Recibido")  && e.getIdPedidoRepartidor() == holder.itemView.getId()){
            holder.itemView.findViewById(R.id.layout_mapa).findViewById(R.id.mapa).setVisibility(View.INVISIBLE);
            holder.itemView.findViewById(R.id.layout_mapa).findViewById(R.id.mapa).setClickable(false);
        }
    }

    public class RepartidorHistorialPedidosViewHolder extends RecyclerView.ViewHolder {
        PedidoRepartidor pedidoRepartidor;
        public RepartidorHistorialPedidosViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //Getter y setter lista
    public List<PedidoRepartidor> getLista () {
        return listaPedidos;
    }
    public void setLista(List<PedidoRepartidor> listaPedidos) {
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
