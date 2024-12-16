package com.example.deligov2.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.ClienteHistorialAdapter;
import com.example.deligov2.Adapters.NotificacionesAdapter;
import com.example.deligov2.DTO.Notificaciones;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

public class ClienteNotificacionesActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ArrayList<Notificaciones> lista = new ArrayList<>();
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_notificaciones);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        NotificacionesAdapter adapter = new NotificacionesAdapter();
        adapter.setContext(this);

        db.collection("Notificaciones").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                lista.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Notificaciones notificaciones = document.toObject(Notificaciones.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                    if (notificaciones.getIdCliente().equals(user.getUid())){
                        lista.add(notificaciones);
                    }
                }

                Collections.sort(lista, (p1, p2) -> {
                    if (p1.getFecha() == null || p2.getFecha() == null) {
                        return 0;
                    }
                    return p2.getFecha().compareTo(p1.getFecha());
                });
                adapter.setListaNotificaciones(lista);
                adapter.notifyDataSetChanged();
            }
        });
        storage = FirebaseStorage.getInstance();

        ShapeableImageView image = findViewById(R.id.shapeableImageView3);
        storageRef = storage.getReference().child("users/" + user.getUid() + "/profile.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.xd)
                    .into(image);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        });

        adapter.setListaNotificaciones(lista);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewNoti);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteNotificacionesActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteNotificacionesActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteNotificacionesActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

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
}