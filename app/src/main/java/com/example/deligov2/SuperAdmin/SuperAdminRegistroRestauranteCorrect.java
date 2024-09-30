package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class SuperAdminRegistroRestauranteCorrect extends AppCompatActivity {
    Button btContinuar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_restaurante_correct);

        //Manejo de botones
        btContinuar = (Button) findViewById(R.id.button);
        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRegistroAdmin1();
            }
        });
    }

    //Cambio vistas
    public void vistaRegistroAdmin1(){
        Intent intent = new Intent(this, SuperAdminRegistroAdministrador1.class);
        startActivity(intent);
    }

}