package com.example.deligov2.Cliente;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.RestaurantesClientesAdapter;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.PerfilRepartidor;
import com.example.deligov2.SuperAdmin.SuperAdminAdministrador;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminRestaurante;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ClienteHomeActivity extends AppCompatActivity {

    ArrayList<Restaurante> lista=new ArrayList<>();

    FirebaseFirestore db;
    FloatingActionButton notiButton;
    FloatingActionButton carritoButton;
    MaterialButton restaurantButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_home);

        db = FirebaseFirestore.getInstance();



        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteHomeActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteHomeActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteHomeActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });



        RecyclerView carouselRecyclerView;
        CarouselAdapter adapter2;
//        carouselRecyclerView = findViewById(R.id.carousel_recycler_view);

//        List<Integer> imageList = Arrays.asList(
//                R.drawable.carrusel_1,
//                R.drawable.carrusel_2,
//                R.drawable.carrusel_3,
//                R.drawable.carrusel_4,
//                R.drawable.carrusel_5
//        );
//
//
//        adapter2 = new CarouselAdapter(this, imageList);
//        carouselRecyclerView.setAdapter(adapter2);
//
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        carouselRecyclerView.setLayoutManager(layoutManager);
//

        notiButton = findViewById(R.id.noti_button);
        carritoButton = findViewById(R.id.cart_button);
        //restaurantButton = findViewById(R.id.go_button);

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteNotificacionesActivity.class);
            startActivity(intent);
        });

        carritoButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteCarrito.class);
            startActivity(intent);
        });



        RestaurantesClientesAdapter adapter = new RestaurantesClientesAdapter();
        adapter.setContext(this);
        adapter.setListaRestaurantes(lista);

        RecyclerView recyclerView = findViewById(R.id.reciclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClienteHomeActivity.this));

        db.collection("restaurantes").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                lista.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Restaurante restaurante = document.toObject(Restaurante.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                    lista.add(restaurante);
                }
                adapter.notifyDataSetChanged();
            }
        });

        //restaurantButton.setOnClickListener(view -> {
          //  Intent intent = new Intent(this, ClienteRestaurantActivity.class);
            //startActivity(intent);
        //});

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