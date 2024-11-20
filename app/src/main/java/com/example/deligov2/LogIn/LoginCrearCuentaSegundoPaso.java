package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginCrearCuentaSegundoPaso extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLocation;
    private TextInputEditText direccionInput, referenciaInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_crear_cuenta_segundo_paso);

        // Configuración de la ventana
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar campos
        direccionInput = findViewById(R.id.direccionInput);
        referenciaInput = findViewById(R.id.referenciaInput);

        // Configurar el mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapa);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Configuración inicial del mapa
        LatLng defaultLocation = new LatLng(-12.0464, -77.0428); // Lima, Perú
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));

        // Seleccionar ubicación al hacer clic en el mapa
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            selectedLocation = latLng;
        });
    }

    public void validarFormulario(View view) {
        String direccion = direccionInput.getText().toString();
        String referencia = referenciaInput.getText().toString();

        if (TextUtils.isEmpty(direccion)) {
            Toast.makeText(this, "Por favor, ingrese su dirección.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(referencia)) {
            Toast.makeText(this, "Por favor, ingrese una referencia.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedLocation == null) {
            Toast.makeText(this, "Por favor, seleccione una ubicación en el mapa.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Continuar al siguiente paso
        Intent intent = new Intent(this, LoginCrearCuentaTercerPaso.class);
        intent.putExtra("direccion", direccion);
        intent.putExtra("referencia", referencia);
        intent.putExtra("latitud", selectedLocation.latitude);
        intent.putExtra("longitud", selectedLocation.longitude);
        startActivity(intent);
    }
}