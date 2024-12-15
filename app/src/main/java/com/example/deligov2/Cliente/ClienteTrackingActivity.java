package com.example.deligov2.Cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.List;
import java.util.concurrent.Executor;
import androidx.fragment.app.FragmentActivity;

public class ClienteTrackingActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private static final String TAG = RepartidorTrackingEstadoEnCamino.class.getSimpleName();
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
    // Set fields for requesting location permission.
    private Restaurante restauranteSupreme = new Restaurante();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_tracking);
        //FIREBASE
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        slider = findViewById(R.id.slider);
        recibidoText = findViewById(R.id.recibido);
        preparacionText = findViewById(R.id.preparacion);
        listoText = findViewById(R.id.listo);
        caminoText = findViewById(R.id.camino);
        entregadoTexT = findViewById(R.id.entregado);
        idPedido = getIntent().getStringExtra("idOrder");
        costoTotal = findViewById(R.id.costoTotal);
        dateText = findViewById(R.id.hourText);

        db.collection("Pedidos").document(idPedido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedido = documentSnapshot.toObject(Pedido.class);
                        idsPlatos = pedido.getIdListaPlatos();
                        actualizarSliderSegunEstado(pedido.getEstado());
                        if(pedido.getIdRepartidor() == null ){
                            findViewById(R.id.esperando).setVisibility(View.VISIBLE);
                            findViewById(R.id.map).setVisibility(View.GONE);
                        }
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

        escucharCambiosEstadoPedido();
        repartidorButton = findViewById(R.id.repartidorButton);
        qrButton = findViewById(R.id.qrButton);
        backButton = findViewById(R.id.atrasTracking);
        repartidorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteVeRepartidor.class);
            intent.putExtra("idOrder",getIntent().getStringExtra("idOrder"));
            startActivity(intent);
        });
        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteQR.class);
            intent.putExtra("idOrder",getIntent().getStringExtra("idOrder"));
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

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ubicacionUpdateListener();
    }
    private void actualizarSliderSegunEstado(String estado) {
        if (estado == null || estado.isEmpty()) return;
        switch (estado) {
            case "Recibido":
                slider.setValue(0);
                actualizarColorTexto(recibidoText);
                break;
            case "En Preparación":
                slider.setValue(35);
                actualizarColorTexto(preparacionText);
                break;
            case "Listo":
                slider.setValue(52);
                actualizarColorTexto(listoText);
                break;
            case "En Camino":
                slider.setValue(70);
                actualizarColorTexto(caminoText);
                break;
            case "Entregado":
                slider.setValue(100);
                actualizarColorTexto(entregadoTexT);
                break;
            default:
                Log.w("ClienteTrackingActivity", "Estado desconocido: " + estado);
        }
    }

    private void actualizarColorTexto(TextView textViewActivo) {
        recibidoText.setTextColor(getResources().getColor(R.color.black));
        preparacionText.setTextColor(getResources().getColor(R.color.black));
        listoText.setTextColor(getResources().getColor(R.color.black));
        caminoText.setTextColor(getResources().getColor(R.color.black));
        entregadoTexT.setTextColor(getResources().getColor(R.color.black));
        textViewActivo.setTextColor(getResources().getColor(R.color.blue));
    }

    private void escucharCambiosEstadoPedido() {
        db.collection("Pedidos").document(idPedido)
                .addSnapshotListener((documentSnapshot, error) -> {
                    if (error != null) {
                        Log.w(TAG, "Error al escuchar cambios: ", error);
                        return;
                    }
                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        Pedido pedidoActualizado = documentSnapshot.toObject(Pedido.class);
                        if (pedidoActualizado != null) {
                            actualizarSliderSegunEstado(pedidoActualizado.getEstado());
                        }
                    }
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
                    if(marker2 != null){
                        marker2.remove();
                        Double latitud =  new Double(pedidoUpdate.getLatitudActualRepartidor());
                        Double longitud = new Double(pedidoUpdate.getLongitudActualRepartidor());
                        LatLng repartidorLocation = new LatLng(latitud, longitud);
                        Bitmap resizedBitmap = resizeBitmap(R.drawable.reparr, 100, 100); // Ajusta el tamaño deseado
                        marker2 = mMap.addMarker(new MarkerOptions().position(repartidorLocation).title("Posicion Repartidor").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(repartidorLocation, 13));
                    }
                }
            } else {
                // El documento no existe o fue eliminado
                Log.d("Firestore", "El documento no existe");
            }
        });
    }

    public void loadRestaurante(Runnable runnable){
        db.collection("restaurantes")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Restaurante.class)).getId()).equals(pedidoSupreme.getIdRestaurante())){
                                restauranteSupreme = document.toObject(Restaurante.class);
                                runnable.run();
                            }
                        }
                    }
                });
    }
    private Pedido pedidoSupreme;
    public void loadPedido(Runnable runnable){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Pedido.class)).getId()).equals(getIntent().getStringExtra("idOrder"))){
                                pedidoSupreme = document.toObject(Pedido.class);
                                if(pedidoSupreme.getEstado().equals("En Camino")){
                                    marker1.remove();
                                }
                                if(pedidoSupreme.getEstado().equals("Entregado")){
                                    Toast.makeText(this, "Pedido entregado", Toast.LENGTH_SHORT);
                                    Intent intent = new Intent(this , ClienteHomeActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                if(pedidoSupreme.getIdRepartidor() != null){
                                    findViewById(R.id.esperando).setVisibility(View.GONE);
                                    findViewById(R.id.map).setVisibility(View.VISIBLE);
                                    findViewById(R.id.repartidorButton).setVisibility(View.VISIBLE);
                                    runnable.run();
                                }
                            }
                        }
                    }
                });
    }
    HashMap<String, Marker> markers = new HashMap<>();
    Integer counter = 0 ;
    Marker marker1;
    Marker marker2;
    Marker marker3;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Agregar un marcador en una ubicación y mover la cámara
        loadPedido(()->{
            loadRestaurante(()->{if(counter == 0 ){
                LatLng restauranteLocation = new LatLng(new Double(restauranteSupreme.getLatitud()), new Double(restauranteSupreme.getLongitud())); // Coordenadas de Sídney
                LatLng RepartidorLocation = new LatLng(new Double(pedidoSupreme.getLatitudActualRepartidor()), new Double(pedidoSupreme.getLongitudActualRepartidor())); // Coordenadas de Sídney
                LatLng DestinoLocation = new LatLng(new Double(pedidoSupreme.getLatitud()), new Double(pedidoSupreme.getLongitud())); // Coordenadas de Sídney
                Log.d("Locations" ,""+ restauranteLocation.latitude );
                Log.d("Locations" ,""+ RepartidorLocation.latitude );
                Log.d("Locations" ,""+ DestinoLocation.latitude );
                Bitmap resizedBitmap = resizeBitmap(R.drawable.restaaaa, 100, 100); // Ajusta el tamaño deseado
                marker1 =mMap.addMarker(new MarkerOptions().position(restauranteLocation).title("Restaurante").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                resizedBitmap = resizeBitmap(R.drawable.reparr, 100, 100); // Ajusta el tamaño deseado
                marker2 = mMap.addMarker(new MarkerOptions().position(RepartidorLocation).title("Posicion Repartidor").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                resizedBitmap = resizeBitmap(R.drawable.metaaa, 100, 100); // Ajusta el tamaño deseado
                marker3 =mMap.addMarker(new MarkerOptions().position(DestinoLocation).title("Destino final").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(restauranteLocation, 13));
                counter++;
            }
            });
        });
    }
    private Bitmap resizeBitmap(int resourceId, int width, int height) {
        // Cargar el recurso como un Bitmap
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resourceId);

        // Redimensionar el Bitmap
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
    }
}