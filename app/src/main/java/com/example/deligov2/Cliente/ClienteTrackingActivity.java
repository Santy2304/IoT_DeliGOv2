package com.example.deligov2.Cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClienteDetalleCompraAdapter;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.NotiActivity;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.ProcesosTracking.RepartidorTrackingEstadoEnCamino;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.libraries.navigation.ListenableResultFuture;
import com.google.android.libraries.navigation.NavigationApi;
import com.google.android.libraries.navigation.Navigator;
import com.google.android.libraries.navigation.RoadSnappedLocationProvider;
import com.google.android.libraries.navigation.RoutingOptions;
import com.google.android.libraries.navigation.SimulationOptions;
import com.google.android.libraries.navigation.SupportNavigationFragment;
import com.google.android.libraries.navigation.Waypoint;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.common.util.concurrent.Futures;
import com.google.firebase.BuildConfig;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.List;
import java.util.concurrent.Executor;

public class ClienteTrackingActivity extends AppCompatActivity {

    private static final String TAG = RepartidorTrackingEstadoEnCamino.class.getSimpleName();
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCZ6AXAM3lT9VfbdDfNTSxtk-cXe0n_nDA";

    private Navigator mNavigator;
    private SupportNavigationFragment mNavFragment;
    private RoutingOptions mRoutingOptions;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private final List<Waypoint> mWaypoints = new ArrayList<>();

    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String channelId = "noti";
    Button repartidorButton;
    TextView recibidoText;
    TextView preparacionText;
    TextView listoText;
    TextView caminoText, costoTotal,dateText;
    TextView entregadoTexT;
    Button qrButton;
    Slider slider;
    FloatingActionButton backButton;
    ArrayList<Platillo> lista = new ArrayList<>();
    ArrayList<String> idsPlatos = new ArrayList<>();
    ArrayList<Integer> cantidades = new ArrayList<>();
    ArrayList<Float> listaPrecios = new ArrayList<>();
    ClienteDetalleCompraAdapter adapter;
    String idPedido;
    Pedido pedido;


    private static final String DISPLAY_BOTH = "both";
    private static final String DISPLAY_TOAST = "toast";
    private static final String DISPLAY_LOG = "log";

    private RoadSnappedLocationProvider mRoadSnappedLocationProvider;


    private Navigator.ArrivalListener mArrivalListener;
    private Navigator.RouteChangedListener mRouteChangedListener;
    private Navigator.RemainingTimeOrDistanceChangedListener mRemainingTimeOrDistanceChangedListener;
    private RoadSnappedLocationProvider.LocationListener mLocationListener;

    private Bundle mSavedInstanceState;
    private static final String KEY_JOURNEY_IN_PROGRESS = "journey_in_progress";
    private boolean mJourneyInProgress = false;

    // Set fields for requesting location permission.



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_tracking);
        //FIREBASE
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
//        crearCanalNotificacion();
        slider = findViewById(R.id.slider);
        recibidoText = findViewById(R.id.recibido);
        preparacionText = findViewById(R.id.preparacion);
        listoText = findViewById(R.id.listo);
        caminoText = findViewById(R.id.camino);
        entregadoTexT = findViewById(R.id.entregado);
        idPedido = getIntent().getStringExtra("idOrder");
        costoTotal = findViewById(R.id.costoTotal);
        dateText = findViewById(R.id.hourText);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                String title;
                String content;

                if(value < 50 && value >= 35){
                    title = "Tu pedido está en preparación";
                    content = "Los cocineros ya leyeron tu pedido y lo están cocinando";
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(getResources().getColor(R.color.blue));
//                    lanzarNotificacion(title, content);
                } else if (value < 64 && value >= 52) {
                    title = "Tu pedido está listo";
                    recibidoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
                    content = "El pedido ya está cocinado y a la espera de un repartidor que lo recoja.";
                    listoText.setTextColor(getResources().getColor(R.color.blue));
//                    lanzarNotificacion(title, content);
                } else if (value < 85 && value >= 70) {
                    title = "Tu pedido está en camino";
                    content = "El repartidor ha tomado tu pedido y está en camino al destino.";
                    caminoText.setTextColor(getResources().getColor(R.color.blue));
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
//                    lanzarNotificacion(title, content);
                } else if (value == 100) {
                    title = "Tu pedido ha llegado";
                    content = "El repartidor ha llegado al destino.";
                    entregadoTexT.setTextColor(getResources().getColor(R.color.blue));
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
//                    lanzarNotificacion(title, content);
                }
            }
        });
        db.collection("Pedidos").document(idPedido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedido = documentSnapshot.toObject(Pedido.class);
                        idsPlatos = pedido.getIdListaPlatos();
                        cantidades = pedido.getListaCantidades();
                        listaPrecios = pedido.getPreciosActuales();
                        db.collection("Platos").addSnapshotListener((snapshot, error)->{
                            if (error != null) {
                                Log.w("msg-test", "Listen failed.", error);
                                return;
                            }
                            if (snapshot != null && !snapshot.isEmpty()) {
                                lista.clear();
                                for (DocumentSnapshot document : snapshot.getDocuments()) {
                                    Platillo platillo = document.toObject(Platillo.class);
                                    Log.w("msg-test", "Listen failed "+ document.getId());
                                    if (idsPlatos.contains(platillo.getId())){
                                        lista.add(platillo);
                                    }
                                }

                                float montoTotal = pedido.getCostoEnvio();
                                for (int i = 0; i < lista.size(); i++) {
                                    montoTotal += listaPrecios.get(i) * cantidades.get(i);
                                }
                                costoTotal.setText(String.format("S/ %.2f",montoTotal));
                                Timestamp timestamp = pedido.getHora();
                                Date date = timestamp.toDate();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                String fechaFormateada = dateFormat.format(date);
                                dateText.setText(fechaFormateada);
                                adapter = new ClienteDetalleCompraAdapter();
                                adapter.setContext(this);
                                adapter.setListafood(lista);
                                adapter.setListaCantidades(cantidades);
                                adapter.setListaPrecios(listaPrecios);

                                RecyclerView recyclerView = findViewById(R.id.recycler);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ClienteTrackingActivity.this));
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));
        repartidorButton = findViewById(R.id.repartidorButton);
        qrButton = findViewById(R.id.qrButton);
        backButton = findViewById(R.id.atrasTracking);
        repartidorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteVeRepartidor.class);
            startActivity(intent);
        });
        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteQR.class);
            startActivity(intent);
        });
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHistorialActivity.class);
            startActivity(intent);
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteTrackingActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteTrackingActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteTrackingActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
        ubicacionUpdateListener();

    }

//    public void crearCanalNotificacion(){
//
//        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(channelId,
//                    "Canal notificaciones default",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//            channel.setDescription("Canal para notificaciones con prioridad default");
//            channel.enableVibration(true);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//
//            askPermission();
//
//        }
//    }
//    public void askPermission(){
//        //android.os.Build.VERSION_CODES.TIRAMISU == 33
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
//                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
//                        PackageManager.PERMISSION_DENIED) {
//
//            ActivityCompat.requestPermissions(ClienteTrackingActivity.this,
//                    new String[]{POST_NOTIFICATIONS},
//                    101);
//        }
//
//    }
//    public void lanzarNotificacion(String title,String texto) {
//        Intent intent = new Intent(this, ClienteTrackingActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
//                .setSmallIcon(R.drawable.deligo)
//                .setContentTitle(title)
//                .setContentText(texto)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//
//        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
//            notificationManager.notify(1, builder.build());
//        }
//    }

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

    Location dummyLocation = new Location("Repartidor");

    public void ubicacionUpdateListener(){
        String id   = getIntent().getStringExtra("idOrder");
        DocumentReference docRef = db.collection("Pedidos").document(id);
        // Agrega un listener al documento
        docRef.addSnapshotListener((documentSnapshot, e) -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                // El documento se actualizó
                Pedido pedidoUpdate = documentSnapshot.toObject(Pedido.class);
                if(pedidoUpdate.getLatitudActualRepartidor() != null){
                    //Actualizar mapa
                    Double latitud =  new Double(pedidoUpdate.getLatitudActualRepartidor());
                    Double longitud = new Double(pedidoUpdate.getLongitudActualRepartidor());
                    getPlaceId(latitud , longitud);
                }
            } else {
                // El documento no existe o fue eliminado
                Log.d("Firestore", "El documento no existe");
            }
        });
    }

    private String placeIdRepartidor = "";
    //Te devuelve el id de la ubicación q quieras de forma asincrona
    private void getPlaceId(Double latitude, Double longitude ) {
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
                                placeIdRepartidor = firstResult.getString("place_id");
                                Log.i(TAG, "Place ID: " + placeIdRepartidor);
                                //Buscamos al restaurante

                                getPlaceIdRestaurante(new Double(pedido.getLatitud()), new Double(pedido.getLongitud()));
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
    private String placeIdRestaurante = "";
    private void getPlaceIdRestaurante(Double latitude, Double longitude ) {
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
                                Log.i(TAG, "Place ID: " + placeIdRepartidor);
                                loadRestaurante(()->{
                                    getPlaceIdDestino(new Double(restauranteSupreme.getLatitud()), new Double(restauranteSupreme.getLongitud()));
                                });
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
    private String idDestino = "";
    private void getPlaceIdDestino (Double latitude, Double longitude ) {
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
                                Log.i(TAG, "Place ID: " + placeIdRepartidor);
                                initializeNavigationSdk();
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

    private void displayMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG, errorMessage);
    }

    private void initializeNavigationSdk( ) {

        //PERMISOS
        if (ContextCompat.checkSelfPermission(
                this.getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        //PERMISOS
        if (!mLocationPermissionGranted) {
            displayMessage(
                    "Error loading Navigation SDK: " + "The user has not granted location permission.");
            return;
        }
        //AHORA SÍ
        NavigationApi.getNavigator(
                this,
                new NavigationApi.NavigatorListener() {
                    @Override
                    public void onNavigatorReady(Navigator navigator) {
                        displayMessage("Navigator ready.");
                        mNavigator = navigator;
                        mNavFragment =
                                (SupportNavigationFragment)
                                        getSupportFragmentManager().findFragmentById(R.id.navigation_fragment);

                        mRoutingOptions = new RoutingOptions();
                        mRoutingOptions.travelMode(RoutingOptions.TravelMode.TAXI);
                        //mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);
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
    //idDestino
    //placeIdRestaurante
    //placeIdRepartidor
    private void navigateToPlace( RoutingOptions travelMode) {

        // Set up a waypoint for each place that we want to go to.
        createWaypoint(idDestino, "Destino final");
        createWaypoint(placeIdRestaurante, "Restaurante");
        createWaypoint(placeIdRepartidor, "Repartidor");

        // If this journey is already in progress, no need to restart navigation.
        // This can happen when the user rotates the device, or sends the app to the background.
        if (mSavedInstanceState != null
                && mSavedInstanceState.containsKey(KEY_JOURNEY_IN_PROGRESS)
                && mSavedInstanceState.getInt(KEY_JOURNEY_IN_PROGRESS) == 1) {
            return;
        }

        // Create a future to await the result of the asynchronous navigator task.
        ListenableResultFuture<Navigator.RouteStatus> pendingRoute =
                mNavigator.setDestinations(mWaypoints);

        Location dummyLocation = new Location("dummyProvider");
        dummyLocation.setLatitude(new Double(pedido.getLatitudActualRepartidor()));
        dummyLocation.setLongitude(new  Double (pedido.getLongitudActualRepartidor()));
        RoadSnappedLocationProvider roadSnappedLocationProvider = new RoadSnappedLocationProvider() {
            @Override
            public void addLocationListener(LocationListener locationListener) {

            }

            @Override
            public void removeLocationListener(LocationListener locationListener) {

            }

            @Override
            public void resetFreeNav() {

            }
        };

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

                                // Register some listeners for navigation events.
                                //registerNavigationListeners();


                                // Enable voice audio guidance (through the device speaker).
                                mNavigator.setAudioGuidance(
                                        Navigator.AudioGuidance.VOICE_ALERTS_AND_GUIDANCE);
                                // Simulate vehicle progress along the route for demo/debug builds.
                                if (BuildConfig.DEBUG) {
                                    mNavigator.getSimulator().simulateLocationsAlongExistingRoute(
                                            new SimulationOptions().speedMultiplier(5));
                                }

                                // Start turn-by-turn guidance along the current route.
                                //mNavigator.startGuidance();
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
//Tres ubicaciones
    //Repartidor
    //Restaurante
    //Destino

    private Restaurante restauranteSupreme = new Restaurante();
    public void loadRestaurante(Runnable runnable){
        db.collection("restaurantes")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Restaurante.class)).getId()).equals(pedido.getIdRestaurante())){
                                restauranteSupreme = document.toObject(Restaurante.class);
                                runnable.run();
                            }
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

}