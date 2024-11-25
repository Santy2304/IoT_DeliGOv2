package com.example.deligov2.LogIn.InicioSesion;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.LogIn.LoginPrimeraVista;
import com.example.deligov2.R;

public class LoginVistaInicialApp extends AppCompatActivity {
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_vista_inicial_app);
        //Add animations
        Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_arriba);
        Animation animation2 = AnimationUtils.loadAnimation(this, R.anim.desplazamiento_abajo);
         TextView bienvenido = findViewById(R.id.bienvenido);
        TextView mensaje = findViewById(R.id.textView);
        ImageView logo = findViewById(R.id.imageView);
        bienvenido.setAnimation(animation2);
        mensaje.setAnimation(animation2);
        logo.setAnimation(animation1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoginVistaInicialApp.this, LoginInicioActivity.class);
                startActivity(intent);
                finish();
            }
        }, 4000);
    }



}