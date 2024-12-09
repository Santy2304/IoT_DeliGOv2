package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.Cliente.ClienteDetalleCompra;
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClienteHistorialAdapter extends RecyclerView.Adapter<ClienteHistorialAdapter.HistorialViewHolder>{
    private List<Pedido> listaOrdenes;
    private Context context;

    @NonNull
    @Override
    public HistorialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_historial, parent, false);
        return new ClienteHistorialAdapter.HistorialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistorialViewHolder holder, int position) {
        Pedido o = listaOrdenes.get(position);
        holder.ordenes = o;

        TextView textViewOrder = holder.itemView.findViewById(R.id.idOrder);
        textViewOrder.setText("#"+o.getId());

        Timestamp timestamp = o.getHora();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormateada = dateFormat.format(date);

        TextView textViewHorario = holder.itemView.findViewById(R.id.fechaHistorial);
        textViewHorario.setText(fechaFormateada);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("restaurantes").document(o.getIdRestaurante()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Restaurante restaurante = documentSnapshot.toObject(Restaurante.class);
                        TextView textViewRestarutante = holder.itemView.findViewById(R.id.restName);
                        textViewRestarutante.setText(restaurante.getNombre());

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));



        ArrayList<Float> precios = o.getPreciosActuales();
        ArrayList<Integer> cantidades = o.getListaCantidades();

        float total=0;
        for(int i=0;i<precios.size();i++){
            total = total + precios.get(i) * cantidades.get(i);
        }

        TextView textViewPrice = holder.itemView.findViewById(R.id.priceOrder);
        textViewPrice.setText(String.format("S/ %.2f", total));
    }

    @Override
    public int getItemCount() {
        return listaOrdenes.size();
    }


    public class HistorialViewHolder extends RecyclerView.ViewHolder{
        Pedido ordenes;
        public HistorialViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView button = itemView.findViewById(R.id.toDetails);
            button.setOnClickListener(view -> {

                if (ordenes.getEstado().equals("Entregado")){
                    Intent intent = new Intent(itemView.getContext(), ClienteDetalleCompra.class);
                    intent.putExtra("idOrder",ordenes.getId());
                    itemView.getContext().startActivity(intent);

                }else{
                    Intent intent = new Intent(itemView.getContext(), ClienteTrackingActivity.class);
                    intent.putExtra("idOrder",ordenes.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

    public List<Pedido> getListaOrdenes() {
        return listaOrdenes;
    }

    public void setListaOrdenes(List<Pedido> listaOrdenes) {
        this.listaOrdenes = listaOrdenes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
