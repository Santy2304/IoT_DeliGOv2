package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaCuartoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaSegundoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaTercerPaso;
import com.example.deligov2.MainActivity;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.RepartidorHomeActivity;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kotlin.collections.ArrayDeque;

public class LoginPrimeraVista extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button comenzarButton;
    private List<Usuario> userList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userList = new ArrayList<>();
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        loadUsers();
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_primera_vista);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if(user!=null){
            if(user.isEmailVerified()){
                db.collection("Usuarios").document(user.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                assert usuario != null;
                                if (usuario.getNumeroTelefono()!=null){
                                    if(usuario.getDireccion()!=null){
                                        if(usuario.getFotoUrl()!=null){
                                            if(usuario.getRol()!=null){
                                                if(usuario.getRol().equals("Cliente")){
                                                    goCliente();
                                                } else if (usuario.getRol().equals("Repartidor")) {
                                                    goRepartidor();
                                                } else if (usuario.getRol().equals("Administrador")) {
                                                    goAdmin();
                                                }else {
                                                    goSuper(usuario);
                                                }
                                            }else{
                                                Intent intent = new Intent(LoginPrimeraVista.this, LoginCrearCuentaCuartoPaso.class);
                                                startActivity(intent);
                                            }
                                        }else {
                                            Intent intent = new Intent(LoginPrimeraVista.this, LoginCrearCuentaTercerPaso.class);
                                            startActivity(intent);
                                        }
                                    }else {
                                        Intent intent = new Intent(LoginPrimeraVista.this, LoginCrearCuentaSegundoPaso.class);
                                        startActivity(intent);
                                    }
                                }else{
                                    Intent intent = new Intent(LoginPrimeraVista.this, LoginCrearCuentaPrimerPaso.class);
                                    startActivity(intent);
                                }
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                Log.d("msg-test", "Firebase uid: " + user.getUid());
            }

        }
        comenzarButton = findViewById(R.id.comenzarButton);
        comenzarButton.setOnClickListener(view -> {
            AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                    .setGoogleButtonId(R.id.IniciarSesionGoogle)
                    .setEmailButtonId(R.id.IniciarSesionButton)
                    .build();

            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(authMethodPickerLayout)
                    .setTheme(R.style.Base_Theme_DeliGOv2)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    ))
                    .build();
            signInLauncher.launch(intent);
        });
    }
    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user!=null){
                        user.reload().addOnCompleteListener(task -> {
                            if(user.isEmailVerified()){
                                Log.d("msg-test", "Firebase uid: " + user.getUid());
                                db.collection("Usuarios").document(user.getUid()).get()
                                        .addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()) {
                                                Usuario usuario = documentSnapshot.toObject(Usuario.class);

                                                if(usuario.getRol().equals("Cliente")){
                                                    goCliente();
                                                } else if (usuario.getRol().equals("Repartidor")) {
                                                    goRepartidor();
                                                } else if (usuario.getRol().equals("Administrador")) {
                                                    goAdmin();
                                                }else if (usuario.getRol().equals("SuperAdmin")){
                                                    goSuper(usuario);
                                                }
                                            } else {
                                                Intent intent = new Intent(LoginPrimeraVista.this,LoginCrearCuentaPrimerPaso.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        })
                                        .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));
                            }else {
                                user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                    Toast.makeText(LoginPrimeraVista.this,"Se le ha enviado un correo para validar la cuenta",Toast.LENGTH_SHORT).show();
                                });
                            }
                        });

                    }else {
                        Log.d("msg-test", "user es nulo");
                    }
                } else {
                    Log.d("msg-test", "Canceló el Log-in");
                }
            }
    );

    public void goCliente(){
        Intent intent = new Intent(LoginPrimeraVista.this, ClienteHomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void goRepartidor(){
        Intent intent = new Intent(LoginPrimeraVista.this, RepartidorVistaHome.class);
        startActivity(intent);
        finish();
    }
    public void goAdmin(){
        Intent intent = new Intent(LoginPrimeraVista.this, AdministradorHomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void goSuper(Usuario user){
        Intent intent = new Intent(LoginPrimeraVista.this, SuperAdminHomeActivity.class);
        intent.putExtra("sa",user);
        Log.d("PROBANDO 123","LOGIN OLA" + user.getId()+"-"+user.getNombre());
        startActivity(intent);
        finish();
    }
    public void loadUsers(){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if(userList !=null){
                            userList.clear();
                        }
                        // Limpiar la lista antes de agregar nuevos datos
                        if(value != null){
                            for (QueryDocumentSnapshot document : value) {
                                Usuario user2 = document.toObject(Usuario.class);
                                userList.add(user2); // Agregar usuario a la lista
                            }
                        }
                    }
                });
    }

}