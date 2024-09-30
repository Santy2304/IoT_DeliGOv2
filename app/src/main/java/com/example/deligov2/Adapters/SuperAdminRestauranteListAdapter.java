package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Beans.RestauranteSA;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SuperAdminRestauranteListAdapter extends RecyclerView.Adapter<SuperAdminRestauranteListAdapter.ViewHolder>{
    private List<RestauranteSA> mRestaurante;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRestauranteListAdapter(List<RestauranteSA> restauranteList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mRestaurante = restauranteList;
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

    public void setRestaurante(List<RestauranteSA> restaurantes){mRestaurante = restaurantes;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvGanancia, tvAdmin;
        FloatingActionButton btVer,btHabilitar,btDeshabilitar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgRestaurante);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvGanancia = itemView.findViewById(R.id.tv_ganancia);
            tvAdmin = itemView.findViewById(R.id.tv_admin);
            btVer = itemView.findViewById(R.id.bt_info);
            btHabilitar=itemView.findViewById(R.id.bt_activar);
            btDeshabilitar=itemView.findViewById(R.id.bt_desactivar);
        }

        public void bindData(final RestauranteSA restaurante) {
            int idAdmin = restaurante.getIdAdmin();

            if(idAdmin==0){
                iconImage.setImageResource(R.drawable.bembos_logo);
                tvGanancia.setText("S/"+ restaurante.getMonto());
                tvNombre.setText(restaurante.getNombre());
                btHabilitar.setVisibility(View.INVISIBLE);
                btDeshabilitar.setVisibility(View.INVISIBLE);
                btVer.setImageResource(R.drawable.baseline_person_add_24);

            }else{
                iconImage.setImageResource(R.drawable.bembos_logo);
                tvGanancia.setText("S/"+ restaurante.getMonto());
                tvNombre.setText(restaurante.getNombre());
                tvAdmin.setText("Admin ola");
            }

        }
    }
}
