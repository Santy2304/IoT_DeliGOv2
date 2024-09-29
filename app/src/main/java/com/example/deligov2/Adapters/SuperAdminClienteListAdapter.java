package com.example.deligov2.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.R;

import java.util.List;

public class SuperAdminClienteListAdapter extends RecyclerView.Adapter<SuperAdminClienteListAdapter.ViewHolder>{
    private List<Cliente> mCliente;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminClienteListAdapter(List<Cliente> clienteList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mCliente = clienteList;
    }

    @Override
    public int getItemCount(){return mCliente.size();}

    @Override
    public SuperAdminClienteListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.sup_admin_cliente_list, null);
        return new SuperAdminClienteListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminClienteListAdapter.ViewHolder holder, final int position){
        holder.bindData(mCliente.get(position));
    }

    public void setClientes(List<Cliente> clientes){mCliente = clientes;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvNombre, tvDni, tvCorreo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgCliente);
            tvNombre = itemView.findViewById(R.id.tv_nombre);
            tvDni = itemView.findViewById(R.id.tv_dni);
            tvCorreo = itemView.findViewById(R.id.tv_correo);
        }

        public void bindData(final Cliente cliente) {
            tvNombre.setText("Nombre: " + cliente.getNombre() + " " + cliente.getApellido());
            tvDni.setText("DNI: " + cliente.getNumDocument());
            tvCorreo.setText("Correo: " + cliente.getCorreo());

            iconImage.setImageResource(R.drawable.elizabeth);

            //Posteriormente se podra hacer lo mismo con botones -- Añadir código para esa lógica
        }
    }

}
