package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.SuperAdminRestauranteVentaListAdapter;
import com.example.deligov2.Beans.PedidoPorRestaurante;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SuperAdminResturanteHistorialVentas extends AppCompatActivity {

    ArrayList<PedidoPorRestaurante> lista;
    String[] direcciones = {
            "Av.Urumbaba",
            "Av.Universitaria",
            "Av.Los rosales",
            "Av.La marina",
    };
    float[] montos  = {
            120.50f,
            60.30f,
            45.30f,
            23.80f
    };

    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_resturante_historial_ventas);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

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
                    Intent intent = new Intent(SuperAdminResturanteHistorialVentas.this, SuperAdminVistaLogEvent.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        // Obtener los datos del intent anterior a este
        Restaurante resR = (Restaurante) intent.getSerializableExtra("res");
        Log.d("RESTAURANTE IDDDD RESUMEN", "OLA: "+resR.getId()+"-"+ resR.getAdmin());

        // Obtener las referencias para los datos
        tvRestaurante = findViewById(R.id.tv_restaurante);
        tvHorario = findViewById(R.id.tv_horario);
        tvCategorias = findViewById(R.id.tv_categorias);
        tvAdminRes = findViewById(R.id.tv_adminRes);
        tvEstado = findViewById(R.id.tv_estado);

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminResturanteHistorialVentas.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminResturanteHistorialVentas.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminResturanteHistorialVentas.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        mostrarDatosRestauranteFirebase(resR.getId());

        //Mostrando los datos
        lista = new ArrayList<>();
        for(int i=0;i<4;i++){
            lista.add(new PedidoPorRestaurante(i,"Bembos"+i,"Enviado",montos[i],direcciones[i],0));
        }

        SuperAdminRestauranteVentaListAdapter listAdapter = new SuperAdminRestauranteVentaListAdapter(lista,this);
        RecyclerView recyclerView = findViewById(R.id.recyclerVentas);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(listAdapter);


        //Manejo de los botones
        Button btResumen = findViewById(R.id.bt_ganancias);

        btResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteResumen(v, resR, sa);
            }
        });

        Button btCarta = findViewById(R.id.bt_carta);

        btCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestaurantePlatillos(v, resR,sa);
            }
        });

        Button btUbicacion = findViewById(R.id.bt_ubicacion);

        btUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteUbicacion(v, resR,sa);
            }
        });


    }

    //Cambio vista
    public void vistaRestauranteResumen(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }

    public void vistaRestaurantePlatillos(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }
    public void vistaRestauranteUbicacion(View view, Restaurante res, Usuario sa){
        Intent intent = new Intent(this, SuperAdminRestauranteUbicacion.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }

    private void mostrarDatosRestauranteFirebase(String resID){
        db.collection("restaurantes")
                .document(resID)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreRestaurante = documentSnapshot.getString("nombre");
                        String horario = documentSnapshot.getString("horario");
                        String categorias = documentSnapshot.getString("categorias");
                        String adminRes = documentSnapshot.getString("admin");

                        Log.d("ID ADMINISTRADOR PT2", "OLA2" + adminRes);
                        boolean estado = documentSnapshot.getBoolean("estado");

                        tvRestaurante.setText(nombreRestaurante != null ? nombreRestaurante : "---");
                        tvHorario.setText(horario != null ? "Horario de atención: " + horario : "---");
                        tvCategorias.setText(categorias != null ? "Categorías: " + categorias : "---");

                        if (estado) {
                            tvEstado.setText("Activado");
                            tvEstado.setTextColor(getResources().getColor(R.color.light_green));
                        } else {
                            tvEstado.setText("Desactivado");
                            tvEstado.setTextColor(getResources().getColor(R.color.md_theme_error));
                        }

                        db.collection("administradores")
                                .document(adminRes)
                                .get()
                                .addOnSuccessListener(documentSnapshotAdmin -> {
                                    if (documentSnapshotAdmin.exists()) {
                                        String adminNombre = documentSnapshotAdmin.getString("nombre");
                                        String adminApellido = documentSnapshotAdmin.getString("apellido");
                                        String adminCompleto = (adminNombre != null ? adminNombre : "---") +
                                                " " + (adminApellido != null ? adminApellido : "---");
                                        tvAdminRes.setText("Administrador: " + adminCompleto);
                                    } else {
                                        tvAdminRes.setText("Administrador no encontrado");
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    tvAdminRes.setText("Error al obtener el administrador");
                                    Log.e("Firestore", "Error al obtener el administrador: ", e);
                                });

                    } else {
                        Log.d("Firestore", "El documento no existe.");
                        tvRestaurante.setText("Restaurante no encontrado");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener los datos del restaurante: ", e);
                    tvRestaurante.setText("Error al cargar datos");
                });

    }


}