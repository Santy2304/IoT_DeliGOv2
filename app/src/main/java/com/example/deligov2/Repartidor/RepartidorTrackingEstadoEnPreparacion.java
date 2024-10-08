package com.example.deligov2.Repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class RepartidorTrackingEstadoEnPreparacion extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_tracking_estado_en_preparacion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void verSiguienteTracking(View view) {
        Intent intent = new Intent(this, RepartidorTrackingEstadoEnCamino.class);
        startActivity(intent);
    }

    public void retroceder(View view){
        Intent intent = new Intent(this, RepartidorVistaHome.class);
        startActivity(intent);
    }

    public void verNotificacionesRepartidor(View view){
        Intent intent = new Intent(this, RepartidorNotificaciones.class);
        startActivity(intent);
    }

    public void verPerfil(View view){
        Intent intent = new Intent(this, PerfilRepartidor.class);
        startActivity(intent);
    }
}