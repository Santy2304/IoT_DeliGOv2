package com.example.deligov2.SuperAdmin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.Adapters.SuperAdminRepartidorListAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Repartidor;
import com.example.deligov2.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminRepartidor extends AppCompatActivity {

    List<Repartidor> repartidores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_repartidor);

        mostrarListaRepartidores();
    }

    public void mostrarListaRepartidores(){
        repartidores = new ArrayList<>();
        repartidores.add(new Repartidor(1,"Repartidor","No me jale",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));
        repartidores.add(new Repartidor(1,"August","Deli",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));
        repartidores.add(new Repartidor(1,"Sisifo","Star",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));


        SuperAdminRepartidorListAdapter listAdapter = new SuperAdminRepartidorListAdapter(repartidores,this);
        RecyclerView recyclerView = findViewById(R.id.listRepartidor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}