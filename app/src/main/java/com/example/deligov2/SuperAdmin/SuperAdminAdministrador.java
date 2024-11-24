package com.example.deligov2.SuperAdmin;

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
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminAdministradorListAdapter;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminAdministrador extends AppCompatActivity {

    List<Usuario> admins = new ArrayList<>();
    private MaterialCardView cardAdmin;
    private GradientDrawable borderDrawable;
    SuperAdminAdministradorListAdapter listAdapter;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_administrador);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

        mostrarListaAdmins();

        ImageView repartidor = findViewById(R.id.imgRepartidor);
        ImageView cliente = findViewById(R.id.imgCostumer);

        repartidor.setOnClickListener(v -> {
            vistaPanelRepartidor(v,sa);
        });

        cliente.setOnClickListener(v -> {
            vistaPanelCliente(v,sa);
        });


        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarAdmin);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminVistaLogEvent.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminAdministrador.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
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

        //-----

        // Efecto de color (brillo)
        ValueAnimator colorAnimator = ValueAnimator.ofArgb(
                ContextCompat.getColor(this, R.color.light_green), // Usar ContextCompat para compatibilidad
                Color.parseColor("#32CD32")); // Verde lima brillante

        colorAnimator.setDuration(1000);
        colorAnimator.
                setRepeatCount(ValueAnimator.INFINITE);
        colorAnimator.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimator.addUpdateListener(animation -> {
            cardAdmin.setStrokeColor((int) animation.getAnimatedValue()); // Aplicar el color animado al borde
        });
        colorAnimator.start();

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


    //Cambio vista
    public void vistaPanelRepartidor(View view, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRepartidor.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void vistaPanelCliente(View view, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}