package com.example.deligov2.SuperAdmin.Home.Perfiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SuperAdminVistaPerfilAdministrador extends AppCompatActivity {

    private FirebaseFirestore db;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_vista_perfil_administrador);
        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminVistaPerfilAdministrador.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminVistaPerfilAdministrador.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminVistaPerfilAdministrador.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });
        MaterialTextView nameView = findViewById(R.id.name);
        MaterialTextView restaurantView = findViewById(R.id.tv_restaurant);
        MaterialTextView dniView = findViewById(R.id.n_dni);
        MaterialTextView correoView = findViewById(R.id.correo);
        MaterialTextView ubicacionView = findViewById(R.id.tv_ubicacion);
        ImageView profileImageView = findViewById(R.id.imgSAperfil);

        // Simula un usuario para el ejemplo (puedes obtener este objeto desde un Intent, Firebase, etc.)
        // URL de prueba para la foto
        // Setea los datos en las vistas
        Usuario usuario = (Usuario) getIntent().getSerializableExtra("administrador");
        nameView.setText(usuario.getNombre() +" "+ usuario.getApellido());
        //restaurantView.setText();

        db.collection("restaurantes")
                .document(usuario.getRestaurante())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreRestaurante = documentSnapshot.getString("nombre");
                        if (nombreRestaurante != null) {
                            restaurantView.setText(nombreRestaurante);
                        } else {
                            restaurantView.setText("Nombre no disponible");
                        }
                    } else {
                        restaurantView.setText("Restaurante no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    restaurantView.setText("Error al obtener restaurante");
                    Log.e("Firestore", "Error al obtener el restaurante: ", e);
                });

        dniView.setText(usuario.getNumDocument());
        correoView.setText(usuario.getCorreo());
        ubicacionView.setText(usuario.getDireccion());
        ((MaterialTextView)findViewById(R.id.ola) ). setText(usuario.getTipoDocumento());
        // Usa Glide para cargar la imagen de perfil
        /*
        Glide.with(this)
                .load(usuario.getFotoUrl())
                .placeholder(R.drawable.ic_loading)
                .error(R.drawable.ic_errorimg)
                .into(profileImageView);

         */
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference()
                .child("users/" + usuario.getId() + "/profile.jpg");

        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(profileImageView.getContext())
                            .load(uri)
                            .placeholder(R.drawable.ic_loading)
                            .error(R.drawable.ic_errorimg)
                            .into(profileImageView);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error al cargar la imagen: ", e);
                    profileImageView.setImageResource(R.drawable.ic_errorimg);
                });
        //Atras
        FloatingActionButton btAtras = findViewById(R.id.back);

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}