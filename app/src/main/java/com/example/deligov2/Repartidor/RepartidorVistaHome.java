package com.example.deligov2.Repartidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.RepartidorPedidosAdapter;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        loadUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_vista_home);
        RepartidorPedidosAdapter adapter = new RepartidorPedidosAdapter();
        adapter.setContext(this);
        listaPedidos =  new ArrayList<Pedido>();
        loadPedidos(()->{
            adapter.setListaPedidosRepartidor(listaPedidos);
            RecyclerView recyclerView = findViewById(R.id.lista);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(RepartidorVistaHome.this));
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
    //Metodos que redirigen apartir de los elementos del recyclerView
    public void verDetalleCompraDelivery(View view){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        listaPedidos.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);

                            if(pedido.getId().equals(view.getContentDescription().toString())){
                                Intent intent = new Intent(RepartidorVistaHome.this, RepartidorDetalleMapaPedido.class);
                                intent.putExtra("pedido" ,pedido);
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
                                intent.putExtra("pedido" ,pedido);
                                startActivity(intent);
                            }
                        }
                    }
                });
    }
    public void aceptacionRepartidor2(View view ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción");
        builder.setMessage("¿Qué acción deseas realizar con esta solicitud?");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Acción cuando se presiona "Aceptar"
                //Se realiza una acción xd
                Intent intent = new Intent(RepartidorVistaHome.this, RepartidorAceptacionPedido.class);
                startActivity(intent);
            }
        });

        // Botón para rechazar

        // Botón para cancelar
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
    public void loadPedidos(Runnable onsuccess){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        listaPedidos.clear();
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getIdRepartidor() == null && (pedido.getEstado().equals("Recibido") || pedido.getEstado().equals("En preparacion"))){
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

}