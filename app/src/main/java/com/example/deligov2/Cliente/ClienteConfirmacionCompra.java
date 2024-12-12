package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.deligov2.R;
import com.example.deligov2.Workers.ContadorWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteConfirmacionCompra extends AppCompatActivity {
    TextView id;
    Button backMenuButton;
    Button trackingButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_confirmacion_compra);
        Intent intent = getIntent();
        String idPedido = intent.getStringExtra("id");

        id = findViewById(R.id.idCompra);
        id.setText("#"+idPedido);
        iniciarPedidoWorker(idPedido);

        backMenuButton = findViewById(R.id.goToMenu);
        trackingButton = findViewById(R.id.followOrderButton);

        backMenuButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(this,ClienteHomeActivity.class);
            startActivity(intent1);
            finish();
        });

        trackingButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(this,ClienteTrackingActivity.class);
            intent1.putExtra("idOrder",idPedido);
            startActivity(intent1);
            finish();
        });

    }
    private void iniciarPedidoWorker(String idPedido) {
        Data inputData = new Data.Builder()
                .putString("pedidoId", idPedido)
                .build();

        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(ContadorWorker.class)
                .setInputData(inputData)
                .build();

        WorkManager.getInstance(this).enqueue(workRequest);

    }
}