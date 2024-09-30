package com.example.deligov2.SuperAdmin;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminAdministradorListAdapter;
import com.example.deligov2.Adapters.SuperAdminLogAdapter;
import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.Beans.Logs;
import com.example.deligov2.R;

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

        mostrarListaLogs();
    }

    public void mostrarListaLogs(){
        logs = new ArrayList<>();
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