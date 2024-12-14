package com.example.deligov2.SuperAdmin.Home.Perfiles;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SuperAdminVistaPerfilCliente extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_vista_perfil_cliente);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();        // Obtener los datos del intent anterior a este
        Usuario clienteDetail = (Usuario) getIntent().getSerializableExtra("cliente_detail");

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
                    Intent intent = new Intent(SuperAdminVistaPerfilCliente.this, SuperAdminVistaLogEvent.class);
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
                    Intent intent = new Intent(SuperAdminVistaPerfilCliente.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminVistaPerfilCliente.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminVistaPerfilCliente.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        // Inicialización de vistas
        MaterialTextView nameTextView = findViewById(R.id.name);
        MaterialTextView dniTextView = findViewById(R.id.n_dni);
        MaterialTextView correoTextView = findViewById(R.id.correo);
        MaterialTextView ubicacionTextView = findViewById(R.id.tv_ubicacion);
        ImageView perfilImageView = findViewById(R.id.imgSAperfil);

        if (clienteDetail != null) {
            // Rellenar los datos desde el objeto Usuario
            nameTextView.setText(clienteDetail.getNombre());
            dniTextView.setText(clienteDetail.getNumDocument());
            correoTextView.setText(clienteDetail.getCorreo());
            ubicacionTextView.setText(clienteDetail.getDireccion());

            // Descargar y mostrar la imagen de perfil desde Firebase Storage
            Glide.with(this)
                    .load(clienteDetail.getFotoUrl())
                    .circleCrop()
                    .placeholder(R.drawable.circular_image_background)
                    .into(perfilImageView);

        }else{
            Log.e("SuperAdminVistaPerfilCliente", "clienteDetail es nulo");
        }

        //Atras
        FloatingActionButton btAtras = findViewById(R.id.backButton);

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}