package com.example.deligov2.Repartidor.ProcesosTracking;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.LogSuper;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorVistaHome;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.libraries.navigation.RoadSnappedLocationProvider;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.BuildConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap.CameraPerspective;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.RoutingOptions;
import com.google.android.libraries.navigation.SimulationOptions;
import com.google.android.libraries.navigation.SupportNavigationFragment;
import com.google.android.libraries.navigation.Waypoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class RepartidorTrackingEstadoEnCamino extends AppCompatActivity {
    private Bundle mSavedInstanceState;
    private static final String KEY_JOURNEY_IN_PROGRESS = "journey_in_progress";
    private boolean mJourneyInProgress = false;
    private static final String TAG = RepartidorTrackingEstadoEnCamino.class.getSimpleName();
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCZ6AXAM3lT9VfbdDfNTSxtk-cXe0n_nDA";
    private Navigator mNavigator;
    private RoutingOptions mRoutingOptions;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private String ola = "";
    private final List<Waypoint> mWaypoints = new ArrayList<>();
    //FIRABASE
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    //Elementos Visuales
    private SupportNavigationFragment mNavFragment;
    //Datos globales
    private Pedido pedidoSupreme;
    private Restaurante restauranteSupreme;
    //PlacesId
//Necesitamos cargar los 3 idDestinos
    private String idDestino = "";
    private String placeIdRestaurante = "";

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_tracking_estado_en_camino);
        storage = FirebaseStorage.getInstance();
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
        loadPedido(() -> {
            //Cargamos pedido
            ((TextView) findViewById(R.id.pedidoId)).setText("Pedido #" + pedidoSupreme.getId());
            ((TextView) findViewById(R.id.estado)).setText(pedidoSupreme.getEstado());
            findViewById(R.id.Recoger).setOnClickListener(view -> {
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Confirmación recojo")
                        .setMessage("¿Recogiste satisfactoriamente el pedido del cliente?")
                        .setPositiveButton("Sí , ahora voy en camino al domicilio", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                updatePedido("En Camino");
                                findViewById(R.id.Recoger).setVisibility(View.GONE);
                                findViewById(R.id.qr).setVisibility(View.VISIBLE);
                                //Hay q quitar la ruta
                                pedidoSupreme.setEstado("En Camino");
                                navigateToPlace( mRoutingOptions);
                            }
                        })
                        .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Acción cuando se presiona "Cancelar" (cerrar el diálogo)
                                dialog.dismiss();
                            }
                        })
                        .setCancelable(true)
                        .show();
            });
            findViewById(R.id.qr).setOnClickListener(view -> {
                //Abrir camara para validar QR
                IntentIntegrator integrator =  new IntentIntegrator(this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                integrator.setPrompt("Escanea el QR del cliente");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();
//                updatePedido("Entregado");
//                findViewById(R.id.Recoger).setVisibility(View.GONE);
//                pedidoSupreme.setEstado("Entregado");
//                navigateToPlace( mRoutingOptions);
            });
            if(pedidoSupreme.getEstado().equals("Listo")){
                findViewById(R.id.qr).setVisibility(View.GONE);
            }else if(pedidoSupreme.getEstado().equals("En Camino")){
                findViewById(R.id.Recoger).setVisibility(View.GONE);
            }
            //Buscamos restaurante
            loadRestaturante(pedidoSupreme.getIdRestaurante(), () -> {
                if (!ola.equals("segundaVez")) {
                    getPlaceIdRestaurante(new Double(restauranteSupreme.getLatitud()), new Double(restauranteSupreme.getLongitud()), () -> {
                        getPlaceIdDestinoFinal(new Double(pedidoSupreme.getLatitud()), new Double(pedidoSupreme.getLongitud()), () -> {
                            //Con los IDs obtenidos ahora podemos marcar las rutas
                            initializeNavigationSdk();
                        });
                    });
                }
                ola = "segundaVez";
            });
        });
        listenerUpdate();
        actualiza();
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    public void verSiguienteTracking(View view) {
        Intent intent = new Intent(this, RepartidorTrackingFinalizado.class);
        startActivity(intent);
    }

    public void loadUser() {
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if (((document.toObject(Usuario.class)).getId()).equals(user.getUid())) {
                                usuario = document.toObject(Usuario.class);
                            }
                        }
                    }
                });
    }

    private void initializeNavigationSdk() {
        // Get a navigator.
        NavigationApi.getNavigator(
                this,
                new NavigationApi.NavigatorListener() {
                    /** Sets up the navigation UI when the navigator is ready for use. */
                    @Override
                    public void onNavigatorReady(Navigator navigator) {
                        displayMessage("Navigator ready.");
                        mNavigator = navigator;
                        mNavFragment =
                                (SupportNavigationFragment)
                                        getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);

                        if (ActivityCompat.checkSelfPermission(RepartidorTrackingEstadoEnCamino.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RepartidorTrackingEstadoEnCamino.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        mNavFragment.getMapAsync(
                                googleMap -> googleMap.followMyLocation(CameraPerspective.TILTED));

                        // Set the travel mode (DRIVING, WALKING, CYCLING, or TWO_WHEELER).
                        mRoutingOptions = new RoutingOptions();
                        mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);

                        // Navigate to a place, specified by Place ID.
                        navigateToPlace( mRoutingOptions);
                    }
                    @Override
                    public void onError(@NavigationApi.ErrorCode int errorCode) {
                        switch (errorCode) {
                            case NavigationApi.ErrorCode.NOT_AUTHORIZED:
                                displayMessage(
                                        "Error loading Navigation SDK: Your API key is "
                                                + "invalid or not authorized to use the Navigation SDK.");
                                break;
                            case NavigationApi.ErrorCode.TERMS_NOT_ACCEPTED:
                                displayMessage(
                                        "Error loading Navigation SDK: User did not accept "
                                                + "the Navigation Terms of Use.");
                                break;
                            case NavigationApi.ErrorCode.NETWORK_ERROR:
                                displayMessage("Error loading Navigation SDK: Network error.");
                                break;
                            case NavigationApi.ErrorCode.LOCATION_PERMISSION_MISSING:
                                displayMessage(
                                        "Error loading Navigation SDK: Location permission " + "is missing.");
                                break;
                            default:
                                displayMessage("Error loading Navigation SDK: " + errorCode);
                        }
                    }
                });
    }

    private void navigateToPlace(String placeId, RoutingOptions travelMode) {
        Waypoint destination;
        try {
            destination = Waypoint.builder().setPlaceIdString(placeId).build();
        } catch (Waypoint.UnsupportedPlaceIdException e) {
            displayMessage("Error starting navigation: Place ID is not supported.");
            return;
        }
        // Create a future to await the result of the asynchronous navigator task.
        ListenableResultFuture<Navigator.RouteStatus> pendingRoute =
                mNavigator.setDestination(destination, travelMode);

        // Define the action to perform when the SDK has determined the route.
        pendingRoute.setOnResultListener(
                new ListenableResultFuture.OnResultListener<Navigator.RouteStatus>() {
                    @Override
                    public void onResult(Navigator.RouteStatus code) {
                        switch (code) {
                            case OK:
                                // Hide the toolbar to maximize the navigation UI.
                                if (getActionBar() != null) {
                                    getActionBar().hide();
                                }

                                // Enable voice audio guidance (through the device speaker).
                                mNavigator.setAudioGuidance(Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE);

                                // Simulate vehicle progress along the route for demo/debug builds.
                                if (BuildConfig.DEBUG) {
                                    mNavigator
                                            .getSimulator()
                                            .simulateLocationsAlongExistingRoute(
                                                    new SimulationOptions().speedMultiplier(5));
                                }

                                // Start turn-by-turn guidance along the current route.
                                mNavigator.startGuidance();
                                break;
                            // Handle error conditions returned by the navigator.
                            case NO_ROUTE_FOUND:
                                displayMessage("Error starting navigation: No route found.");
                                break;
                            case NETWORK_ERROR:
                                displayMessage("Error starting navigation: Network error.");
                                break;
                            case ROUTE_CANCELED:
                                displayMessage("Error starting navigation: Route canceled.");
                                break;
                            default:
                                displayMessage("Error starting navigation: " + String.valueOf(code));
                        }
                    }
                });
    }

    private void navigateToPlace(RoutingOptions travelMode) {
        if(pedidoSupreme.getEstado().equals("Entregado")){
            findViewById(R.id.navigation_fragment).setVisibility(View.GONE);
        }
        // Set up a waypoint for each place that we want to go to.
        mWaypoints.clear();
        if(pedidoSupreme.getEstado().equals("Listo")){
            createWaypoint(idDestino, "Destino final");
            createWaypoint(placeIdRestaurante, "Restaurante");
        }else if(pedidoSupreme.getEstado().equals("En Camino")){
            createWaypoint(idDestino, "Destino final");
        }

        if (mSavedInstanceState != null
                && mSavedInstanceState.containsKey(KEY_JOURNEY_IN_PROGRESS)
                && mSavedInstanceState.getInt(KEY_JOURNEY_IN_PROGRESS) == 1) {
            return;
        }

        ListenableResultFuture<Navigator.RouteStatus> pendingRoute =
                mNavigator.setDestinations(mWaypoints,  travelMode);


        // Define the action to perform when the SDK has determined the route.
        pendingRoute.setOnResultListener(
                new ListenableResultFuture.OnResultListener<Navigator.RouteStatus>() {
                    @Override
                    public void onResult(Navigator.RouteStatus code) {
                        switch (code) {
                            case OK:
                                mJourneyInProgress = true;
                                // Hide the toolbar to maximize the navigation UI.
                                if (getActionBar() != null) {
                                    getActionBar().hide();
                                }


                                // Enable voice audio guidance (through the device speaker).
                                mNavigator.setAudioGuidance(
                                        Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE);
                                // Simulate vehicle progress along the route for demo/debug builds.
                                if (BuildConfig.DEBUG) {
                                    mNavigator.getSimulator().simulateLocationsAlongExistingRoute(
                                            new SimulationOptions().speedMultiplier(5));
                                }

                                // Start turn-by-turn guidance along the current route.
                                mNavigator.startGuidance();
                                break;
                            // Handle error conditions returned by the navigator.
                            case NO_ROUTE_FOUND:
                                displayMessage("Error starting navigation: No route found.");
                                break;
                            case NETWORK_ERROR:
                                displayMessage("Error starting navigation: Network error.");
                                break;
                            case ROUTE_CANCELED:
                                displayMessage("Error starting navigation: Route canceled.");
                                break;
                            default:
                                displayMessage("Error starting navigation: "
                                        + String.valueOf(code));
                        }
                    }
                });
    }

    private void createWaypoint(String placeId, String title) {
        try {
            mWaypoints.add(
                    Waypoint.builder()
                            .setPlaceIdString(placeId)
                            .setTitle(title)
                            .build()
            );
        } catch (Waypoint.UnsupportedPlaceIdException e) {
        }
    }


    private void displayMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG, errorMessage);
    }

    private void getPlaceIdRestaurante(Double latitude, Double longitude , Runnable runnable) {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "?latlng=" + latitude + "," + longitude + "&key=" + API_KEY;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error en la solicitud: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if ("OK".equals(json.getString("status"))) {
                            JSONArray results = json.getJSONArray("results");
                            if (results.length() > 0) {
                                JSONObject firstResult = results.getJSONObject(0);
                                placeIdRestaurante = firstResult.getString("place_id");
                                Log.i(TAG, "Place ID: " + placeIdRestaurante);
                                runnable.run();
                            } else {
                                Log.i(TAG, "No se encontraron resultados.");
                            }
                        } else {
                            Log.e(TAG, "Error en la API: " + json.getString("status"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar la respuesta JSON: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }
        });
    }

    private void getPlaceIdDestinoFinal(Double latitude, Double longitude , Runnable runnable) {
        OkHttpClient client = new OkHttpClient();
        String url = BASE_URL + "?latlng=" + latitude + "," + longitude + "&key=" + API_KEY;
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error en la solicitud: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject json = new JSONObject(responseBody);
                        if ("OK".equals(json.getString("status"))) {
                            JSONArray results = json.getJSONArray("results");
                            if (results.length() > 0) {
                                JSONObject firstResult = results.getJSONObject(0);
                                idDestino = firstResult.getString("place_id");
                                Log.i(TAG, "Place Destino ID: " + idDestino);
                                runnable.run();
                            } else {
                                Log.i(TAG, "No se encontraron resultados.");
                            }
                        } else {
                            Log.e(TAG, "Error en la API: " + json.getString("status"));
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error al procesar la respuesta JSON: " + e.getMessage());
                    }
                } else {
                    Log.e(TAG, "Error HTTP: " + response.code());
                }
            }
        });
    }


    public void loadPedido(Runnable runnable) {
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if (((document.toObject(Pedido.class)).getId()).equals(getIntent().getStringExtra("idPedido"))) {
                                pedidoSupreme = document.toObject(Pedido.class);
                            }
                        }
                        runnable.run();
                    }
                });
    }

    public void loadRestaturante(String idRestaurante, Runnable runnable) {
        db.collection("restaurantes")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if (((document.toObject(Restaurante.class)).getId()).equals(idRestaurante)) {
                                restauranteSupreme = document.toObject(Restaurante.class);
                            }
                        }
                        runnable.run();
                    }
                });
    }

    //Capta cambios
    public void listenerUpdate() {
        DocumentReference docRef = db.collection("Pedidos").document(getIntent().getStringExtra("idPedido"));
        // Agrega un listener al documento
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento se actualizó
                Pedido pedidoUpdate = documentSnapshot.toObject(Pedido.class);
                pedidoSupreme = pedidoUpdate;
                ((TextView) findViewById(R.id.estado)).setText(pedidoUpdate.getEstado());
            } else {
                // El documento no existe o fue eliminado
                Log.d("Firestore", "El documento no existe");
            }
        });
    }

    //PARA ACTUALIZAR LOS DATOS DE POSICIÓN EN FIREBASE
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    @SuppressLint("MissingPermission")
    public void actualiza(){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // Configurar la solicitud de ubicación
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000); // Cada 10 segundos
        locationRequest.setFastestInterval(5000*3); // Máximo cada 5 segundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Definir el callback para recibir las actualizaciones
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) return;
                for (Location location : locationResult.getLocations()) {
                    // Enviar la ubicación a Firebase
                    updateLocationToFirebase(location);
                }
            }
        };
        // Iniciar las actualizaciones de ubicación
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }
    private void updateLocationToFirebase(Location location) {
        if(pedidoSupreme  != null && !pedidoSupreme.getEstado().equals("Entregado")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            // Crear un mapa con los datos de ubicación
            Map<String, Object> locationData = new HashMap<>();
            locationData.put("latitudActualRepartidor",""+location.getLatitude());
            locationData.put("longitudActualRepartidor", ""+location.getLongitude());
            // Actualizar los datos en Firestore
            db.collection("Pedidos").document(pedidoSupreme.getId())
                    .update(locationData)
                    .addOnSuccessListener(aVoid -> Log.d("Firestore", "Ubicación actualizada con éxito"))
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar la ubicación", e));
        }
    }

    //PERMISO
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is canceled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    public void updatePedido(String nuevoEstado) {
        // Obtén la referencia a la ubicación de la base de datos de Firebase
        // Actualiza los datos en la ubicación especificada
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Crear un mapa con los datos de ubicación
        Map<String, Object> locationData = new HashMap<>();
        locationData.put("estado",nuevoEstado);
        // Actualizar los datos en Firestore
        db.collection("Pedidos").document(pedidoSupreme.getId())
                .update(locationData)
                .addOnSuccessListener(aVoid -> {
                    Log.d("Firestore", "Ubicación actualizada con éxito");
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar la ubicación", e));
    }




    @Override
    protected void onActivityResult(int requestCode , int resultCode ,@Nullable Intent data){
        IntentResult result =  IntentIntegrator.parseActivityResult(requestCode , resultCode , data);
        if(result != null){
            if(result.getContents() == null){
                Toast.makeText(this, "Cancelado" , Toast.LENGTH_LONG).show();
                super.onActivityResult(requestCode , resultCode , data);
            }else{
                Toast.makeText(this, "Escaneado" + result.getContents() , Toast.LENGTH_LONG).show();
                if(pedidoSupreme.getId().equals(result.getContents())){
                    Intent intent =  new Intent(this , RepartidorTrackingFinalizado.class);
                    updatePedido("Entregado");
                    LogSuper logSuper = new LogSuper();
                    logSuper.setFecha(Timestamp.now());
                    logSuper.setTipo("Pedido");
                    logSuper.setIdImage(pedidoSupreme.getIdUsuario());
                    db.collection("restaurantes").document(pedidoSupreme.getIdRestaurante()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Restaurante restaurante = documentSnapshot.toObject(Restaurante.class);
                                    logSuper.setInfo("El cliente "+usuario.getNombre() + usuario.getApellido() +"ha recibido su pedido del restaurante "+restaurante.getNombre());
                                    db.collection("Logs").add(logSuper)
                                            .addOnSuccessListener(aVoid -> {
                                                Toast.makeText(this, "Pedido realizado exitosamente", Toast.LENGTH_SHORT).show();

                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al realizar el pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });

                                }
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                    intent.putExtra("idPedido" , result.getContents());
                    startActivity(intent);
                    finish();
                }else{
                    super.onActivityResult(requestCode , resultCode , data);
                }

            }
        }else{
            super.onActivityResult(requestCode , resultCode , data);
        }
    }

}