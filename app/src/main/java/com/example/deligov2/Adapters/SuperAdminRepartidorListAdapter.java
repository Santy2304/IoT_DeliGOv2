package com.example.deligov2.Adapters;

import android.app.AlertDialog;
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
import com.example.deligov2.SuperAdmin.Home.Perfiles.SuperAdminVistaPerfilRepartidor;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        FloatingActionButton btInfo,btnBaneado,btnAceptar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
            btInfo = itemView.findViewById(R.id.bt_info);
            btnBaneado = itemView.findViewById(R.id.btn_baneado);
            btnAceptar = itemView.findViewById(R.id.btn_aceptar);
        }
        public void bindData(final Usuario repartidor) {
            tvNombre.setText( repartidor.getNombre() + " " + repartidor.getApellido());
            tvDni.setText("DNI: " + repartidor.getNumDocument());
            tvCorreo.setText(repartidor.getCorreo());
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
            if (!repartidor.isEstado()) {
                btnBaneado.setImageResource(R.drawable.baseline_deactive_24);
                btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
            } else {
                btnBaneado.setImageResource(R.drawable.baseline_check_circle_24);
                btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
            }
            //LOGICA DE LOS BOTONES
            if( repartidor.getAprobado()!=null && repartidor.getAprobado().equals("PorValidar")){
                btnBaneado.setVisibility(View.INVISIBLE);
                btnBaneado.setClickable(false);
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Confirmar acción");
                        builder.setMessage("¿Qué acción deseas realizar con esta solicitud?");

                        // Botón para aceptar
                        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción cuando se presiona "Aceptar"
                                repartidor.setAprobado("Aceptado");
                                actualizarEstadoEnFirestore(repartidor);
                                btnBaneado.setVisibility(View.VISIBLE);
                                btnBaneado.setClickable(true);
                                btnAceptar.setVisibility(View.INVISIBLE);
                                btnAceptar.setClickable(false);
                                btnBaneado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (repartidor.isEstado()) {
                                            // Mostrar el diálogo para deshabilitar
                                            new MaterialAlertDialogBuilder(itemView.getContext())
                                                    .setTitle("Confirmación")
                                                    .setMessage("¿Estás seguro de deshabilitar?")
                                                    .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("estado", false);
                                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                                    .update(updates)
                                                                    .addOnCompleteListener(task ->{
                                                                        repartidor.setEstado(false);
                                                                        btnBaneado.setImageResource(R.drawable.baseline_deactive_24);
                                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                                        Toast.makeText(itemView.getContext(), "Cliente deshabilitado", Toast.LENGTH_SHORT).show();
                                                                    });

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
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("estado", true);
                                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                                    .update(updates)
                                                                    .addOnCompleteListener(task -> {
                                                                        repartidor.setEstado(true); //restaurante.setHabilitado(true)
                                                                        btnBaneado.setImageResource(R.drawable.baseline_check_circle_24);
                                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                                                        Toast.makeText(itemView.getContext(), "Cliente habilitado", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", null)
                                                    .show();
                                        }
                                    }
                                });

                                Toast.makeText(context, "Solicitud aceptada", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Botón para rechazar
                        builder.setNegativeButton("Rechazar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción cuando se presiona "Rechazar"
                                repartidor.setAprobado("Aceptado");
                                actualizarEstadoEnFirestore(repartidor);
                                btnBaneado.setVisibility(View.VISIBLE);
                                btnBaneado.setClickable(true);
                                btnAceptar.setVisibility(View.INVISIBLE);
                                btnAceptar.setClickable(false);
                                btnBaneado.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        if (repartidor.isEstado()) {
                                            // Mostrar el diálogo para deshabilitar
                                            new MaterialAlertDialogBuilder(context)
                                                    .setTitle("Confirmación")
                                                    .setMessage("¿Estás seguro de deshabilitar?")
                                                    .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("estado", false);
                                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                                    .update(updates)
                                                                    .addOnCompleteListener(task ->{
                                                                        repartidor.setEstado(false);
                                                                        btnBaneado.setImageResource(R.drawable.baseline_deactive_24);
                                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                                        Toast.makeText(itemView.getContext(), "Cliente deshabilitado", Toast.LENGTH_SHORT).show();
                                                                    });

                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", null)
                                                    .show();
                                        } else {
                                            // Mostrar el diálogo para habilitar
                                            new MaterialAlertDialogBuilder(context)
                                                    .setTitle("Confirmación")
                                                    .setMessage("¿Estás seguro de habilitar?")
                                                    .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Map<String, Object> updates = new HashMap<>();
                                                            updates.put("estado", true);
                                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                                    .update(updates)
                                                                    .addOnCompleteListener(task -> {
                                                                        repartidor.setEstado(true); //restaurante.setHabilitado(true)
                                                                        btnBaneado.setImageResource(R.drawable.baseline_check_circle_24);
                                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                                                        Toast.makeText(itemView.getContext(), "Cliente habilitado", Toast.LENGTH_SHORT).show();
                                                                    });
                                                        }
                                                    })
                                                    .setNegativeButton("Cancelar", null)
                                                    .show();
                                        }
                                    }
                                });
                                Toast.makeText(context, "Solicitud rechazada", Toast.LENGTH_SHORT).show();
                            }
                        });

                        // Botón para cancelar
                        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción cuando se presiona "Cancelar" (cerrar el diálogo)
                                dialog.dismiss();
                                Toast.makeText(context, "Acción cancelada", Toast.LENGTH_SHORT).show();
                            }
                        });
                        // Mostrar el diálogo
                        builder.create().show();
                    }
                });
            }
            if(repartidor.getAprobado()!=null && repartidor.getAprobado().equals("Aceptado") || repartidor.getAprobado().equals("Rechazado")){
                btnBaneado.setVisibility(View.VISIBLE);
                btnBaneado.setClickable(true);
                btnAceptar.setVisibility(View.INVISIBLE);
                btnAceptar.setClickable(false);
                //Proceso de baneado normal
                btnBaneado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (repartidor.isEstado()) {
                            // Mostrar el diálogo para deshabilitar
                            new MaterialAlertDialogBuilder(itemView.getContext())
                                    .setTitle("Confirmación")
                                    .setMessage("¿Estás seguro de deshabilitar?")
                                    .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("estado", false);
                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                    .update(updates)
                                                    .addOnCompleteListener(task ->{
                                                        repartidor.setEstado(false);
                                                        btnBaneado.setImageResource(R.drawable.baseline_deactive_24);
                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                        Toast.makeText(itemView.getContext(), "Cliente deshabilitado", Toast.LENGTH_SHORT).show();
                                                    });

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
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("estado", true);
                                            FirebaseFirestore.getInstance().collection("Usuarios").document(repartidor.getId())
                                                    .update(updates)
                                                    .addOnCompleteListener(task -> {
                                                        repartidor.setEstado(true); //restaurante.setHabilitado(true)
                                                        btnBaneado.setImageResource(R.drawable.baseline_check_circle_24);
                                                        btnBaneado.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                                        Toast.makeText(itemView.getContext(), "Cliente habilitado", Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    })
                                    .setNegativeButton("Cancelar", null)
                                    .show();
                        }
                    }
                });
            }
            //Situaciones del repartidor
            // PorValidar -  Aceptado o Rechazado - baneado o desbaneado
        }
    }


    private void actualizarEstadoEnFirestore(Usuario repartidor) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Usuarios").document(repartidor.getId())
                .update("aprobado", repartidor.getAprobado())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Estado actualizado exitosamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Error al actualizar el estado", Toast.LENGTH_SHORT).show();
                });
    }
}
