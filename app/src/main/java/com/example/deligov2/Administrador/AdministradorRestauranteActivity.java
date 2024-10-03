package com.example.deligov2.Administrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorRestauranteAdapter;
import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.R;

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
        AdministradorRestauranteAdapter adapter = new AdministradorRestauranteAdapter();
        adapter.setContext(this);
        adapter.setListaPlatos(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerRestauranteAdmin);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);

    }
}
