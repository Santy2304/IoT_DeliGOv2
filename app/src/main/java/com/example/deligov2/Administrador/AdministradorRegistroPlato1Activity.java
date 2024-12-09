package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class AdministradorRegistroPlato1Activity extends AppCompatActivity {

    TextInputEditText eTnombrePlato, eTprecioPlato, eTdescripcionPlato;
    Button btnContinuar, btnCancelar;
    FirebaseFirestore db;
    FirebaseUser user;
    FirebaseAuth auth;
    String idRestaurante;
    Platillo plato = new Platillo();

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

        // Obtener referencias a los botones
        btnContinuar = findViewById(R.id.continuarRegistroPlato);
        btnCancelar = findViewById(R.id.cancelar1);

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

        // Lógica del botón continuar
        btnContinuar.setOnClickListener(view -> {
            // Obtener los valores de los campos de entrada de texto
            String nombrePlato = eTnombrePlato.getText().toString().trim();
            String precioPlatoStr = eTprecioPlato.getText().toString().trim();
            String descripcionPlato = eTdescripcionPlato.getText().toString().trim();

            // Validar que los campos no estén vacíos
            if (TextUtils.isEmpty(nombrePlato) || TextUtils.isEmpty(precioPlatoStr) || TextUtils.isEmpty(descripcionPlato)) {
                // Mostrar un mensaje de error
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir el precio a un valor numérico
            float precioPlato;
            try {
                precioPlato = Float.parseFloat(precioPlatoStr);
            } catch (NumberFormatException e) {
                // Mostrar un mensaje de error si el precio no es válido
                Toast.makeText(this, "El precio debe ser un número válido", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear un nuevo plato
            plato.setNombre(nombrePlato);
            plato.setPrecio(precioPlato);
            plato.setDescripcion(descripcionPlato);
            plato.setIdRestaurante(idRestaurante);
            plato.setVisibilidad(true); // Establecer visibilidad en true por default
            plato.setCantVentaTotal(0); // Establecer cantidad de ventas en 0 por default

            // Guardar el plato en la base de datos
            String platoId = generarId();
            plato.setId(platoId);
            db.collection("Platos").document(platoId).set(plato)
                    .addOnSuccessListener(aVoid -> {
                        // Mostrar un mensaje de éxito
                        Toast.makeText(this, "Plato registrado con éxito", Toast.LENGTH_SHORT).show();
                        // Continuar a la siguiente actividad
                        Intent intent = new Intent(this, AdministradorRegistroPlato2Activity.class);
                        intent.putExtra("plato", plato); // Enviar el plato a la siguiente parte del registro
                        startActivity(intent);
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        // Mostrar un mensaje de error
                        Toast.makeText(this, "Error al registrar el plato", Toast.LENGTH_SHORT).show();
                    });
        });

    }

    private String generarId() {
        String uuid = UUID.randomUUID().toString();
        String[] parts = uuid.split("-");
        return parts[0] + parts[1];
    }
}
