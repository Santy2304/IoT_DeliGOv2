package com.example.deligov2.Cliente;

import android.content.Intent;
import android.hardware.camera2.CameraExtensionSession;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClienteRestaurantActivity extends AppCompatActivity {
    ArrayList<Platillo> lista = new ArrayList<>();
    List<Platillo> listaPlatosSeleccionados = new ArrayList<>();
    ClientePlatosAdapter adapter;
    String idRestaurante;
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FloatingActionButton cartButton;
    FloatingActionButton backButton;
    Carrito carrito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_restaurant);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        idRestaurante = getIntent().getStringExtra("idRestaurante");


        db.collection("Carrito").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                       carrito  = documentSnapshot.toObject(Carrito.class);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

        db.collection("Platos").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                lista.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Platillo platillo = document.toObject(Platillo.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                    if (platillo.getIdRestaurante().equals(idRestaurante)){
                        lista.add(platillo);
                    }

                }
                adapter.notifyDataSetChanged();
            }
        });


        adapter = new ClientePlatosAdapter();
        adapter.setContext(this);
        adapter.setListaPlatos(lista);
        adapter.setOnPlatoClickListener(plato -> {
            // Agregar plato al arreglo
            listaPlatosSeleccionados.add(plato);


            Toast.makeText(this, "Plato agregado: " + plato.getNombre(), Toast.LENGTH_SHORT).show();
        });

        RecyclerView recyclerView = findViewById(R.id.recycler2columnas);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);

        backButton = findViewById(R.id.atras);
        cartButton = findViewById(R.id.cart_button);

        backButton.setOnClickListener(view -> {



            Intent intent = new Intent(this, ClienteHomeActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(view -> {



            Intent intent = new Intent(this, ClienteCarrito.class);
            intent.putExtra("listaPlatillos",(Serializable) listaPlatosSeleccionados);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteRestaurantActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteRestaurantActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteRestaurantActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }

    public void verPerfil(View view){

        if(!listaPlatosSeleccionados.isEmpty()){

        }

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