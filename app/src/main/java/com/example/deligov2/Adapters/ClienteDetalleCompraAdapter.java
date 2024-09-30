package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClienteDetalleCompraAdapter extends RecyclerView.Adapter<ClienteDetalleCompraAdapter.ClienteDetalleViewHolder>{
    private List<VentaPlatilloSA> listafood;
    private Context context;

    @NonNull
    @Override
    public ClienteDetalleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_boughtfood, parent, false);
        return new ClienteDetalleCompraAdapter.ClienteDetalleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteDetalleViewHolder holder, int position) {
        VentaPlatilloSA v = listafood.get(position);
        holder.plato = v;

        TextView textViewCant = holder.itemView.findViewById(R.id.cantFood);
        textViewCant.setText("Cantidad "+v.getCantidad());

        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(v.getNombre());

        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrice);
        textViewPrice.setText(String.format("S/ %.2f", v.getPrice()));
    }

    @Override
    public int getItemCount() {
        return listafood.size();
    }


    public class ClienteDetalleViewHolder extends RecyclerView.ViewHolder{
        VentaPlatilloSA plato;
        public ClienteDetalleViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<VentaPlatilloSA> getListafood() {
        return listafood;
    }

    public void setListafood(List<VentaPlatilloSA> listafood) {
        this.listafood = listafood;
    }
}
