package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class LoginCrearCuentaQuintoPaso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_crear_cuenta_quinto_paso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void retroceder(View view) {
        // Simular comportamiento de retroceso
        onBackPressed();
    }
    public void verLoginCrearCuentaSextoPaso(View view){
        Intent intent = new Intent(LoginCrearCuentaQuintoPaso.this, LoginCrearCuentaSextoPaso.class);
        startActivity(intent);
    }
}