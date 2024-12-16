package com.example.deligov2.Repartidor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.RepartidorHistorialPedidosAdapter;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorDetalleCompraDelivery;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorDetalleMapaPedido;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorVistaHome;
import com.example.deligov2.Repartidor.ProcesosTracking.RepartidorTrackingEstadoEnCamino;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class RepartidorHistorial extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    private ArrayList<Pedido> listaPedidos =  new ArrayList<Pedido>();

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_historial);
        ShapeableImageView image = findViewById(R.id.imageView);
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

        db.collection("Pedidos")
                .get()
                .addOnCompleteListener((task) -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido ola = document.toObject(Pedido.class);
                            if( ola.getIdRepartidor()!=null && ola.getIdRepartidor().equals(user.getUid()) ){
                                listaPedidos.add(document.toObject(Pedido.class));
                            }
                        }
                        RepartidorHistorialPedidosAdapter adapter = new RepartidorHistorialPedidosAdapter();
                        adapter.setContext(this);
                        Collections.reverse(listaPedidos);
                        adapter.setLista(listaPedidos);
                        RecyclerView recyclerView = findViewById(R.id.lista);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(this));
                    }
                });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.historial);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ordenes) {
                    Intent intentRestaurant = new Intent(RepartidorHistorial.this, RepartidorVistaHome.class);
                    startActivity(intentRestaurant);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (item.getItemId() == R.id.historial) {
                    Intent intentPrincipal = new Intent(RepartidorHistorial.this, RepartidorHistorial.class);
                    startActivity(intentPrincipal);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (item.getItemId() == R.id.perfil) {
                    Intent intentProfile = new Intent(RepartidorHistorial.this, PerfilRepartidor.class);
                    startActivity(intentProfile);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else {
                    return false;
                }

            }
        });
    }


    public void verPerfil(View view){
        Intent intent = new Intent(this, PerfilRepartidor.class);
        startActivity(intent);
    }
    public void verNotificacionesRepartidor(View view ){
        Intent intent = new Intent(this, RepartidorNotificaciones.class);
        view.getId();
        startActivity(intent);
    }
    public void retroceder(View view){
        onBackPressed();
    }


//    public void verDetalleCompraDelivery(View view){
//        Intent intent = new Intent(this, RepartidorDetalleCompraDelivery.class);
//        //Antes de lanzar un activity seteemos algunos valores;
//        View ver = (View) view.getParent().getParent().getParent();
//        int id =  ver.getId();
//        PedidoRepartidor pedido  =new PedidoRepartidor();
//        ArrayList<PedidoRepartidor> pedidos = llenarDatos();
//        for(PedidoRepartidor p : pedidos){
//            if(p.getIdPedidoRepartidor() == id){
//                pedido = p;
//            }
//        }
//        intent.putExtra("id" ,pedido.getIdPedidoRepartidor().toString());
//        intent.putExtra("flag" , "historial");
//        startActivity(intent);
//    }
//    public void verDetalleMapaPedido(View view){
//        Intent intent = new Intent(this, RepartidorDetalleMapaPedido.class);
//        View ver = (View) view.getParent().getParent().getParent();
//        int id =  ver.getId();
//        PedidoRepartidor pedido  =new PedidoRepartidor();
//        ArrayList<PedidoRepartidor> pedidos = llenarDatos();
//        for(PedidoRepartidor p : pedidos){
//            if(p.getIdPedidoRepartidor() == id){
//                pedido = p;
//            }
//        }
//        intent.putExtra("idPedido",pedido.getIdPedidoRepartidor().toString());
//        intent.putExtra("DestinoTienda" , pedido.getDireccionRestaurante());
//        intent.putExtra("DestinoFinal",pedido.getDireccionDelivery());
//        intent.putExtra("flag" , "historial");
//        startActivity(intent);
//    }

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

    //BOTONES
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.repartidor_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.ordenes){
            startActivity(new Intent(this, RepartidorVistaHome.class));
            return true;
        } else if (item.getItemId()==R.id.historial) {
            startActivity(new Intent(this, RepartidorVistaHome.class));
            return true;
        } else if (item.getItemId()==R.id.perfil) {
            startActivity(new Intent(this, RepartidorVistaHome.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

    public void verDetalleCompraDelivery(View view){
        Intent intent = new Intent(this, RepartidorDetalleCompraDelivery.class);
        intent.putExtra("pedido" , view.getContentDescription());
        intent.putExtra("detailOnly" , view.getContentDescription());
        startActivity(intent);
    }

    public void verDetalleMapaPedido(View view){
        Intent intent = new Intent(this, RepartidorTrackingEstadoEnCamino.class);
        intent.putExtra("idPedido" , view.getContentDescription());
        startActivity(intent);
    }
}