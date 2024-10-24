package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AdministradorInfoRestauranteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_info_restaurante);

        //Navegación por medio del navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorReporteClientesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorInfoRestauranteActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }
}
