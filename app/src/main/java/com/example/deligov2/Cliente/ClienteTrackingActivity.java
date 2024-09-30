package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteDetalleCompraAdapter;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ClienteTrackingActivity extends AppCompatActivity {

    Button repartidorButton;
    Button qrButton;
    FloatingActionButton backButton;
    ArrayList<VentaPlatilloSA> lista;
    String[] nombresPlatos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",

    };
    float[] Precios  = {
            8,
            13,
            11,
            15,
    };
    int[] idRestaurante ={
            1,1,1,1
    };

    int[] cantidad = {
            1,5,2,3
    };
    int[] idPlato = {
            1,2,3,4
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_tracking);

        lista = new ArrayList<>();
        for(int i=0;i<4;i++){
            VentaPlatilloSA ventaPlatilloSA = new VentaPlatilloSA(idPlato[i],nombresPlatos[i],Precios[i],cantidad[i],idRestaurante[i]);
            lista.add(ventaPlatilloSA);
        }

        ClienteDetalleCompraAdapter adapter = new ClienteDetalleCompraAdapter();
        adapter.setContext(this);
        adapter.setListafood(lista);

        RecyclerView recyclerView = findViewById(R.id.recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        repartidorButton = findViewById(R.id.repartidorButton);
        qrButton = findViewById(R.id.qrButton);
        backButton = findViewById(R.id.atrasTracking);

        repartidorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteVeRepartidor.class);
            startActivity(intent);
        });

        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteQR.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHistorialActivity.class);
            startActivity(intent);
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.historial){
            startActivity(new Intent(this, ClienteHistorialActivity.class));
            return true;
        } else if (item.getItemId()==R.id.restaurant) {
            startActivity(new Intent(this, ClienteRestaurantActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            startActivity(new Intent(this, ClientePerfil.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);

        }

    }
}