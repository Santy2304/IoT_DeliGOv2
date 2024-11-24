package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilAdministrador;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminAdministradorListAdapter extends RecyclerView.Adapter<SuperAdminAdministradorListAdapter.ViewHolder> {

    private List<Usuario> mAdmin;
    private List<Usuario> mAdminS; //Esta es la listado con el filtro
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminAdministradorListAdapter(List<Usuario> adminList, Context context){
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

    public void setAdmin(List<Usuario> admins){mAdmin = admins;}

    // Método para filtrar la lista
    public void filter(String text) {
        mAdmin.clear();
        if (text.isEmpty()) {
            mAdmin.addAll(mAdminS);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Usuario admin : mAdminS) {
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

        public void bindData(final Usuario admin) {
            tvNombre.setText(admin.getNombre() + " " + admin.getApellido());
            tvDni.setText("DNI: " + admin.getNumDocument());
            tvCorreo.setText(admin.getCorreo());
            btHabilitar.setVisibility(View.VISIBLE);

            // Cargar imagen desde Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference()
                    .child("users/" + admin.getId() + "/profile.jpg");

            storageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        Glide.with(iconImage.getContext())
                                .load(uri)
                                .placeholder(R.drawable.ic_loading)
                                .error(R.drawable.ic_errorimg)
                                .into(iconImage);
                    })
                    .addOnFailureListener(e -> {
                        Log.e("FirebaseStorage", "Error al cargar la imagen: ", e);
                        iconImage.setImageResource(R.drawable.ic_errorimg);
                    });

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
