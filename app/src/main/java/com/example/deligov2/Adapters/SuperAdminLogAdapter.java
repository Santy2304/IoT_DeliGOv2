package com.example.deligov2.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Beans.Logs;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SuperAdminLogAdapter extends RecyclerView.Adapter<SuperAdminLogAdapter.ViewHolder>{
    private List<Logs> mLog;
    private LayoutInflater mInflater;
    private Context context;

    public SuperAdminLogAdapter(List<Logs> logList, Context context){
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mLog = logList;
    }

    @Override
    public int getItemCount(){return mLog.size();}

    @Override
    public SuperAdminLogAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = mInflater.inflate(R.layout.irv_notificaciones, null);
        return new SuperAdminLogAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SuperAdminLogAdapter.ViewHolder holder, final int position){
        holder.bindData(mLog.get(position));
    }

    public void setLog(List<Logs> logs){mLog = logs;}

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImage;
        TextView tvId, tvNote, tvDate, tvLink;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.imgMsg);
            tvId = itemView.findViewById(R.id.orderId);
            tvDate = itemView.findViewById(R.id.notiDate);
            tvNote = itemView.findViewById(R.id.contentId);
            tvLink = itemView.findViewById(R.id.goToDetails);
        }

        public void bindData(final Logs log) {
            tvId.setText("#"+log.getIdLog());

            Date fecha = log.getFecha();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fechaString = sdf.format(fecha);
            tvDate.setText(fechaString);

            tvNote.setText(log.getInfo());
            tvLink.setVisibility(View.INVISIBLE);

            iconImage.setImageResource(R.drawable.deligo);

        }
    }
}
