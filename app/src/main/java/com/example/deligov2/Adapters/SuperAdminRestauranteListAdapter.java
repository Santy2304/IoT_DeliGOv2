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

import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Beans.RestauranteSA;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminRegistroAdministrador1;
import com.example.deligov2.SuperAdmin.SuperAdminRestauranteResumen;
import com.example.deligov2.SuperAdmin.SuperAdminVistaPerfilAdministrador;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRestauranteListAdapter extends RecyclerView.Adapter<SuperAdminRestauranteListAdapter.ViewHolder>{
    private List<Restaurante> mRestaurante;
    private List<Restaurante> mRestauranteS; //Esta es la listado con el filtro
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRestauranteListAdapter(List<Restaurante> restauranteList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mRestaurante = restauranteList;
        this.mRestauranteS = new ArrayList<>(restauranteList);
    }

    @Override
    public int getItemCount(){return mRestaurante.size();}

    @Override
    public SuperAdminRestauranteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.irv_sup_admin_restaurant_list, null);
        return new SuperAdminRestauranteListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminRestauranteListAdapter.ViewHolder holder, final int position){
        holder.bindData(mRestaurante.get(position));
    }

    public void setRestaurante(List<Restaurante> restaurantes){mRestaurante = restaurantes;}

    // Método para filtrar la lista
    public void filter(String text) {
        mRestaurante.clear();
        if (text.isEmpty()) {
            mRestaurante.addAll(mRestauranteS);
        } else {
            String filterPattern = text.toLowerCase().trim();
            for (Restaurante restaurante : mRestauranteS) {
                if (restaurante.getNombre().toLowerCase().contains(filterPattern)) {
                    mRestaurante.add(restaurante);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvGanancia, tvAdmin;
        FloatingActionButton btVer,btHabilitar,btDeshabilitar;

        //private boolean isRestauranteHabilitado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgRestaurante);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvGanancia = itemView.findViewById(R.id.tv_ganancia);
            tvAdmin = itemView.findViewById(R.id.tv_admin);
            btVer = itemView.findViewById(R.id.bt_info);
            btHabilitar=itemView.findViewById(R.id.bt_activar);
            btDeshabilitar=itemView.findViewById(R.id.bt_desactivar);

            //isRestauranteHabilitado = true; //restaurante.getHabilitado
        }

        public void bindData(final Restaurante restaurante) {
            String strAdmin = restaurante.getAdmin();

            if(strAdmin==null){
                iconImage.setImageResource(R.drawable.bembos_logo);
                tvGanancia.setText("S/"+ restaurante.getMonto());
                tvNombre.setText(restaurante.getNombre());
                btHabilitar.setVisibility(View.INVISIBLE);
                btDeshabilitar.setVisibility(View.INVISIBLE);
                btVer.setImageResource(R.drawable.baseline_person_add_24);

                btVer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(itemView.getContext(), SuperAdminRegistroAdministrador1.class);
                        //intent.putExtra("id_cliente", cliente.getId());
                        itemView.getContext().startActivity(intent);
                    }
                });

            }else{

                iconImage.setImageResource(R.drawable.bembos_logo);
                tvGanancia.setText("S/"+ restaurante.getMonto());
                tvNombre.setText(restaurante.getNombre());
                tvAdmin.setText("Admin: "+restaurante.getAdmin());
                btHabilitar.setVisibility(View.VISIBLE);
                btDeshabilitar.setVisibility(View.INVISIBLE);
                btVer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(itemView.getContext(), SuperAdminRestauranteResumen.class);
                        itemView.getContext().startActivity(intent);
                    }
                });
                if (!restaurante.isEstado()) {
                    btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                } else {
                    btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                    btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));

                }
                btHabilitar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (restaurante.isEstado()) {
                            // Mostrar el diálogo para deshabilitar
                            new MaterialAlertDialogBuilder(itemView.getContext())
                                    .setTitle("Confirmación")
                                    .setMessage("¿Estás seguro de deshabilitar?")
                                    .setPositiveButton("Estoy seguro", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            //isRestauranteHabilitado = false;
                                            restaurante.setEstado(false);

                                            // Actualizar el estado en Firestore
                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("restaurantes")
                                                    .document(restaurante.getId())
                                                    .update("estado", false)
                                                    .addOnSuccessListener(aVoid -> {
                                                        btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                        Toast.makeText(itemView.getContext(), "Restaurante deshabilitado", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(itemView.getContext(), "Error al actualizar el estado en Firestore", Toast.LENGTH_SHORT).show();
                                                    });

                                            btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                            btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                            Toast.makeText(itemView.getContext(), "Restaurante deshabilitado", Toast.LENGTH_SHORT).show();
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
                                            //isRestauranteHabilitado = true; //restaurante.setHabilitado(true)
                                            restaurante.setEstado(true);

                                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                                            db.collection("restaurantes")
                                                    .document(restaurante.getId())
                                                    .update("estado", true)
                                                    .addOnSuccessListener(aVoid -> {
                                                        btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                                        btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                                        Toast.makeText(itemView.getContext(), "Restaurante habilitado", Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(itemView.getContext(), "Error al actualizar el estado en Firestore", Toast.LENGTH_SHORT).show();
                                                    });

                                            btHabilitar.setImageResource(R.drawable.baseline_deactive_24);
                                            btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.md_theme_error_mediumContrast));
                                            Toast.makeText(itemView.getContext(), "Restaurante deshabilitado", Toast.LENGTH_SHORT).show();

                                            btHabilitar.setImageResource(R.drawable.baseline_check_circle_24);
                                            btHabilitar.setBackgroundTintList(ContextCompat.getColorStateList(itemView.getContext(), R.color.light_green));
                                            Toast.makeText(itemView.getContext(), "Restaurante habilitado", Toast.LENGTH_SHORT).show();
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
}
