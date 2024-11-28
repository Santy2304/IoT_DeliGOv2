package com.example.deligov2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.LogIn.InicioSesion.LoginVistaInicialApp;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void verCliente(View view) {
        startActivity(new Intent(this, ClienteHomeActivity.class));
    }
    public void verRepartidor(View view) {
        startActivity(new Intent(this, RepartidorVistaHome.class));
    }
    public void verAdministrador(View view) {
        startActivity(new Intent(this, AdministradorHomeActivity.class));
    }
    public void verSuperadministrador(View view) {
         startActivity(new Intent(this, SuperAdminHomeActivity.class));

    }
    public void verLogin(View view) {
        startActivity(new Intent(this, LoginVistaInicialApp.class));
    }
    public void loadUser(){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                            }
                        }
                    }
                });
    }

}