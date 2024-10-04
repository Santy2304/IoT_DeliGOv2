package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorRestauranteAdapter;
import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.Cliente.ClienteHistorialActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.Cliente.ClientePerfil;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class AdministradorRestauranteActivity extends AppCompatActivity {

    ArrayList<Plato> lista;
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
        setContentView(R.layout.activity_administrador_vista_inicial);

        lista = new ArrayList<>();
        for (int i=0;i<6;i++){
            Plato plato = new Plato();
            plato.setNombre(nombresPlatos[i]);
            plato.setPrecio(Precios[i]);
            lista.add(plato);
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorRestauranteActivity.this, AdministradorReporteClientesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorRestauranteActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentProfile = new Intent(AdministradorRestauranteActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        AdministradorRestauranteAdapter adapter = new AdministradorRestauranteAdapter();
        adapter.setContext(this);
        adapter.setListaPlatos(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerRestauranteAdmin);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);

        //Capturar el elemento de botón
        FloatingActionButton historyButton = findViewById(R.id.buttonHistorial);

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdministradorHistorialActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.reports){
            startActivity(new Intent(this, AdministradorReporteClientesActivity.class));
            return true;
        } else if (item.getItemId()==R.id.information) {
            startActivity(new Intent(this, AdministradorInfoRestauranteActivity.class));
            return true;
        } else if (item.getItemId()==R.id.principal) {
            startActivity(new Intent(this, AdministradorRestauranteActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            //startActivity(new Intent(this, AdministradorRestauranteActivity.class));
            return true;
        } else{
            return super.onOptionsItemSelected(item);

        }

    }

}
