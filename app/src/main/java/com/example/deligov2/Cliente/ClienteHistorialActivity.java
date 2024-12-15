package com.example.deligov2.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.deligov2.Adapters.ClienteHistorialAdapter;
import com.example.deligov2.Adapters.NotificacionesAdapter;
import com.example.deligov2.Beans.Notificaciones;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class ClienteHistorialActivity extends AppCompatActivity {
    FloatingActionButton notiButton;
    FloatingActionButton carritoButton;
    ArrayList<Pedido> lista = new ArrayList<>();
    ArrayList<Pedido> listaEnCamino = new ArrayList<>();
    ArrayList<Pedido> listaEntregado = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    MaterialButton enCamino, Entregados;
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_historial);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        ClienteHistorialAdapter adapter = new ClienteHistorialAdapter();
        adapter.setContext(this);
        enCamino = findViewById(R.id.enCaminoButton);
        Entregados = findViewById(R.id.EntregadosButton);
        storage = FirebaseStorage.getInstance();
        db.collection("Pedidos").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                lista.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Pedido pedido = document.toObject(Pedido.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                    if (pedido.getIdUsuario().equals(user.getUid())){
                        lista.add(pedido);
                    }

                }
                adapter.notifyDataSetChanged();
            }
        });
        storage = FirebaseStorage.getInstance();
        ShapeableImageView image = findViewById(R.id.imageView);
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
        adapter.setListaOrdenes(lista);
        RecyclerView recyclerView = findViewById(R.id.recicler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        enCamino.setOnClickListener(view -> {
            listaEnCamino.clear();

            for(int i=0;i<lista.size();i++){
                if(!lista.get(i).getEstado().equals("Entregado")){
                    listaEnCamino.add(lista.get(i));
                }
            }
            adapter.setListaOrdenes(listaEnCamino);

            adapter.notifyDataSetChanged();
            enCamino.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            Entregados.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
        });

        Entregados.setOnClickListener(view -> {
            listaEntregado.clear();

            for(int i=0;i<lista.size();i++){
                if(lista.get(i).getEstado().equals("Entregado")){
                    listaEntregado.add(lista.get(i));
                }
            }
            adapter.setListaOrdenes(listaEntregado);

            adapter.notifyDataSetChanged();
            enCamino.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            Entregados.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.historial);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteHistorialActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteHistorialActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteHistorialActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    return true;
                }else{
                    return false;
                }

            }
        });





        notiButton = findViewById(R.id.noti_button);
        carritoButton = findViewById(R.id.cart_button);

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteNotificacionesActivity.class);
            startActivity(intent);
        });

        carritoButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteCarrito.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return true;}
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



    public void verPerfil(View view){
        Intent intent = new Intent(this, ClientePerfil.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}