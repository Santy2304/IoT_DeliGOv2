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
import com.example.deligov2.Adapters.SuperAdminRestauranteListAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.RestauranteSA;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminRestaurante extends AppCompatActivity {
    List<RestauranteSA> restaurantes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante);


        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRestaurante.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminRestaurante.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRestaurante.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRestaurante.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        FloatingActionButton btAgregar = findViewById(R.id.bt_agregar);

        btAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRegistroRestaurante1();
            }
        });


        mostrarListaRestaurante();


    }

    public void mostrarListaRestaurante(){
        restaurantes = new ArrayList<>();
        String[] categ = {"ola", "ola"};
        restaurantes.add(new RestauranteSA("Bembos","9:00 - 21:00",categ,1,1450.20f,1,true));
        restaurantes.add(new RestauranteSA("Bembos2","9:00 - 21:00",categ,1,1450.20f,1,true));
        restaurantes.add(new RestauranteSA("Bembos3","9:00 - 21:00",categ,0,0.0f,0,true));
        restaurantes.add(new RestauranteSA("Bembos4","9:00 - 21:00",categ,0,0.0f,0,true));
        restaurantes.add(new RestauranteSA("Bembos5","9:00 - 21:00",categ,0,0.0f,0,true));


        SuperAdminRestauranteListAdapter listAdapter = new SuperAdminRestauranteListAdapter(restaurantes,this);
        RecyclerView recyclerView = findViewById(R.id.listRestaurantesPanel);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    //Cambiar vista

    public void vistaRegistroRestaurante1(){
        Intent intent = new Intent(this, SuperAdminRegistroRestaurante1.class);
        startActivity(intent);
    }
}