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
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminAdministradorListAdapter;
import com.example.deligov2.Adapters.SuperAdminLogAdapter;
import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.Beans.Logs;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminVistaLogEvent extends AppCompatActivity {
    List<Logs> logs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_vista_log_event);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Esto de aquí te manda a la vista anterior
                onBackPressed();
            }
        });

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminVistaLogEvent.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminVistaLogEvent.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminVistaLogEvent.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminVistaLogEvent.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        String nameR = intent.getStringExtra("restaurante"); //por ahora solo fue enviado el nombre
        Log.d("Evento log", "Nombre del restaurante: " + nameR);

        Administrador admin = (Administrador) intent.getSerializableExtra("admin");

        //Manejo de mostrar datos
        mostrarListaLogs(nameR, admin);

        //Manejo del side bar
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        FloatingActionButton btFiltro = findViewById(R.id.bt_filtro);

        btFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                    drawerLayout.closeDrawer(GravityCompat.END);
                } else {
                    drawerLayout.openDrawer(GravityCompat.END);
                }
            }
        });

        // Manejar los botones dentro del sidebar
        NavigationView navigationView = findViewById(R.id.nav_view);
        Button btnLimpiar = navigationView.findViewById(R.id.btn_limpiar);
        Button btnMostrar = navigationView.findViewById(R.id.btn_mostrar);

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkCliente = navigationView.findViewById(R.id.check_cliente);
                CheckBox checkRepartidor = navigationView.findViewById(R.id.check_repartidor);
                CheckBox checkAdministrador = navigationView.findViewById(R.id.check_administrador);
                CheckBox checkRegistros = navigationView.findViewById(R.id.check_registros);
                CheckBox checkPedidos = navigationView.findViewById(R.id.check_pedidos);
                CheckBox checkRechazos = navigationView.findViewById(R.id.check_rechazos);

                checkCliente.setChecked(false);
                checkRepartidor.setChecked(false);
                checkAdministrador.setChecked(false);
                checkRegistros.setChecked(false);
                checkPedidos.setChecked(false);
                checkRechazos.setChecked(false);
            }
        });

        btnMostrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //En el futuro irá la lógica para los filtros seleccionados
            }
        });
    }



    public void mostrarListaLogs(String name, Administrador admin){
        logs = new ArrayList<>();
        if(name != null){
            logs.add(new Logs(6,"Se ha registrado el restaurante "+name+".", new Date()));
        }
        if( admin != null){
            logs.add(new Logs(7,"Se ha asignado el administrador "+ admin.getNombre()+"al restaurante "+admin.getRestaurante(), new Date()));
        }
        logs.add(new Logs(5,"Un restaurante ha sido desahabilitado",new Date()));
        logs.add(new Logs(4,"Un nuevo cliente se ha sido registrado",new Date()));
        logs.add(new Logs(3,"Un nuevo restaurante ha sido registrado",new Date()));
        logs.add(new Logs(2,"Se ha registrado un nuevo cliente ",new Date()));
        logs.add(new Logs(1,"Se ha registrado un nuevo administrador",new Date()));

        SuperAdminLogAdapter listAdapter = new SuperAdminLogAdapter(logs,this);
        RecyclerView recyclerView = findViewById(R.id.listLogs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);
    }
}