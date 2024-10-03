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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.Adapters.SuperAdminRestauranteCartaAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRestaurantePlatillos extends AppCompatActivity {

    List<Plato> platos;

    String[] nombresPlatos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",
            "Cheese",
            "Vegano"

    };

    float[] Precios  = {
            8,
            13,
            11,
            15,
            12,
            9
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_platillos);

        //Manejo del adapter para los datos
        platos = new ArrayList<>();

        for (int i=0;i<6;i++){
            Plato plato = new Plato();
            plato.setNombre(nombresPlatos[i]);
            plato.setPrecio(Precios[i]);
            platos.add(plato);
        }


        SuperAdminRestauranteCartaAdapter adapter = new SuperAdminRestauranteCartaAdapter();
        adapter.setContext(this);
        adapter.setListaPlato(platos);

        RecyclerView recyclerView = findViewById(R.id.recyclerCarta);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);


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
                    Intent intent = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminVistaLogEvent.class);
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
                    Intent intentRestaurant = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminPerfil.class);
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

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v);
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

    //Cambio vista
    public void vistaRestauranteResumen(View view) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
        startActivity(intent);
    }
    public void vistaRestauranteUbicacion(View view){
        Intent intent = new Intent(this, SuperAdminRestauranteUbicacion.class);
        startActivity(intent);
    }
}