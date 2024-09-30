package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteHistorialAdapter extends RecyclerView.Adapter<ClienteHistorialAdapter.HistorialViewHolder>{
    private List<Ordenes> listaOrdenes;
    private Context context;

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_historial, parent, false);
        return new ClienteHistorialAdapter.HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Ordenes o = listaOrdenes.get(position);
        holder.ordenes = o;

        TextView textViewOrder = holder.itemView.findViewById(R.id.idOrder);
        textViewOrder.setText("#"+o.getIdOrder());

        LocalDateTime fechaHora = o.getFecha();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHoraFormateada = fechaHora.format(formatter);

        TextView textViewHorario = holder.itemView.findViewById(R.id.fechaHistorial);
        textViewHorario.setText(fechaHoraFormateada);

        TextView textViewRestarutante = holder.itemView.findViewById(R.id.restName);
        textViewRestarutante.setText(o.getNombreRestaurante());

        TextView textViewPrice = holder.itemView.findViewById(R.id.priceOrder);
        textViewPrice.setText(String.format("S/ %.2f", o.getPrice()));
    }

    @Override
    public int getItemCount() {
        return listaOrdenes.size();
    }


    public class HistorialViewHolder extends RecyclerView.ViewHolder{
        Ordenes ordenes;
        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView button = itemView.findViewById(R.id.toDetails);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClienteTrackingActivity.class);
                intent.putExtra("idOrder",ordenes.getIdOrder());
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public List<Ordenes> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(List<Ordenes> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
