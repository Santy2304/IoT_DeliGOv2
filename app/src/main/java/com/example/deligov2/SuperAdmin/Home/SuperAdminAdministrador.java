package com.example.deligov2.SuperAdmin.Home;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminAdministradorListAdapter;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
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

public class SuperAdminAdministrador extends AppCompatActivity {

    List<Usuario> admins = new ArrayList<>();
    private MaterialCardView cardAdmin;
    private GradientDrawable borderDrawable;
    SuperAdminAdministradorListAdapter listAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Cargamos la vista
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_administrador);
        //Cargamos conexión a base de datos
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Cargamos al usuario
        loadUser();
        //Se cargan datos de los administradores en el recyclerView
        mostrarListaAdmins();
        //Seteamos el redireccionamiento de los activities
        findViewById(R.id.imgRepartidor).setOnClickListener(v -> {
            vistaPanelRepartidor();
        });
        findViewById(R.id.imgCostumer).setOnClickListener(v -> {
            vistaPanelCliente();
        });
        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarAdmin);
        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminVistaLogEvent.class);
//                    startActivity(intent);
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        });
        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.principal);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else{
                    return false;
                }

            }
        });
        //Efectos
        cardAdmin = findViewById(R.id.materialCardViewAdmin);
        ObjectAnimator animator = ObjectAnimator.ofFloat(cardAdmin, "translationX", 0f, 10f);
        animator.setDuration(500);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.start();
        // Efecto de color (brillo)
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                ContextCompat.getColor(this, R.color.light_green), // Usar ContextCompat para compatibilidad
                Color.parseColor("#32CD32")); // Verde lima brillante

        colorAnimator.setDuration(1000);
        colorAnimator.setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.addUpdateListener(animation -> {cardAdmin.setStrokeColor((int) animation.getAnimatedValue()); // Aplicar el color animado al borde
        });
        colorAnimator.start();
        //Manejo del buscador
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                listAdapter.filter(s.toString());}
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void mostrarListaAdmins(){
        listAdapter = new SuperAdminAdministradorListAdapter(admins,this);
        RecyclerView recyclerView = findViewById(R.id.listAdmins);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
        db.collection("Usuarios")
                .whereEqualTo("rol", "Administrador")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    admins.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Usuario admin = doc.toObject(Usuario.class);
                        admins.add(admin);
                    }
                    listAdapter.setAdmin(admins);
                    listAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener repartidores: ", e);
                });
    }
    public void vistaPanelRepartidor() {
        Intent intent = new Intent(this, SuperAdminRepartidor.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    public void vistaPanelCliente() {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
    //Cargar datos del usuario
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