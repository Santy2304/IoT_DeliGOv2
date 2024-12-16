package com.example.deligov2.SuperAdmin.Home.Perfiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
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

public class SuperAdminVistaPerfilRepartidor extends AppCompatActivity {
    private FirebaseFirestore db;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_vista_perfil_repartidor);
        db = FirebaseFirestore.getInstance();
        Usuario repartidor = (Usuario) getIntent().getSerializableExtra("repatidor_detail");
        // Obtener los datos del intent anterior a este
        //Manejo del top app bar
        /*
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
                    Intent intent = new Intent(SuperAdminVistaPerfilRepartidor.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

         */

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.principal);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminVistaPerfilRepartidor.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminVistaPerfilRepartidor.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminVistaPerfilRepartidor.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Llenar datos de la vista
        MaterialTextView name = findViewById(R.id.name);
        MaterialTextView nDni = findViewById(R.id.n_dni);
        MaterialTextView correo =  findViewById(R.id.correo);
        MaterialTextView ubicacion = findViewById(R.id.tv_ubicacion);
        name.setText(repartidor.getNombre());
        nDni.setText(repartidor.getNumDocument());
        correo.setText(repartidor.getCorreo());
        ubicacion.setText(repartidor.getDireccion());
        ( (MaterialTextView)findViewById(R.id.doc)).setText(repartidor.getTipoDocumento());
        findViewById(R.id.imgSAperfil);
        Glide.with(this) // Context o View
                .load(repartidor.getFotoUrl()) // URL o URI de la imagen
                .placeholder(R.drawable.ic_loading) // Imagen de carga mientras se descarga
                .error(R.drawable.ic_loading) // Imagen de error si falla
                .into((ImageView) findViewById(R.id.imgSAperfil));

        //atras
        FloatingActionButton btAtras = findViewById(R.id.bt_atras);

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}