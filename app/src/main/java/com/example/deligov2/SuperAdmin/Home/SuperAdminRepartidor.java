package com.example.deligov2.SuperAdmin.Home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminRepartidorListAdapter;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRepartidor extends AppCompatActivity {

    List<Usuario> repartidores = new ArrayList<>();
    private MaterialCardView cardRepartidor;
    private GradientDrawable borderDrawable;
    SuperAdminRepartidorListAdapter listAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_repartidor);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();        // Obtener los datos del intent anterior a este
        loadUser();
        mostrarListaRepartidores();
        ImageView admin = findViewById(R.id.imgAdmin);
        ImageView cliente = findViewById(R.id.imgCostumer);
        admin.setOnClickListener(v -> {
            vistaPanelAdmin();
        });
        cliente.setOnClickListener(v -> {
            vistaPanelCliente();
        });
        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarRepartidor);

        FloatingActionButton logButton = findViewById(R.id.logButton);
        logButton.setOnClickListener(v -> {
            Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminVistaLogEvent.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.principal);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else{
                    return false;
                }

            }
        });
        //Efectos
        cardRepartidor = findViewById(R.id.materialCardViewRepartidor);
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardRepartidor, "translationX", 0f, 10f);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();

        //Manejo del buscador
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarRepartidor(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void mostrarListaRepartidores(){
        listAdapter = new SuperAdminRepartidorListAdapter(repartidores,this);
        RecyclerView recyclerView = findViewById(R.id.listRepartidor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
        db.collection("Usuarios")
                .whereEqualTo("rol", "Repartidor")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    repartidores.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Usuario repartidor = doc.toObject(Usuario.class);
                        if(repartidor.getRol().equals("Repartidor")){
                            repartidores.add(repartidor);
                        }
                    }
                    //listAdapter.setRepartidor(repartidores);
                    listAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener repartidores: ", e);
                });
    }
    //Cambio de vista
    public void vistaPanelCliente() {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    public void vistaPanelAdmin() {
        Intent intent = new Intent(this, SuperAdminAdministrador.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    public void buscarRepartidor(String query) {
        //List<Usuario> repartidores2 = new ArrayList<>();
        SuperAdminRepartidorListAdapter listAdapter2 = new SuperAdminRepartidorListAdapter(repartidores,this);
        RecyclerView recyclerView = findViewById(R.id.listRepartidor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter2);
        if (query.isEmpty()) {
            mostrarListaRepartidores();
            return;}
        db.collection("Usuarios")
                .whereEqualTo("rol","Repartidor")
                .whereGreaterThanOrEqualTo("nombre", query)
                .whereLessThanOrEqualTo("nombre", query + "\uf8ff")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.w("msg-test", "Listen failed.", error);
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        repartidores.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Usuario repartidor = document.toObject(Usuario.class);
                            if (repartidor != null) {
                                repartidores.add(repartidor);}}
                        //listAdapter.setRepartidor(repartidores);
                        listAdapter2.notifyDataSetChanged();
                    }
                });}
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
}