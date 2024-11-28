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
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilCliente;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperAdminClienteListAdapter extends RecyclerView.Adapter<SuperAdminClienteListAdapter.ViewHolder>{
    private List<Usuario> mCliente;
    private List<Usuario> mClienteS; //Esta es la listado con el filtro
    private LayoutInflater mInflater;
    private Context context;
    public SuperAdminClienteListAdapter(List<Usuario> clienteList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mCliente = clienteList;
        this.mClienteS = new ArrayList<>(clienteList);
    }

    @Override
    public int getItemCount(){return mCliente.size();}

    @Override
    public SuperAdminClienteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.sup_admin_cliente_list, parent,false);
        return new SuperAdminClienteListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminClienteListAdapter.ViewHolder holder, final int position){
        holder.bindData(mCliente.get(position));
    }

    public void setClientes(List<Usuario> clientes){mCliente = clientes;}

    // Método para filtrar la lista
    public void filter(String text) {
        mCliente.clear();
        if (text.isEmpty()) {
            mCliente.addAll(mClienteS);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Usuario cliente : mClienteS) {
                if (cliente.getNombre().toLowerCase().contains(filterPattern)) {
                    mCliente.add(cliente);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvDni, tvCorreo;
        FloatingActionButton btInfo,btHabilitar;
        private boolean isClienteHabilitado;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
            btInfo = itemView.findViewById(R.id.bt_info);
            btHabilitar=itemView.findViewById(R.id.bt_activar);
            isClienteHabilitado=true;

        }

        public void bindData(final Usuario cliente) {
            tvNombre.setText(cliente.getNombre() + " " + cliente.getApellido());
            tvDni.setText("DNI: " + cliente.getNumDocument());
            tvCorreo.setText(cliente.getCorreo());
            btHabilitar.setVisibility(View.VISIBLE);
            isClienteHabilitado = cliente.isEstado();
            // Cargar imagen desde Firebase Storage
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference()
                    .child("users/" + cliente.getId() + "/profile.jpg");

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

            //Posteriormente se podra hacer lo mismo con botones -- Añadir código para esa lógica
            btInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(itemView.getContext(), SuperAdminVistaPerfilCliente.class);
                    //intent.putExtra("id_cliente", cliente.getId());
                    itemView.getContext().startActivity(intent);
                }
            });

            //Manejo de habilitar o deshabilitar cliente
            if (!isClienteHabilitado) {
                btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
            } else {
                btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
            }
            btInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(), SuperAdminVistaPerfilCliente.class);
                    intent.putExtra("cliente_detail", cliente);
                    itemView.getContext().startActivity(intent);
                }
            });
            btInfo.setContentDescription(cliente.getId());
            btHabilitar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isClienteHabilitado) {
                        // Mostrar el diálogo para deshabilitar
                        new MaterialAlertDialogBuilder(itemView.getContext())
                                .setTitle("Confirmación")
                                .setMessage("¿Estás seguro de deshabilitar?")
                                .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("estado", false);
                                        FirebaseFirestore.getInstance().collection("Usuarios").document(cliente.getId())
                                                .update(updates)
                                                .addOnCompleteListener(task ->{
                                                    isClienteHabilitado = false;
                                                    btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
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
                                        FirebaseFirestore.getInstance().collection("Usuarios").document(cliente.getId())
                                                .update(updates)
                                                .addOnCompleteListener(task -> {
                                                    isClienteHabilitado = true; //restaurante.setHabilitado(true)
                                                    btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
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
    }

}
