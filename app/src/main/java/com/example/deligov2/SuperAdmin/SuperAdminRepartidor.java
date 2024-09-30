package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminRepartidor.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRepartidor.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRepartidor.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
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

    //Cambio de vista

    public void vistaPanelCliente(View view) {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        startActivity(intent);
    }

    public void vistaPanelAdmin(View view) {
        Intent intent = new Intent(this, SuperAdminAdministrador.class);
        startActivity(intent);
    }
}