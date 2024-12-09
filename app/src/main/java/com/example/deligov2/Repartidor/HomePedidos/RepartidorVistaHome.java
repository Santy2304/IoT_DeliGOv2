package com.example.deligov2.Repartidor.HomePedidos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    private List<Pedido> listaPedidos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_vista_home);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top/2, systemBars.right, 0);
            return insets;
        });
        listaPedidos =  new ArrayList<Pedido>();
        loadUser(()->{
            validarRepartidorDisponible(()->{
                //Exito
                RepartidorPedidosAdapter adapter = new RepartidorPedidosAdapter();
                adapter.setContext(this);
                loadPedidos(()->{
                    adapter.setListaPedidosRepartidor(listaPedidos);
                    RecyclerView recyclerView = findViewById(R.id.lista);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(RepartidorVistaHome.this));
                });
            },()->{
                //Fallo
                //Se debería bloquear la pantalla
                showNonCancelableDialog();
            });
        });


    }
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
    public void aceptarPedido(View view ){
        AlertDialog.Builder builder = new AlertDialog.Builder(RepartidorVistaHome.this);
        builder.setTitle("Confirmar acción");
        builder.setMessage("¿Qué acción deseas realizar con esta solicitud?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción cuando se presiona "Aceptar"
                aceptar(view.getContentDescription().toString() , user.getUid() , ()->{
                    Intent intent = new Intent(RepartidorVistaHome.this, RepartidorAceptacionPedido.class);
                    startActivity(intent);
                }, ()->{
                    Intent intent = new Intent(RepartidorVistaHome.this, RepartidorCancelacionPedido.class);
                    startActivity(intent);
                        }
                );

            }
        });


        builder.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción cuando se presiona "Cancelar" (cerrar el diálogo)
                dialog.dismiss();
            }
        });
        // Mostrar el diálogo
        builder.create().show();
    }
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
                            if(pedido.getIdRepartidor() == null && (pedido.getEstado().equals("Recibido") || pedido.getEstado().equals("En preparacion")  || pedido.getEstado().equals("Pendiente"))){
                                listaPedidos.add(pedido);
                            }
                        }
                        onsuccess.run();
                    }
                });
    }
    public void verOtros(View view){
        startActivity(new Intent(this , RepartidorTrackingEstadoEnCamino.class));
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
                    onfailure.run();                });
    }



    public void validarRepartidorDisponible(Runnable onsuccess  , Runnable onfailure){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Integer count = 0 ;
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getIdRepartidor() != null && pedido.getIdRepartidor().equals(usuario.getId())  && (pedido.getEstado().equals("Recibido") || pedido.getEstado().equals("En preparacion")) ){
                                count++;
                            }
                        }
                        if(count>0){
                            onfailure.run();
                        }else{
                            onsuccess.run();
                        }
                    }
                });
    }

    private void showNonCancelableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Usted tiene una entrega pendiente");
        // Botón para cerrar el diálogo
        builder.setPositiveButton("Ver mapa", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción al presionar el botón (puedes cerrar el diálogo aquí)
                //Se redirige a la vista del tracking
                dialog.dismiss();
            }
        });

        // Crear el diálogo
        AlertDialog dialog = builder.create();

        // Evitar que se cierre al tocar fuera del diálogo
        dialog.setCanceledOnTouchOutside(false);

        // Evitar que se cierre al presionar el botón de retroceso
        dialog.setCancelable(false);

        // Mostrar el diálogo
        dialog.show();
    }
}