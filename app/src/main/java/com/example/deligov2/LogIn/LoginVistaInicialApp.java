package com.example.deligov2.LogIn;
import android.content.Intent;
import android.os.Handler;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;

public class LoginVistaInicialApp extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_vista_inicial_app);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Inicializar el MediaPlayer con el archivo song.mp3 en res/raw
        mediaPlayer = MediaPlayer.create(this, R.raw.song);
        // Comenzar a reproducir la canción
        mediaPlayer.start();
        // Cambiar a otra actividad después de 15 segundos
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Cambiar a la nueva Activity
                Intent intent = new Intent(LoginVistaInicialApp.this, LoginInicioSesion.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                // Finalizar la actividad actual
                finish();
            }
        }, 10000);  // 15000 milisegundos = 15 segundos
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar el MediaPlayer cuando se destruya la actividad para evitar fugas de memoria
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        // Detener el Handler cuando la actividad sea destruida
        handler.removeCallbacksAndMessages(null);
    }
}