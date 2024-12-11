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
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.ReporteComida;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class AdministradorReporteComidaAdapter extends RecyclerView.Adapter<AdministradorReporteComidaAdapter.AdministradorReportesViewHolder>{

    private List<Platillo> listaReportes;
    private Context context;

    @NonNull
    @Override
    public AdministradorReporteComidaAdapter.AdministradorReportesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_reporte_comida, parent, false);
        return new AdministradorReporteComidaAdapter.AdministradorReportesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorReporteComidaAdapter.AdministradorReportesViewHolder holder, int position) {
        Platillo r = listaReportes.get(position);
        holder.reporte = r;

        TextView textViewFood = holder.itemView.findViewById(R.id.nombrePlatoReporte);
        textViewFood.setText(r.getNombre());
        TextView textViewPrice = holder.itemView.findViewById(R.id.pricePedido);
        textViewPrice.setText(String.format("S/.%.2f", r.getPrecio()));
        TextView buttonCantidad = holder.itemView.findViewById(R.id.cantidadVendida);
        buttonCantidad.setText(String.format("%d",r.getCantVentaTotal()));
        TextView buttonGanancia = holder.itemView.findViewById(R.id.ganancia);
        buttonGanancia.setText(String.format("%.2f", r.getCantRecaudadoTotal()));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+r.getIdRestaurante()+"/"+r.getId()+"/plato.jpg");

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
        return listaReportes.size();
    }

    public class AdministradorReportesViewHolder extends RecyclerView.ViewHolder{
        Platillo reporte;
        ImageView imageView;
        public AdministradorReportesViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
        }
    }

    public List<Platillo> getListaReportes() {
        return listaReportes;
    }

    public void setListaReportes(List<Platillo> listaReportes) {
        this.listaReportes = listaReportes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
