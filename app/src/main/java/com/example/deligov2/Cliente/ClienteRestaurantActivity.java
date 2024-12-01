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
    private ArrayList<Platillo> lista = new ArrayList<>();
    private ArrayList<Platillo> listaPlatosSeleccionados = new ArrayList<>();
    private ArrayList<String> listaIdPlatos = new ArrayList<>();
    private ClientePlatosAdapter adapter;
    private String idRestaurante;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
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

        db.collection("Carritos").document(user.getUid()).get()
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
            listaIdPlatos.add(plato.getId());
            Toast.makeText(this, "Plato agregado: " + plato.getNombre(), Toast.LENGTH_SHORT).show();
        });

        RecyclerView recyclerView = findViewById(R.id.recycler2columnas);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);

        backButton = findViewById(R.id.atras);
        cartButton = findViewById(R.id.cart_button);

        backButton.setOnClickListener(view -> {
            if(!listaPlatosSeleccionados.isEmpty()){
                actualizarCarrito();
            }
            Intent intent = new Intent(this, ClienteHomeActivity.class);
            startActivity(intent);
        });

        cartButton.setOnClickListener(view -> {
            if(!listaPlatosSeleccionados.isEmpty()){
                actualizarCarrito();
            }
            Intent intent = new Intent(this, ClienteCarrito.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    if(!listaPlatosSeleccionados.isEmpty()){
                        actualizarCarrito();
                    }
                    Intent intentRestaurant = new Intent(ClienteRestaurantActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    if(!listaPlatosSeleccionados.isEmpty()){
                        actualizarCarrito();
                    }
                    Intent intentPrincipal = new Intent(ClienteRestaurantActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    if(!listaPlatosSeleccionados.isEmpty()){
                        actualizarCarrito();
                    }
                    Intent intentProfile = new Intent(ClienteRestaurantActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
    }

    public void actualizarCarrito( ){
        carrito.setIdRestaurante(idRestaurante);
        carrito.setIdUsuario(user.getUid());
        carrito.setIdListaPlatos(listaIdPlatos);
        ArrayList<Integer> listaActualizadaCantidades = new ArrayList<>();
        for(int i=0;i<listaIdPlatos.size();i++){
            listaActualizadaCantidades.add(1);
        }
        carrito.setListaCantidades(listaActualizadaCantidades);

        db.collection("Carritos")
                .document(user.getUid())
                .set(carrito)
                .addOnSuccessListener(unused -> {
                    Log.d("msg-test","Data guardada exitosamente");
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

}