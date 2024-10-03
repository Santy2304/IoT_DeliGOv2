package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.Adapters.SuperAdminRestauranteVentaAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminHistorialVentasDetalles extends AppCompatActivity {

    List<VentaPlatilloSA> ventas;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_historial_ventas_detalles);

        mostrarListaVentas();

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto de aquí te manda a la vista anterior
                onBackPressed();
            }
        });

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminHistorialVentasDetalles.this, SuperAdminVistaLogEvent.class);
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
                    Intent intentRestaurant = new Intent(SuperAdminHistorialVentasDetalles.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminHistorialVentasDetalles.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminHistorialVentasDetalles.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });


        //Manejo de los botones
        Button btResumen = findViewById(R.id.bt_ganancias);

        btResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteResumen(v);
            }
        });

        Button btCarta = findViewById(R.id.bt_carta);

        btCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestaurantePlatillos(v);
            }
        });

        Button btUbicacion = findViewById(R.id.bt_ubicacion);

        btUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteUbicacion(v);
            }
        });
    }

    //Manejo de datos
    public void mostrarListaVentas(){
        ventas = new ArrayList<>();
        ventas.add(new VentaPlatilloSA(1,"Combo Platano",14.20f,4,1));
        ventas.add(new VentaPlatilloSA(1,"Combo Ola",14.50f,2,1));
        ventas.add(new VentaPlatilloSA(1,"Combo Supernatural",10.30f,3,1));

        SuperAdminRestauranteVentaAdapter listAdapter = new SuperAdminRestauranteVentaAdapter(ventas,this);
        RecyclerView recyclerView = findViewById(R.id.listVentas);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    //Cambio vista
    public void vistaRestauranteResumen(View view) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        startActivity(intent);
    }

    public void vistaRestaurantePlatillos(View view) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        startActivity(intent);
    }
    public void vistaRestauranteUbicacion(View view){
        Intent intent = new Intent(this, SuperAdminRestauranteUbicacion.class);
        startActivity(intent);
    }
}