package com.example.deligov2.Administrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorDetalleCompraAdapter;
import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.R;

import java.util.ArrayList;

public class AdministradorDetalleCompraActivity extends AppCompatActivity {

    ArrayList<DetalleCompra> lista;
    String[] nombresPlatos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",
            "Cheese",
            "Vegano"
    };
    Integer[] cantidades = {5,2,3,6,2,4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_detalle_compra);

        lista = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            DetalleCompra detalleCompra = new DetalleCompra();
            detalleCompra.setPlato(nombresPlatos[i]);
            detalleCompra.setCantidad(cantidades[i]);
            lista.add(detalleCompra);
        }

        AdministradorDetalleCompraAdapter adapter = new AdministradorDetalleCompraAdapter();
        adapter.setContext(this);
        adapter.setListaDetalles(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerDetalleAdmin);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);

    }
}
