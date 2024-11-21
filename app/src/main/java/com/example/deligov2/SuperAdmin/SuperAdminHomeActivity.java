package com.example.deligov2.SuperAdmin;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminClienteListAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.MainActivity;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SuperAdminHomeActivity extends AppCompatActivity {

    List<Cliente> clientes;
    private MaterialCardView cardCliente;
    private GradientDrawable borderDrawable;
    SuperAdminClienteListAdapter listAdapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_home);

        mostrarListaClientes();

        //Para el buscador
        TextInputEditText searchInput;
        searchInput = findViewById(R.id.textInputLayout).findViewById(R.id.buscarCliente);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminHomeActivity.this, SuperAdminVistaLogEvent.class);
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
                    Intent intentRestaurant = new Intent(SuperAdminHomeActivity.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminHomeActivity.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminHomeActivity.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Efectos
        cardCliente = findViewById(R.id.materialCardViewCliente);

        ObjectAnimator animator = ObjectAnimator.ofFloat(cardCliente, "translationX", 0f, 10f);
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

    //Colocar datos
    public void mostrarListaClientes(){
        clientes = new ArrayList<>();
        clientes.add(new  Cliente(1,"Elizabeth","Del Lago","cliente1@gmail.com","12345678","DNI",true,"987654321","Av.Ola", new Date(100000000000L),"ola1234"));
        clientes.add(new  Cliente(2,"Tule","Del Lago","cliente2@gmail.com","12345678","DNI",true,"987654321","Av.Ola", new Date(100000000000L),"ola1234"));
        clientes.add(new  Cliente(3,"Pera","Del Lago","cliente3@gmail.com","12345678","DNI",true,"987654321","Av.Ola", new Date(100000000000L),"ola1234"));
        clientes.add(new  Cliente(4,"Con la","Del Lago","cliente4@gmail.com","12345678","DNI",true,"987654321","Av.Ola", new Date(100000000000L),"ola1234"));
        clientes.add(new  Cliente(5,"Papaya","Del Lago","cliente5@gmail.com","12345678","DNI",true,"987654321","Av.Ola", new Date(100000000000L),"ola1234"));


        listAdapter = new SuperAdminClienteListAdapter(clientes,this);
        RecyclerView recyclerView = findViewById(R.id.listClientesRecyler);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);

        // Añade el OnTouchListener o OnClickListener aquí
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // Aquí puedes detectar los eventos de tacto
                return false;
            }
        });

        // O añade el OnClickListener
        recyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Aquí puedes detectar los eventos de clic
            }
        });
    }


    //Cambio vista
    public void vistaPanelRepartidor(View view) {
        Intent intent = new Intent(this, SuperAdminRepartidor.class);
        startActivity(intent);
    }

    public void vistaPanelAdmin(View view) {
        Intent intent = new Intent(this, SuperAdminAdministrador.class);
        startActivity(intent);
    }


}