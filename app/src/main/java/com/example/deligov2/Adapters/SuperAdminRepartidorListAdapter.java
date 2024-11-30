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
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilRepartidor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRepartidorListAdapter extends RecyclerView.Adapter<SuperAdminRepartidorListAdapter.ViewHolder> {
    private List<Usuario> mRepartidor;
    private List<Usuario> mRepartidorS; //Lista de filtros
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRepartidorListAdapter(List<Usuario> repartidorList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mRepartidor = repartidorList;
        this.mRepartidorS = new ArrayList<>(repartidorList);
    }

    @Override
    public int getItemCount(){return mRepartidor.size();}

    @Override
    public SuperAdminRepartidorListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.irv_sup_admin_repartidor_list, parent,false);
        return new SuperAdminRepartidorListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminRepartidorListAdapter.ViewHolder holder, final int position){
        holder.bindData(mRepartidor.get(position));
    }

    public void setRepartidor(List<Usuario> repartidores){mRepartidor = repartidores;}

    // Método para filtrar la lista
    public void filter(String text) {
        mRepartidor.clear();
        if (text.isEmpty()) {
            mRepartidor.addAll(mRepartidorS);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Usuario repartidor : mRepartidorS) {
                if (repartidor.getNombre().toLowerCase().contains(filterPattern)) {
                    mRepartidor.add(repartidor);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvDni, tvCorreo;
        FloatingActionButton btInfo,btHabilitar,btRechazar;
        private boolean isRepartidorAceptado;
        private boolean isRepartidorHabilitado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
            btInfo = itemView.findViewById(R.id.bt_info);
            btHabilitar = itemView.findViewById(R.id.bt_activar);
            btRechazar = itemView.findViewById(R.id.bt_desactivar);
        }

        public void bindData(final Usuario repartidor) {
            tvNombre.setText( repartidor.getNombre() + " " + repartidor.getApellido());
            tvDni.setText("DNI: " + repartidor.getNumDocument());
            tvCorreo.setText(repartidor.getCorreo());
            btHabilitar.setVisibility(View.VISIBLE);
            btRechazar.setVisibility(View.VISIBLE);
            // Cargar imagen desde Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference()
                    .child("users/" + repartidor.getId() + "/profile.jpg");
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
                    Intent intent = new Intent(itemView.getContext(), SuperAdminVistaPerfilRepartidor.class);
                    intent.putExtra("repatidor_detail", repartidor);
                    itemView.getContext().startActivity(intent);
                }
            });
            btHabilitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        new MaterialAlertDialogBuilder(itemView.getContext())
                                .setTitle("Confirmación")
                                .setMessage("¿Estás seguro de aceptar al repartidor?")
                                .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        isRepartidorAceptado = true;
                                        isRepartidorHabilitado = true;

                                        btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                        btRechazar.setVisibility(View.INVISIBLE);
                                        Toast.makeText(itemView.getContext(), "Repartidor Aceptado", Toast.LENGTH_SHORT).show();

                                        //Manejo de habilitar y deshabilitar
                                        if(isRepartidorAceptado){

                                            btHabilitar.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    if (isRepartidorHabilitado) {
                                                        // Mostrar el diálogo para deshabilitar
                                                        new MaterialAlertDialogBuilder(itemView.getContext())
                                                                .setTitle("Confirmación")
                                                                .setMessage("¿Estás seguro de deshabilitar?")
                                                                .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        isRepartidorHabilitado = false;
                                                                        btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                                        Toast.makeText(itemView.getContext(), "Repartidor deshabilitado", Toast.LENGTH_SHORT).show();
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
                                                                        isRepartidorHabilitado = true; //restaurante.setHabilitado(true)

                                                                        btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                                                        Toast.makeText(itemView.getContext(), "Repartidor habilitado", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .setNegativeButton("Cancelar", null)
                                                                .show();
                                                    }
                                                }
                                            });

                                            if (!isRepartidorHabilitado) {
                                                btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                            } else {
                                                btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                            }

                                        }
                                    }
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }

            });
            btRechazar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new MaterialAlertDialogBuilder(itemView.getContext())
                            .setTitle("Confirmación")
                            .setMessage("¿Estás seguro de rechazar al repartidor?")
                            .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    isRepartidorAceptado = false;
                                    isRepartidorHabilitado = false;

                                    btHabilitar.setImageResource(R.drawable.baseline_delete_24);
                                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                    btHabilitar.setClickable(false);
                                    btRechazar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(itemView.getContext(), "Repartidor fue rechazado", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("Cancelar", null)
                            .show();
                }

            });
            //Situaciones del repartidor
            // PorValidar -  Aceptado o Rechazado - baneado o desbaneado
            if(repartidor.getAprobado().equals("PorValidar")){
                //Se muestra tal cual la vista

            }else{
                if(repartidor.getAprobado().equals("Aceptado") || repartidor.getAprobado().equals("Rechazado") ){
                    btHabilitar.setVisibility(View.INVISIBLE);
                    btRechazar.setVisibility(View.INVISIBLE);
                    btHabilitar.setClickable(false);
                    btRechazar.setClickable(false);
                }
                if(repartidor.getAprobado().equals("Aceptado")){
                    btHabilitar.setVisibility(View.VISIBLE);
                    btHabilitar.setClickable(true);
                    //El boton de habilitar se vuelve boton de baneado
                    btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));

                }
            }

        }
    }
}
