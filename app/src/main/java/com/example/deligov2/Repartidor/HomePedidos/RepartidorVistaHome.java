package com.example.deligov2.Repartidor.HomePedidos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.RepartidorPedidosAdapter;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.example.deligov2.Repartidor.PerfilRepartidor;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.example.deligov2.Repartidor.RepartidorHistorial;
import com.example.deligov2.Repartidor.RepartidorNotificaciones;
import com.example.deligov2.Repartidor.ProcesosTracking.RepartidorTrackingEstadoEnCamino;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bumptech.glide.Glide;

public class RepartidorVistaHome extends AppCompatActivity {
    //FIREBASE
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    //FIREBASE
    private List<Pedido> listaPedidos =  new ArrayList<Pedido>();
    private String primeraVez = null;
    private String ola  =  "PrimeraVez";
    private RepartidorPedidosAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        adapter = new RepartidorPedidosAdapter();
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_vista_home);
        listaPedidos =  new ArrayList<Pedido>();
        adapter.setContext(this);

        loadUser(()->{
            validarRepartidorDisponible(()->{
                //Exito
                if(ola.equals("PrimeraVez")) {
                    loadPedidos(() -> {
                        adapter.setListaPedidosRepartidor(listaPedidos);
                        RecyclerView recyclerView = findViewById(R.id.lista);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RepartidorVistaHome.this));
                        ola  = "ga";
                    });
                }
            },()->{

                if(ola.equals("PrimeraVez")) {
                    loadPedidos(() -> {
                        adapter.setListaPedidosRepartidor(listaPedidos);
                        RecyclerView recyclerView = findViewById(R.id.lista);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(RepartidorVistaHome.this));
                        ola  = "ga";
                    });
                }
                //Fallo
                //Se debería bloquear la pantalla
                if(primeraVez ==null){
                    showNonCancelableDialog();
                }
            });
        });
    }

    //CARGAR DATOS
    public void loadUser(Runnable runnable){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                ShapeableImageView image = findViewById(R.id.shapeableImageView3);
                                storageRef = storage.getReference().child("users/" + user.getUid() + "/profile.jpg");
                                storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                    Glide.with(this)
                                            .load(uri)
                                            .placeholder(R.drawable.user_icon) // Imagen de carga
                                            .error(R.drawable.xd)             // Imagen de error
                                            .into(image);
                                }).addOnFailureListener(e -> {
                                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                                });
                                runnable.run();
                            }
                        }
                    }
                });
    }
    public void loadPedidos(Runnable onsuccess){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        listaPedidos.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getIdRepartidor() == null && (pedido.getEstado().equals("Recibido") || pedido.getEstado().equals("En preparacion")  || pedido.getEstado().equals("Listo"))){
                                listaPedidos.add(pedido);
                            }
                        }
                        adapter.notifyDataSetChanged();
                        onsuccess.run();
                    }
                });
    }
    //CARGAR DATOS
    private String idPedidoPendiente  =  null;
    //VALIDACIÓN DE QUE EL REPARTIDOR PUEDE TOMAR EL PEDIDO
    public void validarRepartidorDisponible(Runnable onsuccess  , Runnable onfailure){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        int count = 0 ;
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getIdRepartidor() != null && pedido.getIdRepartidor().equals(user.getUid())  && (pedido.getEstado().equals("Listo")) ){
                                count++;
                                idPedidoPendiente =  pedido.getId();
                            }
                        }
                        if(count==0){
                            onsuccess.run();
                        }else{
                            onfailure.run();
                        }
                    }
                });
    }
    private void showNonCancelableDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Usted tiene una entrega pendiente")
                .setPositiveButton("Ver mapa", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(RepartidorVistaHome.this, RepartidorTrackingEstadoEnCamino.class);
                        intent.putExtra("idPedido" , idPedidoPendiente);
                        startActivity(intent);
                    }
                })
                .setCancelable(false)
                .show();
    }
    //VALIDACIÓN DE QUE EL REPARTIDOR PUEDE TOMAR EL PEDIDO

    //VER DETALLES DEL PEDIDO
    public void verDetalleCompraDelivery(View view){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        listaPedidos.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(view.getContentDescription().toString())){
                                Intent intent = new Intent(RepartidorVistaHome.this, RepartidorDetalleCompraDelivery.class);
                                intent.putExtra("pedido" ,pedido.getId());
                                startActivity(intent);
                            }

                        }
                    }
                });
    }
    public void verDetalleMapaPedido(View view){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        listaPedidos.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(view.getContentDescription().toString())){
                                Intent intent = new Intent(RepartidorVistaHome.this, RepartidorDetalleMapaPedido.class);
                                intent.putExtra("pedido" , pedido.getId());
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
    //VER DETALLES DEL PEDIDO

    //ACEPTAR PEDIDO
    public void aceptarPedido(View view ){

        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Qué acción deseas realizar con esta solicitud?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aceptar(view.getContentDescription().toString() , user.getUid() , ()->{
                                    primeraVez = "noTeLevantes";
                                    Intent intent =        new Intent(RepartidorVistaHome.this, RepartidorAceptacionPedido.class);
                                    intent.putExtra("idPedido", view.getContentDescription().toString());
                                    startActivity(intent);
                                }, ()->{
                                    Intent intent = new Intent(RepartidorVistaHome.this, RepartidorCancelacionPedido.class);
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
                    onfailure.run();
                });
    }
    //ACEPTAR PEDIDO

    //REDIRECCIONES A OTROS ACTIVITIES Q NO SON DEL RECYCLER
    public void verNotificacionesRepartidor(View view ){
        Intent intent = new Intent(RepartidorVistaHome.this, RepartidorNotificaciones.class);
        view.getId();
        startActivity(intent);
    }
    public void verHistorialRepartidor(View view){
        Intent intent = new Intent(this, RepartidorHistorial.class);
        startActivity(intent);
    }
    public void verPerfil(View view){
        Intent intent = new Intent(RepartidorVistaHome.this, PerfilRepartidor.class);
        startActivity(intent);
    }
}