package com.example.deligov2.SuperAdmin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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
    }

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
}