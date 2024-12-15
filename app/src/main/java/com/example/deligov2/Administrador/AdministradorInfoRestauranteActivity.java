package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class AdministradorInfoRestauranteActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private ImageView logoRestaurante, bannerRestaurante;
    private TextView nombreRestaurante;
    private MaterialTextView /*nombre,*/ horario, ubicacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_info_restaurante);

        // Inicializar instancias de firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Capturar los elementos de la interfaz
        logoRestaurante = findViewById(R.id.logoRestaurante);
        bannerRestaurante = findViewById(R.id.bannerRestaurante);
        nombreRestaurante = findViewById(R.id.nombreRestaurante);
        //nombre = findViewById(R.id.restName);
        horario = findViewById(R.id.restTime);
        ubicacion = findViewById(R.id.ubicacion);


        // Obtener el UUID del usuario logueado
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Cargar el usuario desde firestore para obtener el ID del restaurante al que pertenece
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("restaurante"); // Atributo "restaurante" para obtener idRestaurante
                        cargarInformacionRestaurante(idRestaurante); // Llama al método para cargar la información del restaurante
                    } else {
                        Log.e("Firestore", "No se encontró el usuario con ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener usuario", e));

        //Navegación por medio del navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);
        bottomNavigationView.setSelectedItemId(R.id.information);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorReportesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }

    private void cargarInformacionRestaurante(String idRestaurante) {
        // Obtener los datos del restaurante de firestore
        db.collection("Restaurantes").document(idRestaurante).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreRestauranteStr = documentSnapshot.getString("nombre");
                        String horarioRestaurante = documentSnapshot.getString("horario");
                        String ubicacionRestaurante = documentSnapshot.getString("direccion");
                        // Cargar los datos en los elementos de la interfaz
                        nombreRestaurante.setText(nombreRestauranteStr);
                        //nombre.setText(nombreRestauranteStr);
                        horario.setText(horarioRestaurante);
                        ubicacion.setText(ubicacionRestaurante);
                    } else {
                        Log.e("Firestore", "No se encontró el restaurante con ID: " + idRestaurante);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener restaurante", e));

        // Cargar imagenes del restaurante
        String rutaLogo = "restaurantes/" + idRestaurante + "/logo.jpg";
        String rutaBanner = "restaurantes/" + idRestaurante + "/banner.jpg";

        StorageReference logoRef = storage.getReference(rutaLogo);
        StorageReference bannerRef = storage.getReference(rutaBanner);

        Glide.with(this)
                .load(logoRef)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_errorimg)
                .into(logoRestaurante);

        Glide.with(this)
                .load(bannerRef)
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_errorimg)
                .into(bannerRestaurante);
    }
}
