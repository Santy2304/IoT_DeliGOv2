package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Locale;

public class SuperAdminRestauranteUbicacion extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    private FirebaseFirestore db;
    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private TextInputEditText ubicacionRestaurante;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_ubicacion);

        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

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
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        // Obtener los datos del intent anterior a este
        Restaurante resR = (Restaurante) intent.getSerializableExtra("res");
        Log.d("RESTAURANTE IDDDD RESUMEN", "OLA: "+resR.getId()+"-"+ resR.getAdmin());

        // Obtener las referencias para los datos
        tvRestaurante = findViewById(R.id.tv_restaurante);
        tvHorario = findViewById(R.id.tv_horario);
        tvCategorias = findViewById(R.id.tv_categorias);
        tvAdminRes = findViewById(R.id.tv_adminRes);
        tvEstado = findViewById(R.id.tv_estado);
        ubicacionRestaurante = findViewById(R.id.ubicacionRestaurante);

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestauranteUbicacion.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        mostrarDatosRestauranteFirebase(resR.getId());
        //Manejo de los botones
        Button btResumen = findViewById(R.id.bt_ganancias);

        btResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteResumen(v, resR, sa);
            }
        });

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v, resR, sa);
            }
        });

        Button btCarta = findViewById(R.id.bt_carta);

        btCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestaurantePlatillos(v, resR,sa);
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
        Intent intent = getIntent();
        Restaurante resRc = (Restaurante) intent.getSerializableExtra("res");

        String resUbi = resRc.getDireccion();
        LatLng ubicacion = obtenerUbicacion(resUbi);
        if (ubicacion != null) {
            mMap.addMarker(new MarkerOptions().position(ubicacion).title(resUbi));
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
    public void vistaRestauranteResumen(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        intent.putExtra("sa",sa);
        intent.putExtra("res",res);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }
    public void vistaRestaurantePlatillos(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }

    private void mostrarDatosRestauranteFirebase(String resID){
        db.collection("restaurantes")
                .document(resID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreRestaurante = documentSnapshot.getString("nombre");
                        String horario = documentSnapshot.getString("horario");
                        String categorias = documentSnapshot.getString("categorias");
                        String adminRes = documentSnapshot.getString("admin");
                        String direccionRes = documentSnapshot.getString("direccion");

                        Log.d("ID ADMINISTRADOR PT2", "OLA2" + adminRes);
                        boolean estado = documentSnapshot.getBoolean("estado");


                        db.collection("administradores")
                                .document(adminRes)
                                .get()
                                .addOnSuccessListener(documentSnapshotAdmin -> {
                                    if (documentSnapshotAdmin.exists()) {
                                        tvRestaurante.setText(nombreRestaurante != null ? nombreRestaurante : "---");
                                        tvHorario.setText(horario != null ? "Horario de atención: " + horario : "---");
                                        tvCategorias.setText(categorias != null ? "Categorías: " + categorias : "---");

                                        if (estado) {
                                            tvEstado.setText("Activado");
                                            tvEstado.setTextColor(getResources().getColor(R.color.light_green));
                                        } else {
                                            tvEstado.setText("Desactivado");
                                            tvEstado.setTextColor(getResources().getColor(R.color.md_theme_error));
                                        }

                                        String adminNombre = documentSnapshotAdmin.getString("nombre");
                                        String adminApellido = documentSnapshotAdmin.getString("apellido");
                                        String adminCompleto = (adminNombre != null ? adminNombre : "---") +
                                                " " + (adminApellido != null ? adminApellido : "---");
                                        tvAdminRes.setText("Administrador: " + adminCompleto);

                                        ubicacionRestaurante.setText(direccionRes);
                                    } else {
                                        tvAdminRes.setText("Administrador no encontrado");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    tvAdminRes.setText("Error al obtener el administrador");
                                    Log.e("Firestore", "Error al obtener el administrador: ", e);
                                });

                    } else {
                        Log.d("Firestore", "El documento no existe.");
                        tvRestaurante.setText("Restaurante no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener los datos del restaurante: ", e);
                    tvRestaurante.setText("Error al cargar datos");
                });

    }
}