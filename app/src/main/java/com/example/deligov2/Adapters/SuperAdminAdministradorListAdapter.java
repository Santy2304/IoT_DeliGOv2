package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilAdministrador;
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilCliente;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminAdministradorListAdapter extends RecyclerView.Adapter<SuperAdminAdministradorListAdapter.ViewHolder> {

    private List<Administrador> mAdmin;
    private List<Administrador> mAdminS; //Esta es la listado con el filtro
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminAdministradorListAdapter(List<Administrador> adminList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mAdmin = adminList;
        this.mAdminS = new ArrayList<>(adminList);
    }

    @Override
    public int getItemCount(){return mAdmin.size();}

    @Override
    public SuperAdminAdministradorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.sup_admin_cliente_list, parent,false);
        return new SuperAdminAdministradorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminAdministradorListAdapter.ViewHolder holder, final int position){
        holder.bindData(mAdmin.get(position));
    }

    public void setAdmin(List<Administrador> admins){mAdmin = admins;}

    // Método para filtrar la lista
    public void filter(String text) {
        mAdmin.clear();
        if (text.isEmpty()) {
            mAdmin.addAll(mAdminS);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Administrador admin : mAdminS) {
                if (admin.getNombre().toLowerCase().contains(filterPattern)) {
                    mAdmin.add(admin);
                }
            }
        }
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvDni, tvCorreo;
        FloatingActionButton btInfo,btHabilitar;
        private boolean isAdminHabilitado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
            btInfo = itemView.findViewById(R.id.bt_info);
            btHabilitar=itemView.findViewById(R.id.bt_activar);
            isAdminHabilitado = true;
        }

        public void bindData(final Administrador admin) {
            tvNombre.setText(admin.getNombre() + " " + admin.getApellido());
            tvDni.setText("DNI: " + admin.getNumDocumento());
            tvCorreo.setText(admin.getCorreo());
            btHabilitar.setVisibility(View.VISIBLE);
            iconImage.setImageResource(R.drawable.costumer_green);

            btInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(itemView.getContext(), SuperAdminVistaPerfilAdministrador.class);
                    //intent.putExtra("id_cliente", cliente.getId());
                    itemView.getContext().startActivity(intent);
                }
            });

            //Manejo de habilitar o deshabilitar admin
            if (!isAdminHabilitado) {
                btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
            } else {
                btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
            }

            btHabilitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isAdminHabilitado) {
                        // Mostrar el diálogo para deshabilitar
                        new MaterialAlertDialogBuilder(itemView.getContext())
                                .setTitle("Confirmación")
                                .setMessage("¿Estás seguro de deshabilitar?")
                                .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        isAdminHabilitado = false;

                                        btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                        Toast.makeText(itemView.getContext(), "Administrador deshabilitado", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    } else {
                        // Mostrar el diálogo para habilitar
                        new MaterialAlertDialogBuilder(itemView.getContext())
                                .setTitle("Confirmación")
                                .setMessage("¿Estás seguro de habilitar?")
                                .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        isAdminHabilitado = true; //restaurante.setHabilitado(true)

                                        btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                        Toast.makeText(itemView.getContext(), "Administrador habilitado", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                }
            });
        }
    }
}
