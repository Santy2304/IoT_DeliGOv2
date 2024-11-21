package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;
import java.util.Locale;

public class SuperAdminRestauranteUbicacion extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_ubicacion);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto de aquí te manda a la vista anterior
                onBackPressed();
            }
        });

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminVistaLogEvent.class);
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
                    Intent intentRestaurant = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Manejo de los botones
        Button btResumen = findViewById(R.id.bt_ganancias);

        btResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteResumen(v);
            }
        });

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v);
            }
        });

        Button btCarta = findViewById(R.id.bt_carta);

        btCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestaurantePlatillos(v);
            }
        });

        //Manejo del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRestaurant);
        mapFragment.getMapAsync(this);

    }
    //Mapa esto lo sque de mi proyecto, lo hice mediante busqueda en google
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap){
        mMap = googleMap;
        /*
        this.mMap.setOnMapClickListener(this::onMapClick);
        this.mMap.setOnMapLongClickListener(this::onMapLongClick);

         */
        String userUbicacion = "San Borja";
        LatLng ubicacion = obtenerUbicacion(userUbicacion);
        if (ubicacion != null) {
            mMap.addMarker(new MarkerOptions().position(ubicacion).title(userUbicacion));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
        }
    }

    private LatLng obtenerUbicacion(String strAddress) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList;
        try {
            addressList = geocoder.getFromLocationName(strAddress, 1);
            if (addressList != null && !addressList.isEmpty()) {
                Address address = addressList.get(0);
                return new LatLng(address.getLatitude(), address.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
    @Override
    public void onMapClick(@NonNull LatLng latLng){

    }

    @Override
    public void onMapLongClick(@NonNull LatLng latLng){

    }

     */
    //Cambio vista
    public void vistaRestauranteResumen(View view) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
        startActivity(intent);
    }
    public void vistaRestaurantePlatillos(View view) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        startActivity(intent);
    }
}