package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorDetalleCompraAdapter;
import com.example.deligov2.Adapters.AdministradorHistorialAdapter;
import com.example.deligov2.Beans.DetalleCompra;
import com.example.deligov2.Beans.Solicitud;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AdministradorHistorialActivity extends AppCompatActivity {

    List<Pedido> pedidosPendientes = new ArrayList<>();
    List<Pedido> pedidosEntregados = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseStorage storage;
    RecyclerView recyclerView;
    AdministradorHistorialAdapter adapter = new AdministradorHistorialAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_historial);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        recyclerView = findViewById(R.id.recyclerHistorialAdmin);

        // Obtener el id del usuario actual
        String idUsuario = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Obtener el id del restaurante del usuario actual
        db.collection("Usuarios").document(idUsuario).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String idRestaurante = documentSnapshot.getString("restaurante");
                        cargarHistorial(idRestaurante);
                    } else {
                        Log.e("Firestore", "No se encontró el usuario con ID: " + idUsuario);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener usuario", e));

        // Obtener los componentes de la interfaz
        MaterialButton pendientesButton = findViewById(R.id.pendientesButton);
        MaterialButton entregadosButton = findViewById(R.id.entregadosButton);
        pendientesButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
        entregadosButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));

        // Configuramos los botones
        pendientesButton.setOnClickListener(view -> {
            adapter.setContext(this);
            adapter.setListaSolicitudes(pedidosPendientes);
            adapter.notifyDataSetChanged();
            pendientesButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            entregadosButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        entregadosButton.setOnClickListener(view -> {
            adapter.setContext(this);
            adapter.setListaSolicitudes(pedidosEntregados);
            adapter.notifyDataSetChanged();
            pendientesButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            entregadosButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        // Bottom Navigation View
        // Navegación por medio del bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorHistorialActivity.this, AdministradorReportesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorHistorialActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorHistorialActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else{
                    return false;
                }

            }
        });
    }

    private void cargarHistorial(String idRestaurante) {
        // Cargamos la lista de pedidos pendientes
        db.collection("Pedidos")
                .whereEqualTo("idRestaurante", idRestaurante)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pedidosPendientes.clear();
                    pedidosEntregados.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        if ("Entregado".equals(doc.getString("estado"))) {
                            pedidosEntregados.add(doc.toObject(Pedido.class));
                        } else {
                            pedidosPendientes.add(doc.toObject(Pedido.class));
                        }
                    }
                    // Configuramos lista por default
                    adapter.setContext(this);
                    adapter.setListaSolicitudes(pedidosPendientes);

                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener pedidos", e);
                });
    }
}
