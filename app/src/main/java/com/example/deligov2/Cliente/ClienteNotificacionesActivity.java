package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.NotificacionesAdapter;
import com.example.deligov2.Adapters.RestaurantesClientesAdapter;
import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClienteNotificacionesActivity extends AppCompatActivity {


   // TextView goToDetails;
    ArrayList<Notificaciones> lista;
    String[] contenido = {
            "Se ha recibido su pedido correctamente",
            "Su pedido está en preparación",
            "Su pedido está listo",
            "Su pedido está en camino",
            "Su pedido ha llegado a su destino"
    };

    int[] idsNotificaciones = {
            1,2,3,4,5
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_notificaciones);

       // goToDetails = findViewById(R.id.goToDetails);
        //goToDetails.setOnClickListener(view -> {
            //Intent intent = new Intent(this,ClienteDetalleCompra.class);
            //startActivity(intent);
        //});

        lista = new ArrayList<>();
        for(int i=0;i<5;i++){
            Notificaciones notificaciones = new Notificaciones();
            notificaciones.setId(idsNotificaciones[i]);
            notificaciones.setContenido(contenido[i]);
            notificaciones.setIdCompra(42);
            notificaciones.setFecha(LocalDateTime.now().plusMinutes(i*10));
            lista.add(notificaciones);
        }
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteNotificacionesActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteNotificacionesActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteNotificacionesActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
        NotificacionesAdapter adapter = new NotificacionesAdapter();
        adapter.setContext(ClienteNotificacionesActivity.this);
        adapter.setListaNotificaciones(lista);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewNoti);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void verPerfil(View view){
        Intent intent = new Intent(this, ClientePerfil.class);
        startActivity(intent);
    }

    public void verHistorial(View view){
        Intent intent = new Intent(this, ClienteHistorialActivity.class);
        startActivity(intent);
    }

    public void verHome(View view){
        Intent intent = new Intent(this, ClienteHomeActivity.class);
        startActivity(intent);
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