package com.example.deligov2.Administrador;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdministradorRegistroPlato1Activity extends AppCompatActivity {

    TextInputEditText eTnombrePlato, eTprecioPlato, eTdescripcionPlato;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;
    String idRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_registro_plato_parte1);

        // Inicializar instancias de Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        // Obtener referencias a los campos de entrada de texto
        eTnombrePlato = findViewById(R.id.nombrePlato);
        eTprecioPlato = findViewById(R.id.precioPlato);
        eTdescripcionPlato = findViewById(R.id.descripcionPlato);

        //Obtener el usuario actual (administrador)
        user = auth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("Usuarios").document(uid).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Usuario usuario = documentSnapshot.toObject(Usuario.class);
                        assert usuario != null : "Usuario no creado en DB";
                        idRestaurante = usuario.getRestaurante();
                    })
                    .addOnFailureListener(e -> {
                        // Manejar el error al obtener los datos del usuario
                    });
        }


    }
}
