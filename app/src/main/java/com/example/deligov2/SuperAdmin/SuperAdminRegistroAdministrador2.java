package com.example.deligov2.SuperAdmin;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SuperAdminRegistroAdministrador2 extends AppCompatActivity {
    private TextInputEditText adminRestaurante;
    private TextInputEditText adminCorreo;
    private Administrador adminN;
    String canal2 = "importante Otro";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_administrador2);


        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        // Obtener los datos del intent anterior a este

        Intent intent = getIntent();
        String nameR = intent.getStringExtra("nr2");
        String nameAdmin = intent.getStringExtra("adminName");
        String apellidoAdmin = intent.getStringExtra("adminApellido");
        String numDocAdmin = intent.getStringExtra("adminDoc");

        Log.d("Registro Admin 2", "Nombre del restaurante: " + nameR);
        String cNameR;
        if(nameR != null){
            cNameR = nameR;
        }else{
            cNameR = "Nombre no disponible";
        }


        adminRestaurante = findViewById(R.id.adminRestaurante);
        adminCorreo = findViewById(R.id.adminCorreo);
        adminRestaurante.setText(cNameR);

        adminN = new Administrador(0,nameAdmin,apellidoAdmin,adminCorreo.getText().toString().trim(),true,cNameR,"Av.Urubamba",numDocAdmin);


        //Manejo de botones
        Button btContinuar = findViewById(R.id.aceptar);
        TextInputEditText adminCorreo = findViewById(R.id.adminCorreo);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);

        crearCanalesNotificacion();

        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = adminCorreo.getText().toString().trim();

                if (email.isEmpty()) {
                    emailLayout.setError("El correo no puede estar vacío");
                    emailLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }

                if (!email.contains("@")) {
                    emailLayout.setError("El correo debe contener un '@'");
                    emailLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }

                emailLayout.setError(null);
                notificarAsignarAdminRestaurante(adminN,nameAdmin,nameR);
                vistaRegistroAdminCorrect();
            }
        });

        Button btCancelar = findViewById(R.id.cancelar2);

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaPanelRestaurante();
            }
        });
    }

    public void vistaRegistroAdminCorrect(){
        Intent intent = new Intent(this, SuperAdminRegistroAdminCorrect.class);
        startActivity(intent);
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }

    //Notificar cuando se registra un admin al restaurante
    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canal2,
                "Canal notificaciones default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(SuperAdminRegistroAdministrador2.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }

    public void notificarAsignarAdminRestaurante(Administrador admin, String name, String nameR){

        //Crear notificación
        //Agregar información a la notificación que luego sea enviada a la actividad que se abre
        Intent intent = new Intent(this, SuperAdminVistaLogEvent.class);
        intent.putExtra("admin",admin);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal2)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle("Deligo events")
                .setContentText("Se ha registrado el administrador "+ name +" al restaurante "+nameR)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        //Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(282, notification);
        }

    }
}