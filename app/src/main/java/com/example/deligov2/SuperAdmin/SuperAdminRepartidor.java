package com.example.deligov2.SuperAdmin;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.Adapters.SuperAdminRepartidorListAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Repartidor;
import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminRepartidor extends AppCompatActivity {

    List<Repartidor> repartidores;
    private MaterialCardView cardRepartidor;
    private GradientDrawable borderDrawable;
    SuperAdminRepartidorListAdapter listAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_repartidor);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");
        mostrarListaRepartidores();

        ImageView admin = findViewById(R.id.imgAdmin);
        ImageView cliente = findViewById(R.id.imgCostumer);

        admin.setOnClickListener(v -> {
            vistaPanelAdmin(v,sa);
        });

        cliente.setOnClickListener(v -> {
            vistaPanelCliente(v,sa);
        });


        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarRepartidor);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminVistaLogEvent.class);
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
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRepartidor.this, SuperAdminPerfil.class);
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
                listAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    public void mostrarListaRepartidores(){
        repartidores = new ArrayList<>();
        repartidores.add(new Repartidor(1,"Repartidor","No me jale",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));
        repartidores.add(new Repartidor(1,"August","Deli",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));
        repartidores.add(new Repartidor(1,"Sisifo","Star",true,true,"12345678","repartidor@gmail.com","Av.Urubamba","987654321"));


        listAdapter = new SuperAdminRepartidorListAdapter(repartidores,this);
        RecyclerView recyclerView = findViewById(R.id.listRepartidor);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }

    //Cambio de vista

    public void vistaPanelCliente(View view, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    public void vistaPanelAdmin(View view, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminAdministrador.class);
        intent.putExtra("sa",sa);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}