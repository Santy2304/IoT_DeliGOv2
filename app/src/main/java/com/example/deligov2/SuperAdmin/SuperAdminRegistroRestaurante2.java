package com.example.deligov2.SuperAdmin;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.List;

public class SuperAdminRegistroRestaurante2 extends AppCompatActivity implements OnMapReadyCallback {

    private Button btContinuar;
    private Button btCancelar;
    String canal1 = "importanteDefault";

    private GoogleMap myMap;
    private SearchView mapSearchView;
    private Marker currentMarker;

    private FirebaseFirestore db;
    //Para guardar la foto
    private StorageReference storageReference;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_restaurante2);

        db = FirebaseFirestore.getInstance();

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRegistroRestaurante2.this, SuperAdminVistaLogEvent.class);
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
                    Intent intentRestaurant = new Intent(SuperAdminRegistroRestaurante2.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRegistroRestaurante2.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRegistroRestaurante2.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Restaurante nameR = (Restaurante) intent.getSerializableExtra("nameR");

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        // Procesar la imagen
        String imageUriString = intent.getStringExtra("imageUri");
        byte[] imageByteArray = intent.getByteArrayExtra("imageBitmap");
        Log.d("IMAGEN GOOD","GOOO: "+imageUriString+ "-"+ imageByteArray);

        if (nameR != null) {
            String cNameR = nameR.getNombre();
            String categoriaR = nameR.getCategorias();
            Log.d("Registro restaurante 2", "Nombre del restaurante: " + cNameR);
        } else {
            Log.d("Registro restaurante 2", "Nombre del restaurante no disponible");
        }
        //Mapa
        mapSearchView = findViewById(R.id.mapSearch);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //String location = mapSearchView.getQuery().toString();
                String location = s.trim();
                List<Address> addressList = null;
                if(location != null && !location.isEmpty()){
                    Geocoder geocoder = new Geocoder(SuperAdminRegistroRestaurante2.this);
                    try{
                        addressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }

                    if (addressList != null && !addressList.isEmpty()) {
                        // Si hay un marcador anterior, lo eliminamos
                        if (currentMarker != null) {
                            currentMarker.remove();
                        }

                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

                        currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title(location));
                        myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
                        nameR.setDireccion(location);

                    } else {
                        Toast.makeText(SuperAdminRegistroRestaurante2.this, "No se encontró ninguna dirección para: " + location, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SuperAdminRegistroRestaurante2.this, "Por favor ingresa un lugar válido", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        mapFragment.getMapAsync( SuperAdminRegistroRestaurante2.this);


        //Notificaciones
        crearCanalesNotificacion();
        //Manejo de botones
        btContinuar = (Button) findViewById(R.id.continuar1);
        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificarRestauranteCreado(nameR.getNombre());
                //vistaRegistroRestauranteCorrect(nameR);
                registrarRestauranteFirestore(nameR, imageUriString, imageByteArray);

            }
        });

        btCancelar = (Button) findViewById(R.id.cancelar1);
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaPanelRestaurante();
            }
        });
    }



    //Cambio vista

    public void vistaRegistroRestauranteCorrect(Restaurante restaurante){
        Intent intent = new Intent(this, SuperAdminRegistroRestauranteCorrect.class);
        intent.putExtra("nr",restaurante); //Se envía el restaurante

        Log.d("ID FIREBASE RESTAURANTE", "ID:" +restaurante.getId());
        startActivity(intent);
    }
    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }

    //Notificación de que se ha creado un restaurante
    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canal1,
                "Canal notificaciones default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(SuperAdminRegistroRestaurante2.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }

    public void notificarRestauranteCreado(String nameR){

        //Crear notificación
        //Agregar información a la notificación que luego sea enviada a la actividad que se abre
        Intent intent = new Intent(this, SuperAdminVistaLogEvent.class);
        intent.putExtra("restaurante",nameR); //Falta afinar detalles
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        //
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal1)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle("DeliGO")
                .setContentText("Se ha creado el restaurante: " + nameR)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        //Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(281, notification);
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap){
        myMap = googleMap;

        myMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (currentMarker != null) {
                    currentMarker.remove();
                }

                currentMarker = myMap.addMarker(new MarkerOptions().position(latLng).title("Nueva ubicación"));

                myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));

                Log.d("Ubicación seleccionada", "Lat: " + latLng.latitude + ", Lng: " + latLng.longitude);
            }
        });
    }

    private void registrarRestauranteFirestore(Restaurante restaurante, String imageUriString, byte[] imageByteArray) {

        Restaurante newRestaurant = new Restaurante();
        newRestaurant.setCategorias(restaurante.getCategorias());
        newRestaurant.setNombre(restaurante.getNombre());
        newRestaurant.setMonto(0.00f);
        newRestaurant.setEstado(true);
        newRestaurant.setHorario(restaurante.getHorario());
        newRestaurant.setDireccion(restaurante.getDireccion());

        db.collection("restaurantes")
                .add(newRestaurant)
                .addOnSuccessListener(documentReference -> {
                    String idRestaurante = documentReference.getId();
                    newRestaurant.setId(idRestaurante);

                    // Actualizar el campo 'id' en Firestore con el ID generado
                    documentReference.update("id", idRestaurante)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID actualizado correctamente en el documento."))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar el campo ID", e));

                    Log.d("Firestore", "Restaurante registrado con ID: " + idRestaurante);

                    // Subir imagen a Firebase Storage
                    subirImagenAFirebaseStorage(idRestaurante, imageUriString, imageByteArray);

                    vistaRegistroRestauranteCorrect(newRestaurant);

                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al registrar el restaurante", e);
                });

    }

    private void subirImagenAFirebaseStorage(String idRestaurante, String imageUriString, byte[] imageByteArray) {
        if (imageUriString != null) {
            // Subir desde Uri
            Uri imageUri = Uri.parse(imageUriString);
            StorageReference imageRef = storageReference.child("restaurantes/" + idRestaurante + "/logo.jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("Firebase Storage", "Imagen subida correctamente desde Uri.");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firebase Storage", "Error al subir la imagen desde Uri", e);
                    });

        } else if (imageByteArray != null) {
            // Subir desde byte[]
            StorageReference imageRef = storageReference.child("restaurantes/" + idRestaurante + "/logo.jpg");

            imageRef.putBytes(imageByteArray)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("Firebase Storage", "Imagen subida correctamente desde byte[].");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firebase Storage", "Error al subir la imagen desde byte[]", e);
                    });

        } else {
            Log.w("Firebase Storage", "No se encontró una imagen para subir.");
        }
    }

}