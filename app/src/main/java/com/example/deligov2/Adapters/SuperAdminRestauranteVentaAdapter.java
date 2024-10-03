package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;

import java.util.List;

public class SuperAdminRestauranteVentaAdapter extends RecyclerView.Adapter<SuperAdminRestauranteVentaAdapter.ViewHolder> {
    private List<VentaPlatilloSA> mVenta;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRestauranteVentaAdapter(List<VentaPlatilloSA> ventaListt, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mVenta = ventaListt;
    }

    @Override
    public int getItemCount(){return mVenta.size();}

    @Override
    public SuperAdminRestauranteVentaAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.irv_sup_admin_restaurante_detalles_ventas, null);
        return new SuperAdminRestauranteVentaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminRestauranteVentaAdapter.ViewHolder holder, final int position){
        holder.bindData(mVenta.get(position));
    }

    public void setVentas(List<VentaPlatilloSA> ventas){mVenta = ventas;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvPrecio, tvCantidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgVenta);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvPrecio = itemView.findViewById(R.id.tv_precio);
            tvCantidad = itemView.findViewById(R.id.tv_cantidad);
        }

        public void bindData(final VentaPlatilloSA venta) {
            tvNombre.setText(venta.getNombre());
            tvPrecio.setText("S/" + venta.getPrice());
            tvCantidad.setText("Cantidad:" + venta.getCantidad());

            iconImage.setImageResource(R.drawable.hamburguesa_royal);
        }
    }
}
