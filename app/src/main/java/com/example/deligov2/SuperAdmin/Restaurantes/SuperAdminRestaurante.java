package com.example.deligov2.SuperAdmin.Restaurantes;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminRestauranteListAdapter;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.Restaurantes.RegistrarRestaurante.SuperAdminRegistroRestaurante1;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRestaurante extends AppCompatActivity {
    List<Restaurante> restaurantes = new ArrayList<>();
    SuperAdminRestauranteListAdapter listAdapter;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_restaurante);

        //Firebase
        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");


        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarRestaurante);

        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminRestaurante.this, SuperAdminVistaLogEvent.class);
//                    startActivity(intent);
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRestaurante.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestaurante.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestaurante.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else{
                    return false;
                }

            }
        });

        FloatingActionButton btAgregar = findViewById(R.id.bt_agregar);

        btAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRegistroRestaurante1(sa);
            }
        });



        //Manejo del buscador
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                buscarRestaurantes(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        mostrarListaRestaurante();
    }

    public void mostrarListaRestaurante(){
        /*
        restaurantes = new ArrayList<>();
         */

        listAdapter = new SuperAdminRestauranteListAdapter(restaurantes, this);
        RecyclerView recyclerView = findViewById(R.id.listRestaurantesPanel);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        db.collection("restaurantes").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                restaurantes.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Restaurante restaurante = document.toObject(Restaurante.class);
                    restaurantes.add(restaurante);
                }
                listAdapter.notifyDataSetChanged();
            }
        });
    }

    public void buscarRestaurantes(String query) {
        if (query.isEmpty()) {
            mostrarListaRestaurante();
            return;
        }

        db.collection("restaurantes")
                .whereGreaterThanOrEqualTo("nombre", query)
                .whereLessThanOrEqualTo("nombre", query + "\uf8ff")
                .addSnapshotListener((snapshot, error) -> {
                    if (error != null) {
                        Log.w("msg-test", "Listen failed.", error);
                        return;
                    }
                    if (snapshot != null && !snapshot.isEmpty()) {
                        restaurantes.clear();
                        for (DocumentSnapshot document : snapshot.getDocuments()) {
                            Restaurante restaurante = document.toObject(Restaurante.class);
                            if (restaurante != null) {
                                restaurantes.add(restaurante);
                            }
                        }
                        listAdapter.notifyDataSetChanged();
                    }
                });
    }


    //Cambiar vista

    public void vistaRegistroRestaurante1(Usuario sa){
        Intent intent = new Intent(this, SuperAdminRegistroRestaurante1.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }
}