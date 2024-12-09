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

import com.example.deligov2.R;
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

}