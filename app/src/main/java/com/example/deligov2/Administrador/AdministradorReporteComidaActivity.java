package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorReporteClientesAdapter;
import com.example.deligov2.Adapters.AdministradorReporteComidaAdapter;
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.ReporteComida;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AdministradorReporteComidaActivity extends AppCompatActivity {

    ArrayList<ReporteComida> lista;
    int[] ids = {33, 32, 31, 30, 29, 28};
    String[] platos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",
            "Cheese",
            "Vegano"
    };
    int[] cantidadesVendidas = {20, 5, 12, 1, 13, 9};
    float[] ganancias = {450, 100, 240, 25, 265, 200};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_reporte_comida);

        lista = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ReporteComida reporte = new ReporteComida();
            reporte.setId(ids[i]);
            reporte.setPlato(platos[i]);
            reporte.setCantidadVendida(cantidadesVendidas[i]);
            reporte.setGanancia(ganancias[i]);
            lista.add(reporte);
        }

        AdministradorReporteComidaAdapter adapter = new AdministradorReporteComidaAdapter();
        adapter.setContext(this);
        adapter.setListaReportes(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerReporteComida);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Botones para cambiar entre reportes
        findViewById(R.id.clientesButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdministradorReporteClientesActivity.class);
            startActivity(intent);
        });

        //Navegación por el navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorReporteComidaActivity.this, AdministradorReporteClientesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorReporteComidaActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorReporteComidaActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }
}
