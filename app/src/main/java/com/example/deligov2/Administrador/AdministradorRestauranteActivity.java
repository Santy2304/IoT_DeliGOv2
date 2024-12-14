package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.AdministradorRestauranteAdapter;
import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.Cliente.ClienteHistorialActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.Cliente.ClientePerfil;
import com.example.deligov2.Cliente.ClienteRestaurantActivity;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdministradorRestauranteActivity extends AppCompatActivity {

    /*ArrayList<Plato> lista;
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
    };*/
    FirebaseAuth auth;
    FirebaseFirestore db;
    FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_vista_inicial);

        /*lista = new ArrayList<>();
        for (int i=0;i<6;i++){
            Plato plato = new Plato();
            plato.setNombre(nombresPlatos[i]);
            plato.setPrecio(Precios[i]);
            lista.add(plato);
        }*/
        // Inicializar instancias de firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Obtener el UUID del usuario logueado
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Navegación por medio del bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);
        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorRestauranteActivity.this, AdministradorReporteClientesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorRestauranteActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorRestauranteActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });

        // Cargar el usuario desde firestore para obtener el ID del restaurante al que pertenece
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("restaurante"); // Atributo "restaurante" para obtener idRestaurante
                        cargarDatosRestaurante(idRestaurante); // Llama al método para cargar los datos de la vista (banner y listado)
                    } else {
                        Log.e("Firestore", "No se encontró el usuario con ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener usuario", e));

        //Capturar el elemento de botón
        FloatingActionButton historyButton = findViewById(R.id.buttonHistorial);

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdministradorHistorialActivity.class);
            startActivity(intent);
        });

        Button newDishButton = findViewById(R.id.newDishButton);

        newDishButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdministradorRegistroPlato1Activity.class);
            startActivity(intent);
        });

    }

    private void cargarDatosRestaurante(String idRestaurante) {
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Cargar nombre del restaurante
        db.collection("restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreRestaurante = documentSnapshot.getString("nombre");
                        TextView titleRestaurante = findViewById(R.id.titleRestaurante);
                        titleRestaurante.setText(nombreRestaurante);
                    } else {
                        Log.e("Firestore", "No se encontró el restaurante con ID: " + idRestaurante);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener restaurante", e));

        // Cargar el logo del restaurante
        String rutaLogo = "restaurantes/" + idRestaurante + "/logo.jpg";
        StorageReference storageReference = storage.getReference(rutaLogo);
        ImageView imageRestaurante = findViewById(R.id.imageRestaurante);

        Glide.with(this)
                .load(storageReference)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_errorimg)
                .into(imageRestaurante);

        // Cargar la lista de platos del restaurante
        db.collection("Platos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Platillo> listaPlatos = new ArrayList<>();

                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Platillo plato = document.toObject(Platillo.class); // Convierte el documento a un objeto Platillo
                        listaPlatos.add(plato); // Se añade a la lista
                    }

                    configurarAdapter(listaPlatos); // Envía la lista al adapter
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al cargar platos", e));
    }

    private void configurarAdapter(List<Platillo> listaPlatos) {
        AdministradorRestauranteAdapter adapter = new AdministradorRestauranteAdapter();
        adapter.setContext(this);
        adapter.setListaPlatos(listaPlatos);

        RecyclerView recyclerView = findViewById(R.id.recyclerRestauranteAdmin);
        recyclerView.setAdapter(adapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columnas
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_navigation_admin, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.reports){
            startActivity(new Intent(this, AdministradorReporteClientesActivity.class));
            return true;
        } else if (item.getItemId()==R.id.information) {
            startActivity(new Intent(this, AdministradorInfoRestauranteActivity.class));
            return true;
        } else if (item.getItemId()==R.id.principal) {
            startActivity(new Intent(this, AdministradorRestauranteActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            //startActivity(new Intent(this, AdministradorRestauranteActivity.class));
            return true;
        } else{
            return super.onOptionsItemSelected(item);

        }

    }

}
