package com.example.deligov2.Administrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorReporteClientesAdapter;
import com.example.deligov2.Adapters.AdministradorReporteComidaAdapter;
import com.example.deligov2.Beans.ReporteCliente;
import com.example.deligov2.Beans.ReporteComida;
import com.example.deligov2.R;

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

    }
}
