package com.example.deligov2.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.PedidoPorRestaurante;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminHistorialVentasDetalles;
import com.example.deligov2.SuperAdmin.SuperAdminRegistroAdministrador1;

import java.util.List;

public class SuperAdminRestauranteVentaListAdapter extends RecyclerView.Adapter<SuperAdminRestauranteVentaListAdapter.ViewHolder> {

    private List<PedidoPorRestaurante> mPedido;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminRestauranteVentaListAdapter(List<PedidoPorRestaurante> pedidoList, Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mPedido = pedidoList;
    }

        @Override
        public int getItemCount () {
        return mPedido.size();
        }

        @Override
        public SuperAdminRestauranteVentaListAdapter.ViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
            View view = mInflater.inflate(R.layout.irv_sup_admin_restaurant_list_ventas, null);
            return new SuperAdminRestauranteVentaListAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder ( final SuperAdminRestauranteVentaListAdapter.ViewHolder holder, final int position){
            holder.bindData(mPedido.get(position));
        }

        public void setPedidos (List<PedidoPorRestaurante> pedidos) {mPedido = pedidos;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvDireccion, tvPrecio,tvId,tvLinkDetalle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgPedido);
            tvDireccion = itemView.findViewById(R.id.tv_direccionPedido);
            tvPrecio = itemView.findViewById(R.id.tv_monto);
            tvId= itemView.findViewById(R.id.tv_idPedido);
            tvLinkDetalle=itemView.findViewById(R.id.link_detalle);
        }

        public void bindData(final PedidoPorRestaurante pedido) {
            tvDireccion.setText("Dirección: "+ pedido.getDireccion());
            tvPrecio.setText("Monto: S/" + pedido.getPrice());
            tvId.setText("#"+pedido.getIdOrder());

            iconImage.setImageResource(R.drawable.bembos_logo);

            tvLinkDetalle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(itemView.getContext(), SuperAdminHistorialVentasDetalles.class);
                    //intent.putExtra("id_cliente", cliente.getId());
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}


