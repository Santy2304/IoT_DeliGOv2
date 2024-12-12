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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.example.deligov2.SuperAdmin.Home.Perfiles.SuperAdminVistaPerfilCliente;
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

public class SuperAdminHomeActivity extends AppCompatActivity {
    List<Usuario> clientes = new ArrayList<>();
    private MaterialCardView cardCliente;
    private GradientDrawable borderDrawable;
    SuperAdminClienteListAdapter listAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        loadUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_home);
        ImageView repartidor = findViewById(R.id.imgRepartidor);
        ImageView admin = findViewById(R.id.imgAdmin);
        repartidor.setOnClickListener(v -> {
            vistaPanelRepartidor();
        });
        admin.setOnClickListener(v -> {
            vistaPanelAdmin();
        });

        mostrarListaClientes();

        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarCliente);

        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminHomeActivity.this, SuperAdminVistaLogEvent.class);
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
                    Intent intent = new Intent(SuperAdminHomeActivity.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminHomeActivity.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminHomeActivity.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Efectos
        cardCliente = findViewById(R.id.materialCardViewCliente);

        ObjectAnimator animator = ObjectAnimator.ofFloat(cardCliente, "translationX", 0f, 10f);
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
                listAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    //Colocar datos
    public void mostrarListaClientes(){

        listAdapter = new SuperAdminClienteListAdapter(clientes,this);
        RecyclerView recyclerView = findViewById(R.id.listClientesRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        db.collection("Usuarios")
                .whereEqualTo("rol", "Cliente")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Usuario cliente = doc.toObject(Usuario.class);
                        clientes.add(cliente);
                    }

                    listAdapter.setClientes(clientes);
                    listAdapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener repartidores: ", e);
                });

        // Añade el OnTouchListener o OnClickListener aquí
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Aquí puedes detectar los eventos de tacto
                return false;
            }
        });

        // O añade el OnClickListener
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes detectar los eventos de clic
            }
        });
    }

    //Cambio vista
    public void vistaPanelRepartidor() {
        Intent intent = new Intent(this, SuperAdminRepartidor.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void vistaPanelAdmin() {
        Intent intent = new Intent(this, SuperAdminAdministrador.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    public void vistaPerfilCliente(View view){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(view.getContentDescription().toString())){
                                Intent intent = new Intent(this, SuperAdminVistaPerfilCliente.class);
                                intent.putExtra("cliente_detail", document.toObject(Usuario.class));
                                startActivity(intent);
                            }
                        }
                    }
                });

    }
}