package com.example.deligov2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.LogIn.LoginVistaInicialApp;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void verCliente(View view) {
        startActivity(new Intent(this, ClienteHomeActivity.class));
    }
    public void verRepartidor(View view) {
        //startActivity(new Intent(this, RepartidorActivity.class));
    }
    public void verAdministrador(View view) {
        //startActivity(new Intent(this, AdministradorActivity.class));
    }
    public void verSuperadministrador(View view) {
        //startActivity(new Intent(this, SuperAdminActivity.class));
    }
    public void verLogin(View view) {
        startActivity(new Intent(this, LoginVistaInicialApp.class));
    }


}