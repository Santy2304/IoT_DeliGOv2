package com.example.deligov2.Repartidor.ProcesosTracking;

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
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorVistaHome;
import com.example.deligov2.Repartidor.PerfilRepartidor;
import com.example.deligov2.Repartidor.RepartidorNotificaciones;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RepartidorTrackingEstadoRecibido extends AppCompatActivity {
    String channelId = "canal";
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        storage = FirebaseStorage.getInstance();

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_tracking_estado_recibido);

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

        ShapeableImageView image = findViewById(R.id.shapeableImageView3);
        storageRef = storage.getReference().child("users/" + user.getUid() + "/profile.jpg");
        // Usa Glide para cargar la imagen
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user_icon) // Imagen de carga
                    .error(R.drawable.xd)             // Imagen de error
                    .into(image);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        });

    }

    public void  retroceder(View view){
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

    public void loadUser(){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                            }
                        }
                    }
                });
    }
}