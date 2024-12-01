package com.example.deligov2.LogIn.Registro;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class LoginCrearCuentaSegundoPaso extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LatLng selectedLocation;
    private TextInputEditText addressInput, referenceInput;
    private String latitud , longitud;
    private boolean direccionValida = false;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_crear_cuenta_segundo_paso);
        addressInput = findViewById(R.id.addressInput);
        referenceInput = findViewById(R.id.referenceInput);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        addressInput.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1000;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer.cancel(); // Reinicia el temporizador si el usuario sigue escribiendo
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel(); // Reinicia el temporizador si el usuario sigue escribiendo
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Aquí se ejecuta la acción cuando el usuario deja de escribir
                        runOnUiThread(() -> {
                            // Código a ejecutar en el hilo principal
                            searchAddress(addressInput.getText().toString());
                            Log.d("Input", "El usuario dejó de escribir. Texto actual: " + addressInput.getText().toString());
                        });
                    }
                }, DELAY); // Ejecuta después del tiempo definido en DELAY
            }
        });
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(-12.0464, -77.0428); // Lima, Perú
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            selectedLocation = latLng;
            addressInput.setText(obtenerDireccionDesdeLatLng(selectedLocation));
        });
    }
    private String obtenerDireccionDesdeLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                direccionValida =true;
                return direccion.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("GeocoderError", "No se pudo obtener la dirección", e);
            direccionValida =false;
        }
        return null;
    }
    public void searchAddress(String  address) {
        if (!address.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(selectedLocation).title("Ubicación encontrada"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                    direccionValida =true;
                } else {
                    direccionValida =false;
                    Toast.makeText(this, "No se encontró la dirección", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al buscar la dirección", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, ingresa una dirección", Toast.LENGTH_SHORT).show();
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

    public void continuar(View view) {
        if(!direccionValida){
            //Setear error por dirección
            addressInput.setError("Debe ingresar una dirección válida");
        }
        if(referenceInput.getText().toString().trim().equals("")){
            //Setear error
            referenceInput.setError("Debe ingresar una referencia");
        }
        if((referenceInput.getText().toString().trim() == null || !direccionValida)){
           //No enviar formulario
        }else{
            //Enviar formulario
            usuario.setDireccion(addressInput.getText().toString());
            usuario.setLatitud("" + selectedLocation.latitude);
            usuario.setLongitud("" +selectedLocation.longitude);
            usuario.setReferencia(referenceInput.getText().toString());
            guardarDatosEnFirestore();
        }
    }

    private void guardarDatosEnFirestore() {
        db.collection("Usuarios")
                .document(usuario.getId())
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    irAlSiguientePaso(usuario);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al registrar usuario", e);
                    Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
                });
    }

    private void irAlSiguientePaso(Usuario usuario) {
        Intent intent = new Intent(this, LoginCrearCuentaTercerPaso.class);
        startActivity(intent);
    }
}