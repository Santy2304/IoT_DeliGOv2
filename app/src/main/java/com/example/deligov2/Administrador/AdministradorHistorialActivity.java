package com.example.deligov2.Administrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorDetalleCompraAdapter;
import com.example.deligov2.Adapters.AdministradorHistorialAdapter;
import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.R;

import java.util.ArrayList;

public class AdministradorHistorialActivity extends AppCompatActivity {

    ArrayList<Solicitud> lista;
    int[] ids = {33, 32, 31, 30, 29, 28};
    String[] estados = {
            "En camino",
            "Entregado",
            "En camino",
            "Entregado",
            "Entregado",
            "Entregado"
    };
    String[] fechas = {
            "12/12/2024",
            "12/12/2024",
            "12/12/2024",
            "11/12/2024",
            "10/12/2024",
            "10/12/2024"
    };
    float[] preciosDelivery = {12, 9, 8, 5, 4, 7};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_historial);

        lista = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Solicitud solicitud = new Solicitud();
            solicitud.setId(ids[i]);
            solicitud.setEstado(estados[i]);
            solicitud.setFecha(fechas[i]);
            solicitud.setPrecioDelivery(preciosDelivery[i]);
            lista.add(solicitud);
        }

        AdministradorHistorialAdapter adapter = new AdministradorHistorialAdapter();
        adapter.setContext(this);
        adapter.setListaSolicitudes(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerHistorialAdmin);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
