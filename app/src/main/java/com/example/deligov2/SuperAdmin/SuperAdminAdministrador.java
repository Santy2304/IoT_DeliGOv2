package com.example.deligov2.SuperAdmin;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminAdministradorListAdapter;
import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminAdministrador extends AppCompatActivity {

    List<Administrador> admins;
    private MaterialCardView cardAdmin;
    private GradientDrawable borderDrawable;
    SuperAdminAdministradorListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_administrador);

        mostrarListaAdmins();

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
                    Intent intentRestaurant = new Intent(SuperAdminAdministrador.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminAdministrador.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminAdministrador.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
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
        admins = new ArrayList<>();
        /*
        admins.add(new Administrador(1,"Admin","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));
        admins.add(new Administrador(1,"Admin2","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));
        admins.add(new Administrador(1,"Admin3","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));

         */

        listAdapter = new SuperAdminAdministradorListAdapter(admins,this);
        RecyclerView recyclerView = findViewById(R.id.listAdmins);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }


    //Cambio vista
    public void vistaPanelRepartidor(View view) {
        Intent intent = new Intent(this, SuperAdminRepartidor.class);
        startActivity(intent);
    }

    public void vistaPanelCliente(View view) {
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        startActivity(intent);
    }
}