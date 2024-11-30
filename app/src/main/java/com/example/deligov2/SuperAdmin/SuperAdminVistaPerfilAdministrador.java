package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SuperAdminVistaPerfilAdministrador extends AppCompatActivity {

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_vista_perfil_administrador);
        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto de aquí te manda a la vista anterior
                onBackPressed();
            }
        });

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminVistaPerfilAdministrador.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

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
        //Llenamos los datos
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
        nameView.setText(usuario.getNombre());
        restaurantView.setText(usuario.getRestaurante());
        dniView.setText(usuario.getNumDocument());
        correoView.setText(usuario.getCorreo());
        ubicacionView.setText(usuario.getDireccion());
        // Usa Glide para cargar la imagen de perfil
        Glide.with(this)
                .load(usuario.getFotoUrl())
                .placeholder(R.drawable.ic_loading) // Imagen mientras se carga
                .error(R.drawable.ic_errorimg)       // Imagen si falla la carga
                .into(profileImageView);

    }
}