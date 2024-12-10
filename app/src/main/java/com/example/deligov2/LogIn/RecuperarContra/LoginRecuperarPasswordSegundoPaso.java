package com.example.deligov2.LogIn.RecuperarContra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class LoginRecuperarPasswordSegundoPaso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_recuperar_password_segundo_paso);

    }
    public void irTercerPaso(View view){
        Intent intent = new Intent(LoginRecuperarPasswordSegundoPaso.this, LoginRecuperarPasswordTercerPaso.class);
        startActivity(intent);//Sin destruir el activity;
    }
    public void retroceder(View view) {
        // Simular comportamiento de retroceso
        onBackPressed();
    }
}