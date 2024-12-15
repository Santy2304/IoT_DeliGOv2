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
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.DTO.LogSuper;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuperAdminLogAdapter extends RecyclerView.Adapter<SuperAdminLogAdapter.SuperAdminLogViewHolder>{
    private List<LogSuper> mLog;
    private Context context;

    @NonNull
    @Override
    public SuperAdminLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_notificaciones, parent, false);
        return new SuperAdminLogAdapter.SuperAdminLogViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull SuperAdminLogViewHolder holder, int position) {
        LogSuper l = mLog.get(position);
        holder.logSuper = l;


        Timestamp timestamp = l.getFecha();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormateada = dateFormat.format(date);

        TextView textViewId = holder.itemView.findViewById(R.id.orderId);
        textViewId.setText("");

        TextView textViewD = holder.itemView.findViewById(R.id.goToDetails);
        textViewD.setText("");

        TextView textViewHorario = holder.itemView.findViewById(R.id.notiDate);
        textViewHorario.setText(fechaFormateada);

        TextView textViewInfo = holder.itemView.findViewById(R.id.contentId);
        textViewInfo.setText(l.getInfo());

        FirebaseStorage storage = FirebaseStorage.getInstance();
        if(l.getTipo().equals("Pedido")){
            StorageReference storageReference = storage.getReference().child("users/"+l.getIdImage()+"/profile.jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .placeholder(R.drawable.camara_icon)
                        .error(R.drawable.camara_icon)
                        .into(holder.imagen);
            }).addOnFailureListener(e -> {
                holder.imagen.setImageResource(R.drawable.camara_icon);
            });
        }else if (l.getTipo().equals("Restaurante")){
            StorageReference storageReference = storage.getReference().child("restaurantes/"+l.getIdImage()+"/logo.jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .placeholder(R.drawable.camara_icon)
                        .error(R.drawable.camara_icon)
                        .into(holder.imagen);
            }).addOnFailureListener(e -> {
                holder.imagen.setImageResource(R.drawable.camara_icon);
            });
        }else {
            StorageReference storageReference = storage.getReference().child("restaurantes/"+l.getIdImage()+"/plato.jpg");
            storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                Glide.with(holder.itemView.getContext())
                        .load(uri)
                        .placeholder(R.drawable.camara_icon)
                        .error(R.drawable.camara_icon)
                        .into(holder.imagen);
            }).addOnFailureListener(e -> {
                holder.imagen.setImageResource(R.drawable.camara_icon);
            });
        }


    }

    @Override
    public int getItemCount() {
        return mLog.size();
    }


    public class SuperAdminLogViewHolder extends RecyclerView.ViewHolder{
        LogSuper logSuper;
        ImageView imagen;
        public SuperAdminLogViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imgMsg);
        }
    }

    public List<LogSuper> getmLog() {
        return mLog;
    }

    public void setmLog(List<LogSuper> mLog) {
        this.mLog = mLog;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
