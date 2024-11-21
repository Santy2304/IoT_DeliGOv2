package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.MainActivity;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginCrearCuentaPasoFinal extends AppCompatActivity {
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
        setContentView(R.layout.activity_login_crear_cuenta_paso_final);
    }
    public void iniciar(View view){
        Intent intent;
        if(usuario.getRol().equals("Cliente")){
             intent = new Intent(LoginCrearCuentaPasoFinal.this, ClienteHomeActivity.class);

        } else if (usuario.getRol().equals("Repartidor")){
             intent = new Intent(LoginCrearCuentaPasoFinal.this, RepartidorVistaHome.class);

        } else if (usuario.getRol().equals("Administrador")){
             intent = new Intent(LoginCrearCuentaPasoFinal.this, AdministradorHomeActivity.class);

        }else {
            intent = new Intent(LoginCrearCuentaPasoFinal.this, SuperAdminHomeActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar pila
        startActivity(intent);
        finish();
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