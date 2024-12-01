package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorHistorialAdapter;
import com.example.deligov2.Adapters.AdministradorReporteClientesAdapter;
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class AdministradorReporteClientesActivity extends AppCompatActivity {

    ArrayList<ReporteCliente> lista;
    int[] ids = {33, 32, 31, 30, 29, 28};
    String[] nombres = {
            "Elizabeth Swann",
            "Santiago Yong",
            "Hineill Cespedes",
            "Yosthim Enciso",
            "Jean Piere Ipurre",
            "Manuel Yarleque"
    };
    String[] fechasUltimoPedido = {
            "12/12/2024",
            "12/12/2024",
            "12/12/2024",
            "11/12/2024",
            "10/12/2024",
            "10/12/2024"
    };
    int[] cantidadPedidos = {20, 5, 12, 1, 13, 9};
    float[] gastos = {450, 100, 240, 25, 265, 200};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_reporte_clientes);

        lista = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            ReporteCliente reporte = new ReporteCliente();
            reporte.setId(ids[i]);
            reporte.setNombre(nombres[i]);
            reporte.setUltimoPedido(fechasUltimoPedido[i]);
            reporte.setCantidadPedidos(cantidadPedidos[i]);
            reporte.setGasto(gastos[i]);
            lista.add(reporte);
        }

        AdministradorReporteClientesAdapter adapter = new AdministradorReporteClientesAdapter();
        adapter.setContext(this);
        adapter.setListaReportes(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerReporteClientes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Botones para cambiar entre reportes
        findViewById(R.id.comidaButton).setOnClickListener(v -> {
            Intent intent = new Intent(this, AdministradorReporteComidaActivity.class);
            startActivity(intent);
        });

        //Navegación por el navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorReporteClientesActivity.this, AdministradorReporteClientesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorReporteClientesActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorReporteClientesActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }
}
