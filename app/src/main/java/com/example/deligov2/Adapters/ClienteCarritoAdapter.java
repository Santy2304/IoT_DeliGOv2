package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.Cliente.ClientePlatoActivity;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ClienteCarritoAdapter extends RecyclerView.Adapter<ClienteCarritoAdapter.CarritoViewHolder>{
    private List<Platillo> listaPlatosCarrito;
    private Context context;
    private List<Integer> cantidades;         // Lista de cantidades asociadas

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_cliente_carrito, parent, false);
        return new ClienteCarritoAdapter.CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        Platillo p = listaPlatosCarrito.get(position);
        holder.platillo = p;
        TextView textViewName = holder.itemView.findViewById(R.id.foodName);
        textViewName.setText(p.getNombre());

        TextView textViewPrice = holder.itemView.findViewById(R.id.foodPrice);
        textViewPrice.setText(String.format("S/ %.2f", p.getPrecio()));

        holder.textCantidad.setText(String.valueOf(cantidades.get(position)));

        holder.addButton.setOnClickListener(v -> {
            int currentQuantity = cantidades.get(position);
            cantidades.set(position, currentQuantity + 1);
            holder.textCantidad.setText(String.valueOf(cantidades.get(position)));
            if (onDataChangeListener != null) {
                onDataChangeListener.onDataChanged();
            }
        });

        holder.minusButton.setOnClickListener(v -> {
            int currentQuantity = cantidades.get(position);
            if (currentQuantity > 1) { // No permitir valores menores a 1
                cantidades.set(position, currentQuantity - 1);
                holder.textCantidad.setText(String.valueOf(cantidades.get(position)));
            }
            if (onDataChangeListener != null) {
                onDataChangeListener.onDataChanged();
            }
        });

        holder.textCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    int nuevaCantidad = Integer.parseInt(s.toString());
                    cantidades.set(position, nuevaCantidad);
                    if (onDataChangeListener != null) {
                        onDataChangeListener.onDataChanged();
                    }
                } catch (NumberFormatException e) {
                    cantidades.set(position, 0);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });



        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference().child("restaurantes/"+p.getIdRestaurante()+"/"+p.getId()+"/plato.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(holder.itemView.getContext())
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(holder.ImageView);
        }).addOnFailureListener(e -> {
            holder.ImageView.setImageResource(R.drawable.camara_icon);
        });


        holder.deleteButton.setOnClickListener(v -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                listaPlatosCarrito.remove(adapterPosition);
                cantidades.remove(adapterPosition);
                notifyItemRemoved(adapterPosition);
                if (onDataChangeListener != null) {
                    onDataChangeListener.onDataChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaPlatosCarrito.size();
    }


    public class CarritoViewHolder extends RecyclerView.ViewHolder{
        Platillo platillo;
        android.widget.ImageView ImageView,deleteButton;
        TextView textCantidad, button;
        Button minusButton, addButton;
        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            ImageView = itemView.findViewById(R.id.ImageFood);
            button = itemView.findViewById(R.id.foodDetails);
            minusButton = itemView.findViewById(R.id.minusButton);
            addButton = itemView.findViewById(R.id.addButton);
            textCantidad = itemView.findViewById(R.id.cantidadId);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            button.setOnClickListener(view -> {
                Intent intent = new Intent(itemView.getContext(), ClientePlatoActivity.class);
                intent.putExtra("idPlato",platillo.getId());
                itemView.getContext().startActivity(intent);
            });
        }
    }

    public List<Platillo> getListaPlatosCarrito() {
        return listaPlatosCarrito;
    }

    public void setListaPlatosCarrito(List<Platillo> listaPlatosCarrito) {
        this.listaPlatosCarrito = listaPlatosCarrito;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Integer> getCantidades() {
        return cantidades;
    }

    public void setCantidades(List<Integer> cantidades) {
        this.cantidades = cantidades;
    }
    private OnDataChangeListener onDataChangeListener;

    public interface OnDataChangeListener {
        void onDataChanged();
    }

    public void setOnDataChangeListener(OnDataChangeListener listener) {
        this.onDataChangeListener = listener;
    }

}
