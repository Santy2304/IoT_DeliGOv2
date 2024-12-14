package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.ReporteCliente;
import com.example.deligov2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdministradorReporteClientesAdapter extends RecyclerView.Adapter<AdministradorReporteClientesAdapter.AdministradorReportesViewHolder>{

    private List<ReporteCliente> listaReportes;
    private Context context;
    private ArrayList<String> listaNombres;

    @NonNull
    @Override
    public AdministradorReporteClientesAdapter.AdministradorReportesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_reporte_cliente, parent, false);
        return new AdministradorReporteClientesAdapter.AdministradorReportesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorReporteClientesAdapter.AdministradorReportesViewHolder holder, int position) {
        ReporteCliente r = listaReportes.get(position);
        holder.reporte = r;

        TextView textViewName = holder.itemView.findViewById(R.id.nombreCliente);
        textViewName.setText(listaNombres.get(position));

        Timestamp timestamp = r.getUltimoPedido();
        Date date = timestamp.toDate();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        String fechaFormateada = dateFormat.format(date);

        TextView textViewFecha = holder.itemView.findViewById(R.id.fechaUltimoPedido);
        textViewFecha.setText(fechaFormateada);
        TextView buttonCantidad = holder.itemView.findViewById(R.id.cantidadPedidos);
        buttonCantidad.setText(String.format("%d",r.getCantidadPedidos()));
        TextView buttonGasto = holder.itemView.findViewById(R.id.gastoTotal);
        buttonGasto.setText(String.format("%.2f", r.getTotalGastado()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("users/"+r.getIdCliente()+"/profile.jpg");

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

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public class AdministradorReportesViewHolder extends RecyclerView.ViewHolder{
        ReporteCliente reporte;
        ImageView imagen;
        public AdministradorReportesViewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.img);
        }
    }

    public List<ReporteCliente> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<ReporteCliente> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public ArrayList<String> getListaNombres() {
        return listaNombres;
    }

    public void setListaNombres(ArrayList<String> listaNombres) {
        this.listaNombres = listaNombres;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
