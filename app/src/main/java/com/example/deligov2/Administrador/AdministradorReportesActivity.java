package com.example.deligov2.Administrador;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.AdministradorReporteClientesAdapter;
import com.example.deligov2.Adapters.AdministradorReporteComidaAdapter;
import com.example.deligov2.DTO.ReporteCliente;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class AdministradorReportesActivity extends AppCompatActivity {


    ArrayList<Platillo> listaPlatillo = new ArrayList<>();
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String idRestaurante;
    MaterialButton reporteClienteButton, reporteComidaButton;
    AdministradorReporteComidaAdapter adapterComida = new AdministradorReporteComidaAdapter();
    AdministradorReporteClientesAdapter adapterCliente = new AdministradorReporteClientesAdapter();
    ArrayList<String> listaNombres = new ArrayList<>();
    ArrayList<ReporteCliente> reporteClientes = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                    assert usuario != null : "Usuario no creado en DB";
                    idRestaurante = usuario.getRestaurante();
                    String idRes = usuario.getRestaurante();
                    db.collection("Platos").orderBy("cantVentaTotal", Query.Direction.DESCENDING).addSnapshotListener((snapshot, error)->{
                        if (snapshot != null && !snapshot.isEmpty()) {
                            listaPlatillo.clear();
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                Platillo platillo = document.toObject(Platillo.class);
                                Log.w("msg-test", "Listen failed "+ document.getId());
                                if (platillo.getIdRestaurante().equals(idRestaurante)){
                                    listaPlatillo.add(platillo);
                                }

                            }
                        }
                    });

                    db.collection("ReportesClientes").addSnapshotListener((snapshot, error)->{
                        if (error != null) {
                            Log.w("msg-test", "Listen failed.", error);
                            return;
                        }
                        if (snapshot != null && !snapshot.isEmpty()) {
                            reporteClientes.clear();
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                ReporteCliente reporteCliente = document.toObject(ReporteCliente.class);
                                if(reporteCliente.getIdRestaurante().equals(idRes)){
                                    reporteClientes.add(reporteCliente);
                                    db.collection("Usuarios").document(reporteCliente.getIdCliente()).get()
                                            .addOnSuccessListener(documentSnapshotUWU -> {
                                                if (documentSnapshotUWU.exists()) {
                                                    Usuario usuarioUWU = documentSnapshotUWU.toObject(Usuario.class);
                                                    listaNombres.add(usuarioUWU.getNombre()+" "+usuarioUWU.getApellido());
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                                }
                            }
                        }
                    });
                })
                .addOnFailureListener(e -> {
                });


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_reporte_clientes);
        reporteClienteButton = findViewById(R.id.clientesButton);
        reporteComidaButton = findViewById(R.id.comidaButton);

        RecyclerView recyclerView = findViewById(R.id.recyclerReporteClientes);

        // Setear vista por defecto (reporte de platos)
        adapterComida.setListaReportes(listaPlatillo);
        adapterComida.setContext(this);
        recyclerView.setAdapter(adapterComida);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        reporteComidaButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
        reporteClienteButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));

        // Cambio de vista de acuerdo al botón seleccionado
        reporteClienteButton.setOnClickListener(view -> {
            adapterCliente.setListaReportes(reporteClientes);
            adapterCliente.setListaNombres(listaNombres);
            adapterCliente.setContext(this);
            adapterCliente.notifyDataSetChanged();
            reporteComidaButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            reporteClienteButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            recyclerView.setAdapter(adapterCliente);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        reporteComidaButton.setOnClickListener(v -> {
            adapterComida.setListaReportes(listaPlatillo);
            adapterComida.setContext(this);
            adapterComida.notifyDataSetChanged();
            reporteComidaButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.light_green));
            reporteClienteButton.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.dark_green));
            recyclerView.setAdapter(adapterComida);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        });

        // Botón del historial
        FloatingActionButton historyButton = findViewById(R.id.buttonHistorial);

        historyButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, AdministradorHistorialActivity.class);
            startActivity(intent);
        });

        //Navegación por el navigation bar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);
        bottomNavigationView.setSelectedItemId(R.id.reports);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorReportesActivity.this, AdministradorReportesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorReportesActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorReportesActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(AdministradorReportesActivity.this, AdministradorPerfilActivity.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }
}
