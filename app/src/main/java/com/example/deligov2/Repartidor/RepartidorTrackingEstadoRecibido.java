package com.example.deligov2.Repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class RepartidorTrackingEstadoRecibido extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_tracking_estado_recibido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Iniciar la nueva actividad
                Intent intent = new Intent(RepartidorTrackingEstadoRecibido.this, RepartidorTrackingEstadoEnPreparacion.class);
                startActivity(intent);                // Finalizar la actividad actual (opcional)
                finish();
            }
        }, 1000 ); //60 segundos



    }

    public void  retroceder(View view)
    {
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