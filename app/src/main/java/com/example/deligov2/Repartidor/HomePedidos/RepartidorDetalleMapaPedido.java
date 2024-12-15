package com.example.deligov2.Repartidor.HomePedidos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.DTO.Pedido;

import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RepartidorDetalleMapaPedido extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    GoogleMap mMap;
    SupportMapFragment mapFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Seteamos los valores de firebase
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Lanzamos el view
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_detalle_mapa_pedido);
        //Recogemos los valores del anterior activity



        loadUser(()->{
            //Buscamos el idDelPedido
            loadPedidos(getIntent().getStringExtra("pedido") , ()->{
                if(isValido){
                    TextView title = findViewById(R.id.title);
                    ( findViewById(R.id.btn_aceptar) ).setContentDescription(pedidoSupreme.getId());
                    title.setText("Mapa de pedido #" +  pedidoSupreme.getId());
                    TextView destinoTienda = findViewById(R.id.destinoTienda);
                    destinoTienda.setText( pedidoSupreme.getDireccion());
                    TextView destinoFinal = findViewById(R.id.destinoFinal);
                    destinoFinal.setText( pedidoSupreme.getDireccion());
                    try {
                        if (getIntent().getStringExtra("flag").equals("historial")) {
                            //ocultamos el boton
                            findViewById(R.id.btn_aceptar).setVisibility(View.INVISIBLE);
                            findViewById(R.id.btn_aceptar).setClickable(false);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                    //Seteamos el mapa
                    mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRestaurant);
                    mapFragment.getMapAsync(RepartidorDetalleMapaPedido.this);
                    //
                }
            });
        });
    }
    public void retroceder(View view){
        Intent intent = new Intent(this, RepartidorVistaHome.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
    private ListenerRegistration listenerRegistrationUser;
    private ListenerRegistration listenerRegistrationPedido;

    public void loadUser(Runnable run){
         db.collection("Usuarios")
                 .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                if(isValido){
                                    run.run();

                                }
                            }
                        }
                    }

                });
    }
    private Pedido pedidoSupreme =  new Pedido();
    public void loadPedidos(String idPedido, Runnable run){
        db.collection("Pedidos")
                .get()
                .addOnCompleteListener(task-> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(idPedido)){
                                pedidoSupreme =  pedido;
                                if(isValido){
                                    run.run();
                                }
                            }
                        }
                    }
                });
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap){
        mMap = googleMap;
        String resUbi = pedidoSupreme.getDireccion();
        LatLng ubicacion = new LatLng(new Double(pedidoSupreme.getLatitud()) , new Double(pedidoSupreme.getLongitud()));
        if (ubicacion != null) {
            Bitmap resizedBitmap = resizeBitmap(R.drawable.metaaa, 100, 100); // Ajusta el tamaño deseado
            mMap.addMarker(new MarkerOptions().position(ubicacion).title("Destino").icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
            ((TextView)findViewById(R.id.destinoFinal)).setText(obtenerDireccionDesdeLatLng(ubicacion));
            loadRestaurante(()->{
                mMap.addMarker(new MarkerOptions().position( new LatLng(new Double(restauranteSupreme.getLatitud()) , new Double(restauranteSupreme.getLongitud()))).title("Restaurante").icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(R.drawable.restaaaa, 100, 100))));
                LatLng ola = new LatLng(new Double(restauranteSupreme.getLatitud()) , new Double(restauranteSupreme.getLongitud()));
                ((TextView)findViewById(R.id.destinoTienda)).setText(obtenerDireccionDesdeLatLng(ola));
            });
        }
    }
    public void aceptar(String idPedido ,  String idRepartidor , Runnable onsuccess  , Runnable onfailure){
        Map<String, Object> updates = new HashMap<>();
        updates.put("idRepartidor", idRepartidor);

        // Realizar el update
        db.collection("Pedidos")
                .document(idPedido)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Éxito
                    onsuccess.run();
                })
                .addOnFailureListener(e -> {
                    // Error
                    onfailure.run();});
    }
    public void aceptarPedido(View view ){

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Qué acción deseas realizar con esta solicitud?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aceptar(getIntent().getStringExtra("pedido") , user.getUid() , ()->{
                                    Intent intent = new Intent(RepartidorDetalleMapaPedido.this, RepartidorAceptacionPedido.class);
                                    intent.putExtra("idPedido", getIntent().getStringExtra("pedido"));
                            getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
                            startActivity(intent);
                                    finish();
                                    }, ()->{
                                    Intent intent = new Intent(RepartidorDetalleMapaPedido.this, RepartidorCancelacionPedido.class);
                                    getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();

                                    startActivity(intent);
                                    finish();
                        });
                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción cuando se presiona "Cancelar" (cerrar el diálogo)
                        dialog.dismiss();
                    }
                })
                .show();
    }
    private Bitmap resizeBitmap(int resourceId, int width, int height) {
        // Cargar el recurso como un Bitmap
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), resourceId);

        // Redimensionar el Bitmap
        return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
    }
private Restaurante restauranteSupreme;
    public void loadRestaurante(Runnable runnable){
        db.collection("restaurantes")
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(((document.toObject(Restaurante.class)).getId()).equals(pedidoSupreme.getIdRestaurante())){
                                restauranteSupreme = document.toObject(Restaurante.class);
                                runnable.run();
                            }
                        }
                    }
                });
    }


    private String obtenerDireccionDesdeLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                return direccion.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("GeocoderError", "No se pudo obtener la dirección", e);
        }
        return null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Elimina el listener de Firebase al destruir la actividad
        if (listenerRegistrationUser != null) {
            listenerRegistrationUser.remove();
        }
        if (listenerRegistrationPedido != null) {
            listenerRegistrationPedido.remove();
        }
        if (mapFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
    }
    private Boolean isValido= false;
    @Override
    protected void onResume(){
        super.onResume();
        isValido =  true;
    }
    @Override
    protected void onPause() {
        super.onPause();
        // Elimina el listener de Firebase al destruir la actividad
        if (listenerRegistrationUser != null) {
            listenerRegistrationUser.remove();
        }
        if (listenerRegistrationPedido != null) {
            listenerRegistrationPedido.remove();
        }
        if (mapFragment != null) {
            getSupportFragmentManager().beginTransaction().remove(mapFragment).commitAllowingStateLoss();
        }
        isValido =  false;
    }

}