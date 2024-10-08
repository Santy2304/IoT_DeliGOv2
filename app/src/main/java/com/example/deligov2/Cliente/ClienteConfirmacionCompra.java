package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ClienteConfirmacionCompra extends AppCompatActivity {

    Button backMenuButton;
    Button trackingButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cliente_confirmacion_compra);

        backMenuButton = findViewById(R.id.goToMenu);
        trackingButton = findViewById(R.id.followOrderButton);

        backMenuButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHomeActivity.class);
            startActivity(intent);
        });

        trackingButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteTrackingActivity.class);
            startActivity(intent);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void verPerfil(View view){
        Intent intent = new Intent(this, ClientePerfil.class);
        startActivity(intent);
    }

    public void verHistorial(View view){
        Intent intent = new Intent(this, ClienteHistorialActivity.class);
        startActivity(intent);
    }

    public void verHome(View view){
        Intent intent = new Intent(this, ClienteHomeActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.historial){
            startActivity(new Intent(this, ClienteHistorialActivity.class));
            return true;
        } else if (item.getItemId()==R.id.restaurant) {
            startActivity(new Intent(this, ClienteRestaurantActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            startActivity(new Intent(this, ClientePerfil.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);

        }

    }
}