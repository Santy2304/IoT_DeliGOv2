package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminLogAdapter;
import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.Beans.Logs;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminVistaLogEvent extends AppCompatActivity {
    List<Logs> logs;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_vista_log_event);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        mostrarListaLogs();

    }

    public void mostrarListaLogs() {
        logs = new ArrayList<>();
        db.collection("restaurantes").get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                            String restaurantName = document.getString("nombre");
                            String adminId = document.getString("admin");
                            String restaurantId = document.getId();

                            obtenerDatosAdmin(db, adminId, restaurantName, restaurantId);
                        }
                    } else {
                        actualizarRecyclerView();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void obtenerDatosAdmin(FirebaseFirestore db, String adminId, String restaurantName, String restaurantId) {
        db.collection("Usuarios").document(adminId).get()
                .addOnSuccessListener(adminSnapshot -> {
                    if (adminSnapshot.exists()) {
                        String adminName = adminSnapshot.getString("nombre");
                        String adminLastName = adminSnapshot.getString("apellido");

                        String mensaje = "Se ha registrado el restaurante " + restaurantName +
                                " con el administrador " + adminName + " " + adminLastName + ".";

                        logs.add(new Logs(restaurantId, mensaje, new Date()));
                        Log.d("FirestoreDebug", "restaurantId: " + restaurantId);
                        actualizarRecyclerView();
                    }
                })
                .addOnFailureListener(e -> {
                    e.printStackTrace();
                });
    }

    private void actualizarRecyclerView() {
        SuperAdminLogAdapter listAdapter = new SuperAdminLogAdapter(logs, this);
        RecyclerView recyclerView = findViewById(R.id.listLogs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}