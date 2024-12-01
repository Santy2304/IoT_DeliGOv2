package com.example.deligov2.Cliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.R;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.CarouselViewHolder> {
    private List<String> listaURLs;
    private Context context;

    @NonNull
    @Override
    public CarouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.irv_carousel, parent, false);
        return new CarouselAdapter.CarouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarouselViewHolder holder, int position) {
        String imageUrl = listaURLs.get(position);
        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.alert)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return listaURLs.size();
    }


    public static class CarouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public CarouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.carrusel_imageView);
        }
    }

    public List<String> getListaURLs() {
        return listaURLs;
    }

    public void setListaURLs(List<String> listaURLs) {
        this.listaURLs = listaURLs;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }
}
