package com.example.deligov2.SuperAdmin.Restaurantes.RegistrarAdministrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.google.firebase.firestore.FirebaseFirestore;

public class SuperAdminRegistroAdminCorrect extends AppCompatActivity {
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_admin_correct);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");
        //Manejo de botones
        Button btContinuar = findViewById(R.id.button);

        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaPanelRestaurante(sa);
            }
        });
    }

    public void vistaPanelRestaurante(Usuario sa){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }
}