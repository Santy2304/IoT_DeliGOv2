package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Cliente.ClienteDetalleCompra;
import com.example.deligov2.Cliente.ClienteHistorialActivity;
import com.example.deligov2.DTO.Notificaciones;
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificacionesAdapter extends RecyclerView.Adapter<NotificacionesAdapter.NotificacionesViewHolder> {

    private List<Notificaciones> listaNotificaciones;
    private Context context;

    @NonNull
    @Override
    public NotificacionesAdapter.NotificacionesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_notificaciones, parent, false);
        return new NotificacionesAdapter.NotificacionesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificacionesAdapter.NotificacionesViewHolder holder, int position) {
        Notificaciones n = listaNotificaciones.get(position);
        holder.notificaciones = n;

        TextView textViewOrder = holder.itemView.findViewById(R.id.orderId);
        textViewOrder.setText("#"+n.getIdPedido());

        Timestamp timestamp = n.getFecha();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormateada = dateFormat.format(date);

        TextView textViewHorario = holder.itemView.findViewById(R.id.notiDate);
        textViewHorario.setText(fechaFormateada);

        TextView textViewContent = holder.itemView.findViewById(R.id.contentId);
        textViewContent.setText(n.getContenido());
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+n.getIdRestaurante()+"/logo.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.imageView);
        }).addOnFailureListener(e -> {
            holder.imageView.setImageResource(R.drawable.camara_icon);
        });
    }

    @Override
    public int getItemCount() {
        return listaNotificaciones.size();
    }

    public List<Notificaciones> getListaNotificaciones() {
        return listaNotificaciones;
    }

    public void setListaNotificaciones(List<Notificaciones> listaNotificaciones) {
        this.listaNotificaciones = listaNotificaciones;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
    public class NotificacionesViewHolder extends RecyclerView.ViewHolder{
        Notificaciones notificaciones;
        ImageView imageView;
        public NotificacionesViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView button = itemView.findViewById(R.id.goToDetails);
            imageView = itemView.findViewById(R.id.imgMsg);
            Pedido pedido = new Pedido();
            button.setOnClickListener(view -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("Pedidos")
                                .get()
                        .addOnCompleteListener(task->{
                            if(task.isSuccessful()){
                                for (DocumentSnapshot document : task.getResult()) {
                                    Pedido pedido2 = document.toObject(Pedido.class);
                                    Log.d("AYUDAAA" , "GAAAAAAAAAA");
                                    if (pedido2.getId().equals(notificaciones.getIdPedido())){
                                        if(pedido2.getEstado().equals("Entregado")){
                                            Intent intent = new Intent(itemView.getContext(), ClienteDetalleCompra.class);
                                            intent.putExtra("idOrder",notificaciones.getIdPedido());
                                            itemView.getContext().startActivity(intent);
                                        }else{
                                            Intent intent = new Intent(itemView.getContext(), ClienteTrackingActivity.class);
                                            intent.putExtra("idOrder",notificaciones.getIdPedido());
                                            itemView.getContext().startActivity(intent);
                                        }

                                    }
                                }
                            }
                        });
            });
        }
    }


}
