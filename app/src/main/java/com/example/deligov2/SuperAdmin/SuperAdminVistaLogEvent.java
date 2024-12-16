package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteHistorialAdapter;
import com.example.deligov2.Adapters.SuperAdminLogAdapter;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.LogSuper;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SuperAdminVistaLogEvent extends AppCompatActivity {
    ArrayList<LogSuper> listalogsSuperPedido = new ArrayList<>();
    ArrayList<LogSuper> listalogsSuperRest = new ArrayList<>();
    ArrayList<LogSuper> lista = new ArrayList<>();
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Usuario usuario;
    MaterialButton restaurantesButton, btPedidos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUserSa();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_vista_log_event);
        btPedidos = findViewById(R.id.bt_pedidos);
        restaurantesButton = findViewById(R.id.bt_res);
        RecyclerView recyclerView = findViewById(R.id.listLogs);
        SuperAdminLogAdapter adapter = new SuperAdminLogAdapter();
        adapter.setContext(this);

        db.collection("Logs").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                lista.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    LogSuper logSuper = document.toObject(LogSuper.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                        lista.add(logSuper);
                }
                Collections.sort(lista, (p1, p2) -> {
                    if (p1.getFecha() == null || p2.getFecha() == null) {
                        return 0; // Manejo de nulos
                    }
                    return p2.getFecha().compareTo(p1.getFecha()); // Orden descendente
                });
                adapter.setmLog(lista);

                adapter.notifyDataSetChanged();
            }
        });
        adapter.setmLog(lista);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        btPedidos.setOnClickListener(view -> {
            listalogsSuperPedido.clear();
            for(int i=0;i<lista.size();i++){
                if(lista.get(i).getTipo().equals("Pedido")){
                    listalogsSuperPedido.add(lista.get(i));
                }
            }
            adapter.setmLog(listalogsSuperPedido);
            adapter.notifyDataSetChanged();
            restaurantesButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            btPedidos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
        });

        restaurantesButton.setOnClickListener(view -> {
            listalogsSuperRest.clear();
            for(int i=0;i<lista.size();i++){
                if(lista.get(i).getTipo().equals("Restaurante") || lista.get(i).getTipo().equals("Plato")){
                    listalogsSuperRest.add(lista.get(i));
                }
            }
            adapter.setmLog(listalogsSuperRest);
            adapter.notifyDataSetChanged();
            restaurantesButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            btPedidos.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;

                if (item.getItemId() == R.id.restaurant) {
                    intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa", usuario);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                    return true;

                } else if (item.getItemId() == R.id.principal) {
                    intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa", usuario);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                    return true;

                } else if (item.getItemId() == R.id.profile) {
                    intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminPerfil.class);
                    intent.putExtra("sa", usuario);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

                    bottomNavigationView.getMenu().setGroupCheckable(0, false, true);
                    return true;

                } else {
                    return false;
                }
            }
        });


    }

//    public void mostrarListaLogs() {
//        logs = new ArrayList<>();
//        db.collection("restaurantes").get()
//                .addOnSuccessListener(querySnapshot -> {
//                    if (!querySnapshot.isEmpty()) {
//                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
//                            String restaurantName = document.getString("nombre");
//                            String adminId = document.getString("admin");
//                            String restaurantId = document.getId();
//
//                            obtenerDatosAdmin(db, adminId, restaurantName, restaurantId);
//                        }
//                    } else {
//                        actualizarRecyclerView();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    e.printStackTrace();
//                });
//    }
//
//    private void obtenerDatosAdmin(FirebaseFirestore db, String adminId, String restaurantName, String restaurantId) {
//        db.collection("Usuarios").document(adminId).get()
//                .addOnSuccessListener(adminSnapshot -> {
//                    if (adminSnapshot.exists()) {
//                        String adminName = adminSnapshot.getString("nombre");
//                        String adminLastName = adminSnapshot.getString("apellido");
//
//                        String mensaje = "Se ha registrado el restaurante " + restaurantName +
//                                " con el administrador " + adminName + " " + adminLastName + ".";
//
//                        logs.add(new Logs(restaurantId, mensaje, new Date()));
//                        Log.d("FirestoreDebug", "restaurantId: " + restaurantId);
//                        actualizarRecyclerView();
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    e.printStackTrace();
//                });
//    }
//
//    private void actualizarRecyclerView() {
//        SuperAdminLogAdapter listAdapter = new SuperAdminLogAdapter(logs);
//        RecyclerView recyclerView = findViewById(R.id.listLogs);
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(listAdapter);
//    }

    public void loadUserSa(){
        db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuario = documentSnapshot.toObject(Usuario.class);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));
    }

}