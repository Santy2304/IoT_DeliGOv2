package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.R;

public class AdministradorRegistroPlatoExitosoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_registro_plato_final);

        Button btnContinuar = findViewById(R.id.botonRegistroPlatoFinalizado);

        btnContinuar.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdministradorRestauranteActivity.class);
            startActivity(intent);
        });

    }
}
