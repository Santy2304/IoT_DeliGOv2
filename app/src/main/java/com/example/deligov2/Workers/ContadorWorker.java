package com.example.deligov2.Workers;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.deligov2.Cliente.ClienteTrackingActivity;
import com.example.deligov2.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Random;

public class ContadorWorker extends Worker {

    private static final String CHANNEL_ID = "pedido_notifications";
    private static final int NOTIFICATION_ID_1 = 1;
    private static final int NOTIFICATION_ID_2 = 2;
    public ContadorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String pedidoId = getInputData().getString("pedidoId");

        try {
            int tiempoParte1 = (new Random().nextInt(3) + 1) * 60 * 1000;
            Log.d("PedidoWorker", "Esperando " + tiempoParte1 / 1000 + " segundos para la primera parte.");
            Thread.sleep(tiempoParte1);

            cambiarEstadoPedido(pedidoId, "En Preparación");
            enviarNotificacion("Pedido Preparándose", "Tu pedido ahora está en preparación.", NOTIFICATION_ID_1);

            FirebaseFirestore db = FirebaseFirestore.getInstance();


            int tiempoParte2 = (new Random().nextInt(3) + 1) * 60 * 1000;
            Log.d("PedidoWorker", "Esperando " + tiempoParte2 / 1000 + " segundos para la segunda parte.");
            Thread.sleep(tiempoParte2);

            cambiarEstadoPedido(pedidoId, "Listo");
            enviarNotificacion("Pedido Listo", "El pedido está listo y esperando por un repartidor para inicar el camnio.", NOTIFICATION_ID_2);

        } catch (InterruptedException e) {
            Log.e("PedidoWorker", "Error en el temporizador: " + e.getMessage());
            return Result.failure();
        }

        return Result.success();
    }
    private void cambiarEstadoPedido(String pedidoId, String nuevoEstado) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Pedidos").document(pedidoId)
                .update("estado", nuevoEstado)
                .addOnSuccessListener(aVoid -> Log.d("PedidoWorker", "Estado actualizado a " + nuevoEstado))
                .addOnFailureListener(e -> Log.e("PedidoWorker", "Error al actualizar estado: " + e.getMessage()));
    }

    private void enviarNotificacion(String titulo, String mensaje, int notificationId) {
        Context context = getApplicationContext();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d("PedidoWorker", "No se tienen permisos para enviar notificaciones.");
                return;
            }
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Notificaciones de Pedido",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle(titulo)
                .setContentText(mensaje)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(notificationId, builder.build());
    }

}
