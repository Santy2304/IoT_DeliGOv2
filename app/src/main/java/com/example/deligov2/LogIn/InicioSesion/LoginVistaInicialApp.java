package com.example.deligov2.LogIn.InicioSesion;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.LogIn.LoginPrimeraVista;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaCuartoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaSegundoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaTercerPaso;
import com.example.deligov2.R;

public class LoginVistaInicialApp extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login_vista_inicial_app);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
//                        REQUEST_CODE_POST_NOTIFICATIONS);
//            }
//        }
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permiso otorgado
//                Log.d("Permiso", "Permiso de notificaciones concedido.");
//            } else {
//                // Permiso denegado
//                Log.d("Permiso", "Permiso de notificaciones denegado.");
//            }
//        }
//    }

}