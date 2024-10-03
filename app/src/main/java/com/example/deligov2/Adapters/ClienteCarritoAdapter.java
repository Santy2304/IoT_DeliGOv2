package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.Cliente.ClientePlatoActivity;
import com.example.deligov2.R;

import java.util.List;

public class ClienteCarritoAdapter extends RecyclerView.Adapter<ClienteCarritoAdapter.CarritoViewHolder>{
    private List<VentaPlatilloSA> listaPlatosVentas;
    private Context context;

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_carrito, parent, false);
        return new ClienteCarritoAdapter.CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        VentaPlatilloSA p = listaPlatosVentas.get(position);
        holder.platoCarrito = p;
        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(p.getNombre());

        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrice);
        textViewPrice.setText(String.format("S/ %.2f", p.getPrice()));
    }

    @Override
    public int getItemCount() {
        return listaPlatosVentas.size();
    }


    public class CarritoViewHolder extends RecyclerView.ViewHolder{
        VentaPlatilloSA platoCarrito;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView button = itemView.findViewById(R.id.foodDetails);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClientePlatoActivity.class);
                intent.putExtra("idFood",platoCarrito.getIdVentaPlatillo());
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public List<VentaPlatilloSA> getListaPlatosVentas() {
        return listaPlatosVentas;
    }

    public void setListaPlatosVentas(List<VentaPlatilloSA> listaPlatosVentas) {
        this.listaPlatosVentas = listaPlatosVentas;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
