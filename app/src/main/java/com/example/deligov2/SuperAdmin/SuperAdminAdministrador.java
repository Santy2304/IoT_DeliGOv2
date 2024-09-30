package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminAdministrador extends AppCompatActivity {

    List<Administrador> admins;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_administrador);

        mostrarListaAdmins();

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
    }

    public void mostrarListaAdmins(){
        admins = new ArrayList<>();
        admins.add(new Administrador(1,"Admin","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));
        admins.add(new Administrador(1,"Admin2","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));
        admins.add(new Administrador(1,"Admin3","Del Lago","admin@deligo.com",true,"Bembos","Av.universitaria","12345678"));

        SuperAdminAdministradorListAdapter listAdapter = new SuperAdminAdministradorListAdapter(admins,this);
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