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

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
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
        if(passwordPassword == null || emailEmail ==null){
        }else{
            if(emailEmail.equals(credenciales[0])  && passwordPassword.equals(credenciales[0])){
                Intent intent = new Intent(this, ClienteHomeActivity.class);
                startActivity(intent);
            }
            if(emailEmail.equals(credenciales[1])  && passwordPassword.equals(credenciales[1])){
                Intent intent = new Intent(this, RepartidorVistaHome.class);
                startActivity(intent);
            }
            if(emailEmail.equals(credenciales[2])  && passwordPassword.equals(credenciales[2])){
                Intent intent = new Intent(this, AdministradorHomeActivity.class);
                startActivity(intent);
            }
            if(emailEmail.equals(credenciales[3])  && passwordPassword.equals(credenciales[3])){
                Intent intent = new Intent(this, SuperAdminHomeActivity.class);
                startActivity(intent);
            }
        }


    }




}