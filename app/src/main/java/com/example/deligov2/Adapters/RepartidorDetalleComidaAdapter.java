package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.R;

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
    }
    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class RepartidorDetalleComidaViewHolder extends RecyclerView.ViewHolder {
        Comida elemento;
        public RepartidorDetalleComidaViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

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
}
