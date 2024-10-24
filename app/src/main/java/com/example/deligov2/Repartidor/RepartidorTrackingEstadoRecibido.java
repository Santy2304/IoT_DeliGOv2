package com.example.deligov2.Repartidor;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.R;

public class RepartidorTrackingEstadoRecibido extends AppCompatActivity {
    String channelId = "canal";

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
        crearCanalNotificacion();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lanzarNotificacion("Tu pedido está en preparación","Los cocineros ya leyeron tu pedido y lo están cocinando");
                // Iniciar la nueva actividad
                Intent intent = new Intent(RepartidorTrackingEstadoRecibido.this, RepartidorTrackingEstadoEnPreparacion.class);
                startActivity(intent);                // Finalizar la actividad actual (opcional)
                //Lanzar la notificación
                finish();
            }
        }, 5000 ); //60 segundos



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

    public void lanzarNotificacion(String title,String texto) {
        Intent intent = new Intent(this, RepartidorTrackingEstadoEnPreparacion.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle(title)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
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
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }

    }
}