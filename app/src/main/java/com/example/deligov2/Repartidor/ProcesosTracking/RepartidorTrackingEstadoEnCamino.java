package com.example.deligov2.Repartidor.ProcesosTracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.BuildConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class RepartidorTrackingEstadoEnCamino extends AppCompatActivity {

    private static final String TAG = RepartidorTrackingEstadoEnCamino.class.getSimpleName();
    private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
    private static final String API_KEY = "AIzaSyCZ6AXAM3lT9VfbdDfNTSxtk-cXe0n_nDA";
    private Navigator mNavigator;
    private SupportNavigationFragment mNavFragment;
    private RoutingOptions mRoutingOptions;
    // Define the Sydney Opera House by specifying its place ID.
    // Set fields for requesting location permission.
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_tracking_estado_en_camino);

        loadUser();
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
        String idAddres = "ChIJkzeqdLbIBZERzolWqH64Kc4";
        Double latitud =  -12.046374 ;
        Double longitud =  -77.0427934 ;
        getPlaceId(latitud,longitud );
    }

    public void verSiguienteTracking(View view) {
        Intent intent = new Intent(this, RepartidorTrackingFinalizado.class);
        startActivity(intent);
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
    /**
     * Starts the Navigation SDK and sets the camera to follow the device's location. Calls the
     * navigateToPlace() method when the navigator is ready.
     */
    private void initializeNavigationSdk(String id ) {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
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

        if (!mLocationPermissionGranted) {
            displayMessage(
                    "Error loading Navigation SDK: " + "The user has not granted location permission.");
            return;
        }

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

                        // Set the last digit of the car's license plate to get route restrictions
                        // in supported countries. (optional)
                        // mNavigator.setLicensePlateRestrictionInfo(getLastDigit(), "BZ");

                        // Set the camera to follow the device location with 'TILTED' driving view.
                        mNavFragment.getMapAsync(
                                googleMap -> googleMap.followMyLocation(CameraPerspective.TILTED));

                        // Set the travel mode (DRIVING, WALKING, CYCLING, or TWO_WHEELER).
                        mRoutingOptions = new RoutingOptions();
                        mRoutingOptions.travelMode(RoutingOptions.TravelMode.DRIVING);

                        // Navigate to a place, specified by Place ID.
                        navigateToPlace(id, mRoutingOptions);
                    }

                    /**
                     * Handles errors from the Navigation SDK.
                     *
                     * @param errorCode The error code returned by the navigator.
                     */
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
    /**
     * Requests directions from the user's current location to a specific place (provided by the
     * Google Places API).
     */
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

    /** Handles the result of the request for location permissions. */
    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
            {
                // If request is canceled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }

    /**
     * Shows a message on screen and in the log. Used when something goes wrong.
     *
     * @param errorMessage The message to display.
     */
    private void displayMessage(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        Log.d(TAG, errorMessage);
    }
    private String placeId = "";
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
                                placeId = firstResult.getString("place_id");
                                Log.i(TAG, "Place ID: " + placeId);
                                initializeNavigationSdk(placeId);
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

}