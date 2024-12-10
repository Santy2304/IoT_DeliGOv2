package com.example.deligov2.LogIn.Registro;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LoginCrearCuentaCuartoPaso extends AppCompatActivity {
    private String[] selectedRole = {null};
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
        setContentView(R.layout.activity_login_crear_cuenta_cuarto_paso);

        MaterialCardView cardCliente = findViewById(R.id.card_cliente);
        MaterialCardView cardRepartidor = findViewById(R.id.card_repartidor);
        // Variables para almacenar el rol seleccionado
        // Guardará "Cliente" o "Repartidor"

        // Listener para la tarjeta Cliente
        cardCliente.setOnClickListener(v -> {
            selectedRole[0] = "Cliente";

            // Cambiar colores visualmente para mostrar que está seleccionado
            cardCliente.setCardBackgroundColor(getResources().getColor(R.color.light_green));
            cardCliente.setStrokeColor(getResources().getColor(R.color.dark_green));

            // Revertir el estilo de la otra tarjeta
            cardRepartidor.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            cardRepartidor.setStrokeColor(getResources().getColor(R.color.gray));
        });

        // Listener para la tarjeta Repartidor
        cardRepartidor.setOnClickListener(v -> {
            selectedRole[0] = "Repartidor";

            // Cambiar colores visualmente para mostrar que está seleccionado
            cardRepartidor.setCardBackgroundColor(getResources().getColor(R.color.light_green));
            cardRepartidor.setStrokeColor(getResources().getColor(R.color.dark_green));

            // Revertir el estilo de la otra tarjeta
            cardCliente.setCardBackgroundColor(getResources().getColor(android.R.color.white));
            cardCliente.setStrokeColor(getResources().getColor(R.color.gray));
        });

        // Botón Terminar
        Button terminarButton = findViewById(R.id.continuar1);
        terminarButton.setOnClickListener(v -> {
            if (selectedRole[0] != null) {
                // Guardar el rol seleccionado en Firebase, preferencias, o pasar a la siguiente pantalla
                Toast.makeText(this, "Rol seleccionado: " + selectedRole[0], Toast.LENGTH_SHORT).show();
                continuar();
                // Aquí puedes enviar el rol a la siguiente actividad o guardarlo en Firestore
            } else {
                // Mostrar error si no se seleccionó ningún rol
                Toast.makeText(this, "Por favor, selecciona un rol antes de continuar", Toast.LENGTH_SHORT).show();
                continuar();
            }
        });
    }

    public void retroceder(View view) {
        // Simular comportamiento de retroceso
        onBackPressed();
    }

    public void continuar(){
        if(selectedRole[0] == null){
            Toast.makeText(this, "Debes de escoger un rol dentro de esta aplicacion", Toast.LENGTH_SHORT).show();
        }else{
            usuario.setRol(selectedRole[0]);
            if(!usuario.getRol().equals("Cliente")){
                usuario.setAprobado("PorValidar");
            }else{
                usuario.setAprobado("Aceptado");
            }
            guardarDatosEnFirestore();
        }
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
    private void guardarDatosEnFirestore() {
        db.collection("Usuarios")
                .document(usuario.getId())
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    irAlSiguientePaso();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al registrar usuario", e);
                    Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
                });
    }
    private void irAlSiguientePaso() {
        Intent intent = new Intent(this, LoginCrearCuentaPasoFinal.class);
        startActivity(intent);
    }

}