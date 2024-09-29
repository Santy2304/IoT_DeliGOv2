package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Repartidor;
import com.example.deligov2.R;

import java.util.List;

public class SuperAdminRepartidorListAdapter extends RecyclerView.Adapter<SuperAdminRepartidorListAdapter.ViewHolder> {
    private List<Repartidor> mRepartidor;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRepartidorListAdapter(List<Repartidor> repartidorList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mRepartidor = repartidorList;
    }

    @Override
    public int getItemCount(){return mRepartidor.size();}

    @Override
    public SuperAdminRepartidorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.sup_admin_cliente_list, null);
        return new SuperAdminRepartidorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminRepartidorListAdapter.ViewHolder holder, final int position){
        holder.bindData(mRepartidor.get(position));
    }

    public void setRepartidor(List<Repartidor> repartidores){mRepartidor = repartidores;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvDni, tvCorreo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
        }

        public void bindData(final Repartidor repartidor) {
            tvNombre.setText("Nombre: " + repartidor.getNombre() + " " + repartidor.getApellido());
            tvDni.setText("DNI: " + repartidor.getNumDocument());
            tvCorreo.setText("Correo: " + repartidor.getCorreo());

            iconImage.setImageResource(R.drawable.costumer_green);
        }
    }
}
