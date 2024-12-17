package com.example.deligov2.Repartidor.HomePedidos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.RepartidorDetalleComidaAdapter;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.example.deligov2.Repartidor.PerfilRepartidor;
import com.example.deligov2.Repartidor.RepartidorHistorial;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RepartidorDetalleCompraDelivery extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    private Pedido pedidoSupreme;
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        //Cargamos firebase
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Cargamos vista
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_detalle_compra_delivery);
        //Seteamos los valores
        loadUser(()->{
            loadPedidos(getIntent().getStringExtra("pedido") , ()->{
                ((TextView)findViewById(R.id.idPedido)).setText("#"+pedidoSupreme.getId());
                ((MaterialButton)findViewById(R.id.btn_estado)).setText(pedidoSupreme.getEstado());
                ((TextView)findViewById(R.id.direccion)).setText("Destino : "+pedidoSupreme.getDireccion());
                ((TextView)findViewById(R.id.ola)).setText( String.format("Precio por delivery: %.2f" , pedidoSupreme.getCostoEnvio()));
                ((ExtendedFloatingActionButton)findViewById(R.id.btn_aceptar)).setContentDescription(pedidoSupreme.getId());
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference fileRef = storage.getReference().child("restaurantes/"+pedidoSupreme.getIdRestaurante()+"/logo.jpg");
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(RepartidorDetalleCompraDelivery.this)
                            .load(uri)
                            .placeholder(R.drawable.camara_icon)
                            .error(R.drawable.camara_icon)
                            .into((ImageView) findViewById(R.id.logo));
                }).addOnFailureListener(error -> {

                });

                fileRef = storage.getReference().child("restaurantes/"+pedidoSupreme.getIdRestaurante()+"/banner.jpg");
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    Glide.with(RepartidorDetalleCompraDelivery.this)
                            .load(uri)
                            .placeholder(R.drawable.camara_icon)
                            .error(R.drawable.camara_icon)
                            .into((ImageView) findViewById(R.id.imageView5));
                }).addOnFailureListener(error -> {

                });

                Float sum =  new Float(0);
                for(Integer j =0 ; j<pedidoSupreme.getIdListaPlatos().size(); j++){
                    sum = sum +  pedidoSupreme.getPreciosActuales().get(j) * pedidoSupreme.getListaCantidades().get(j);
                }
                ((TextView)findViewById(R.id.id_costo)).setText(String.format("Costo: %.2f" , sum));
                obtenerPlatillos(pedidoSupreme.getIdListaPlatos(), ()->{
                    RepartidorDetalleComidaAdapter adapter = new RepartidorDetalleComidaAdapter();
                    adapter.setContext(this);
                    adapter.setLista(convertComida(pedidoSupreme.getIdListaPlatos() , devolverPlatos(pedidoSupreme.getIdListaPlatos()) ,  pedidoSupreme.getListaCantidades() , pedidoSupreme.getPreciosActuales()));
                    RecyclerView recyclerView = findViewById(R.id.lista);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                });
                //Empezamos con el adapter


            });
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.ordenes);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.ordenes) {
                    Intent intentRestaurant = new Intent(RepartidorDetalleCompraDelivery.this, RepartidorVistaHome.class);
                    startActivity(intentRestaurant);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (item.getItemId() == R.id.historial) {
                    Intent intentPrincipal = new Intent(RepartidorDetalleCompraDelivery.this, RepartidorHistorial.class);
                    startActivity(intentPrincipal);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else if (item.getItemId() == R.id.perfil) {
                    Intent intentProfile = new Intent(RepartidorDetalleCompraDelivery.this, PerfilRepartidor.class);
                    startActivity(intentProfile);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                } else {
                    return false;
                }

            }
        });

        if(getIntent().getStringExtra("detailOnly") != null){
            findViewById(R.id.btn_aceptar).setVisibility(View.GONE);
        }
        if(getIntent().getStringExtra("detailOnly") != null){
            ((FloatingActionButton)findViewById(R.id.atras)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  = new Intent(RepartidorDetalleCompraDelivery.this , RepartidorHistorial.class);
                    startActivity(intent);
                }
            });
        }else {
            ((FloatingActionButton)findViewById(R.id.atras)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent  = new Intent(RepartidorDetalleCompraDelivery.this , RepartidorVistaHome.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void retroceder(View view) {
        Intent intent = new Intent(this, RepartidorVistaHome.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    public void loadUser(Runnable runnable){
        db.collection("Usuarios")
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                runnable.run();
                            }
                        }
                    }
                });
    }
    public void loadPedidos(String idPedido, Runnable run){
        db.collection("Pedidos")
                .get()
                .addOnCompleteListener((task) -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(idPedido)){
                                pedidoSupreme =  pedido;
                                run.run();
                            }
                        }
                    }
                });
    }
    ArrayList<Platillo> listaPlatillos = new ArrayList<>() ;

    public void obtenerPlatillos(ArrayList<String> listaIds, Runnable runnable){
        for (int i = 0 ; i < listaIds.size() ; i++){
            db.collection("Platos")
                    .get()
                    .addOnCompleteListener((task) -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Platillo plato = document.toObject(Platillo.class);
                                if(listaIds.contains( plato.getId()) ){
                                    listaPlatillos.add(plato);
                                }
                            }
                            runnable.run();
                        }
                    });
        }
    }

    public ArrayList<String> devolverPlatos(ArrayList<String> ids){
        ArrayList<String> nuevo = new ArrayList<>();
        for(int i =0 ; i < ids.size() ; i++){
            //Buscamos en el arreglo
            for( Platillo p :  listaPlatillos){
                if(p.getId().equals(ids.get(i))){
                    nuevo.add(p.getNombre());
                    break;
                }
            }
        }
        return nuevo;
    }
    public List<Comida> convertComida(ArrayList<String> listaIds, ArrayList<String> idComidas , ArrayList<Integer> listaCantidades , ArrayList<Float> preciosActuales){
        List<Comida> lista = new ArrayList<>();
        for (int i = 0 ; i < listaCantidades.size() ;  i++){
            Comida comida = new Comida();
            comida.setNombreComida(idComidas.get(i));
            comida.setCantidad(listaCantidades.get(i));
            comida.setIdComida(listaIds.get(i));
            comida.setPrecioActual((preciosActuales.get(i)).toString());
            comida.setIdRestaurante(pedidoSupreme.getIdRestaurante());
            lista.add(comida);
        }
        return lista;
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
    public void aceptarPedido(View view ) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Qué acción deseas realizar con esta solicitud?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aceptar(getIntent().getStringExtra("pedido"), user.getUid(), () -> {
                                 Intent intent=    new Intent(RepartidorDetalleCompraDelivery.this, RepartidorAceptacionPedido.class);
                                    intent.putExtra("idPedido", getIntent().getStringExtra("pedido"));
                                startActivity(intent);
                                }, () -> {
                                    Intent intent = new Intent(RepartidorDetalleCompraDelivery.this, RepartidorCancelacionPedido.class);
                                    startActivity(intent);
                                }
                        );
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
    }

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
            startActivity(new Intent(this, RepartidorHistorial.class));
            return true;
        } else if (item.getItemId()==R.id.perfil) {
            startActivity(new Intent(this, PerfilRepartidor.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }

}