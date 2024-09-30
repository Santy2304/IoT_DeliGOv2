package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteHistorialAdapter;
import com.example.deligov2.Adapters.NotificacionesAdapter;
import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClienteHistorialActivity extends AppCompatActivity {
    FloatingActionButton notiButton;
    FloatingActionButton carritoButton;
    ArrayList<Ordenes> lista;
    String[] nombreRestaurante = {
            "Bembos",
            "KFC",
            "Pardos",
            "Comida Saludable",
            "Rincón Italiano",
            "Eco Suchi",
            "Fridays"
    };

    float[] precios = {
            50,20, 32.5F,40,60,23,19
    };
    int[] idOrdes={
            42,36,56,78,90,23,88
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_historial);


        lista = new ArrayList<>();
        for(int i=0;i<7;i++){
            Ordenes ordenes = new Ordenes();
            ordenes.setIdOrder(idOrdes[i]);
            ordenes.setNombreRestaurante(nombreRestaurante[i]);
            ordenes.setPrice(precios[i]);
            ordenes.setFecha(LocalDateTime.now().plusMinutes(i*10));
            lista.add(ordenes);
        }


        ClienteHistorialAdapter adapter = new ClienteHistorialAdapter();
        adapter.setContext(this);
        adapter.setListaOrdenes(lista);

        RecyclerView recyclerView = findViewById(R.id.recicler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        notiButton = findViewById(R.id.noti_button);
        carritoButton = findViewById(R.id.cart_button);

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteNotificacionesActivity.class);
            startActivity(intent);
        });

        carritoButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteCarrito.class);
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