package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class AdministradorHomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_home);

        //Capturar el elemento de botón
        Button continueButton = findViewById(R.id.continuarAdmin);

        continueButton.setOnClickListener(view -> {
            Intent intent = new Intent(AdministradorHomeActivity.this, AdministradorRestauranteActivity.class);
            startActivity(intent);
        });

    }
}