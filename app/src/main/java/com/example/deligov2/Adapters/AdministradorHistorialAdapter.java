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
import com.example.deligov2.Administrador.AdministradorHistorialActivity;
import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.firebase.Timestamp;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdministradorHistorialAdapter extends RecyclerView.Adapter<AdministradorHistorialAdapter.AdministradorSolicitudViewHolder>{

    private List<Pedido> listaSolicitudes;
    private Context context;

    @NonNull
    @Override
    public AdministradorHistorialAdapter.AdministradorSolicitudViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_administrador_solicitud, parent, false);
        return new AdministradorHistorialAdapter.AdministradorSolicitudViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdministradorHistorialAdapter.AdministradorSolicitudViewHolder holder, int position) {
        Pedido s = listaSolicitudes.get(position);
        holder.solicitud = s;

        TextView textViewName = holder.itemView.findViewById(R.id.idSolicitud);
        textViewName.setText(String.format("#%s", s.getId()));
        TextView textViewEstado = holder.itemView.findViewById(R.id.estadoSolicitud);
        textViewEstado.setText(String.format("Estado: %s", s.getEstado()));
        TextView textViewFecha = holder.itemView.findViewById(R.id.fechaSolicitud);
        String fecha = convertirTimestampASoloFecha(s.getHora());
        textViewFecha.setText(String.format("Fecha: %s", fecha));
        TextView textViewCosto = holder.itemView.findViewById(R.id.costoDeliverySolicitud);
        textViewCosto.setText(String.format("Costo por productos: S/.%.2f", obtenerCostoTotal(s)));

        // Cargar imagen
        ImageView imageView = holder.itemView.findViewById(R.id.img);
        String url = "restaurantes/" + s.getIdRestaurante() + "/logo.jpg";
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(url);

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.ic_loading)
                    .error(R.drawable.ic_errorimg)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            imageView.setImageResource(R.drawable.ic_errorimg);
        });

    }

    public String convertirTimestampASoloFecha(Timestamp timestamp) {
        // Obtener el objeto Date del Timestamp
        Date fecha = timestamp.toDate();

        // Formatear solo la fecha (día/mes/año)
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return formato.format(fecha);
    }

    public float obtenerCostoTotal(Pedido pedido) {
        float costoTotal = 0;
        ArrayList<Integer> cantidades = pedido.getListaCantidades();
        ArrayList<Float> precios = pedido.getPreciosActuales();
        for (int i = 0; i < cantidades.size(); i++) {
            costoTotal += cantidades.get(i) * precios.get(i);
        }
        return costoTotal;
    }

    @Override
    public int getItemCount() {
        return listaSolicitudes.size();
    }

    public class AdministradorSolicitudViewHolder extends RecyclerView.ViewHolder{
        Pedido solicitud;
        public AdministradorSolicitudViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public List<Pedido> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Pedido> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
