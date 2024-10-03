package com.example.deligov2.LogIn;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.deligov2.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginInicioSesion extends AppCompatActivity {
    String[] credenciales =  { "cliente" , "repartidor" , "administrador" , "superadministrador" };
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }




    public void RecuperarPassword(View view) {
        Intent intent = new Intent(LoginInicioSesion.this, LoginRecuperarPasswordPrimerPaso.class);
        startActivity(intent);//Sin destruir el activity;
    }


    public void crearCuenta(View view){
        Intent intent = new Intent(LoginInicioSesion.this, LoginCrearCuentaPrimerPaso.class);
        startActivity(intent);//Sin destruir el activity;
    }
    public void iniciarSesion(View view){
        TextInputEditText email = findViewById(R.id.email);
        TextInputEditText  password = findViewById(R.id.password);
        String emailEmail = email.getText().toString();
        String passwordPassword =  password.getText().toString();

        if(emailEmail.equals(credenciales[0]) ){

        }
    }




}