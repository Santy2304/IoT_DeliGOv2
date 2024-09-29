package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.R;

import java.util.List;

public class SuperAdminAdministradorListAdapter extends RecyclerView.Adapter<SuperAdminAdministradorListAdapter.ViewHolder> {

    private List<Administrador> mAdmin;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminAdministradorListAdapter(List<Administrador> adminList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mAdmin = adminList;
    }

    @Override
    public int getItemCount(){return mAdmin.size();}

    @Override
    public SuperAdminAdministradorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.sup_admin_cliente_list, null);
        return new SuperAdminAdministradorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminAdministradorListAdapter.ViewHolder holder, final int position){
        holder.bindData(mAdmin.get(position));
    }

    public void setAdmin(List<Administrador> admins){mAdmin = admins;}

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

        public void bindData(final Administrador admin) {
            tvNombre.setText("Nombre: " + admin.getNombre() + " " + admin.getApellido());
            tvDni.setText("DNI: " + admin.getNumDocumento());
            tvCorreo.setText("Correo: " + admin.getCorreo());

            iconImage.setImageResource(R.drawable.costumer_green);
        }
    }
}
