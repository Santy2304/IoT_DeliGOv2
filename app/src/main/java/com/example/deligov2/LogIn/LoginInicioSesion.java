package com.example.deligov2.LogIn;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginInicioSesion extends AppCompatActivity {
    String channelId = "oli";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_inicio_sesion);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        crearCanalNotificacion();
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
        //BUSCAMOS LOS VALORES DE LOS CAMPOS
        TextInputEditText email = findViewById(R.id.correo);
        TextInputEditText password = findViewById(R.id.password);
        String emailStr =  email.getText().toString();
        String passwordStr =  password.getText().toString();
        int counter = 0 ;

        if(emailStr.equals("admin") && passwordStr.equals("admin")){
            startActivity(new Intent(this, AdministradorHomeActivity.class));
        }else{
            counter++;
        }

        if(emailStr.equals("superAdmin") && passwordStr.equals("superAdmin")){
            startActivity(new Intent(this, SuperAdminHomeActivity.class));
        }else{
            counter++;
        }

        if(emailStr.equals("cliente") && passwordStr.equals("cliente")){
            startActivity(new Intent(this, ClienteHomeActivity.class));
        }else{
            counter++;
        }

        if(emailStr.equals("repartidor") && passwordStr.equals("repartidor")){
            startActivity(new Intent(this, RepartidorVistaHome.class));
        }else{
            counter++;
        }

        if(counter==4){
            //ERROR
            validateFields(email,  password, findViewById(R.id.correoLayout), findViewById(R.id.passwordLayout) );
        }



    }


    private boolean validateFields(TextInputEditText correoEditText , TextInputEditText contrasenaEditText , TextInputLayout correoLayout  , TextInputLayout contrasenaLayout ) {
        String correo = correoEditText.getText().toString().trim();
        String contrasena = contrasenaEditText.getText().toString().trim();
        boolean isValid = true;

        // Validar campo de correo
        if (correo.isEmpty()) {
            correoLayout.setError("El campo de correo no puede estar vacío");
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            correoLayout.setError("Por favor, introduce un correo válido");
            isValid = false;
        } else {
            correoLayout.setError(null); // Limpiar error si es válido
        }

        // Validar campo de contraseña
        if (contrasena.isEmpty()) {
            contrasenaLayout.setError("El campo de contraseña no puede estar vacío");
            isValid = false;
        } else {
            contrasenaLayout.setError(null); // Limpiar error si es válida
        }

        return isValid;
    }

    public void crearCanalNotificacion(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Canal notificaciones default",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canal para notificaciones con prioridad default");
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            askPermission();

        }
    }


    public void askPermission(){
        //android.os.Build.VERSION_CODES.TIRAMISU == 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }

    }







}