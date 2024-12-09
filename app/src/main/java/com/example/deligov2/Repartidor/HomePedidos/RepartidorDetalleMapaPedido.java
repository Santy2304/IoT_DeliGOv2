package com.example.deligov2.Repartidor.HomePedidos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.DTO.Pedido;

import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RepartidorDetalleMapaPedido extends AppCompatActivity implements OnMapReadyCallback {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Seteamos los valores de firebase
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Lanzamos el view
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_detalle_mapa_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Recogemos los valores del anterior activity
        loadUser(()->{
            //Buscamos el idDelPedido
            loadPedidos(getIntent().getStringExtra("pedido") , ()->{
                TextView title = findViewById(R.id.title);
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
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapRestaurant);
                mapFragment.getMapAsync(this);


                //

            });
        });
    }
    public void retroceder(View view){
        onBackPressed();
    }

    public void aceptacionRepartidor2(View view ) {
        Intent intent = new Intent(this, RepartidorAceptacionPedido.class);
        startActivity(intent);
    }

    public void loadUser(Runnable run){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                run.run();
                            }
                        }
                    }
                });
    }
    private Pedido pedidoSupreme =  new Pedido();
    public void loadPedidos(String idPedido, Runnable run){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(idPedido)){
                                pedidoSupreme =  pedido;
                                run.run();
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
            mMap.addMarker(new MarkerOptions().position(ubicacion).title(resUbi));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 15));
        }
    }
}