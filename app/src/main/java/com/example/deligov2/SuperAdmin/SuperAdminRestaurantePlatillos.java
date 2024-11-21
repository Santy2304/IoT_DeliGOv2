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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.Adapters.SuperAdminRestauranteCartaAdapter;
import com.example.deligov2.Beans.Cliente;
import com.example.deligov2.Beans.Plato;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class SuperAdminRestaurantePlatillos extends AppCompatActivity {

    List<Plato> platos;

    String[] nombresPlatos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",
            "Cheese",
            "Vegano"

    };

    float[] Precios  = {
            8.2f,
            13.40f,
            11.10f,
            15.30f,
            12.60f,
            9.30f
    };

    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_platillos);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();

        //Manejo del adapter para los datos
        platos = new ArrayList<>();

        for (int i=0;i<6;i++){
            Plato plato = new Plato();
            plato.setNombre(nombresPlatos[i]);
            plato.setDescripcion("Esta jugosa hamburguesa tiene todo para saciar tu hambre a un bajo precio. Un clásico en Bembos");
            plato.setIdRestaurante(1);
            plato.setId(i);
            plato.setPrecio(Precios[i]);
            platos.add(plato);
        }


        SuperAdminRestauranteCartaAdapter adapter = new SuperAdminRestauranteCartaAdapter();
        adapter.setContext(this);
        adapter.setListaPlato(platos);


        RecyclerView recyclerView = findViewById(R.id.recyclerCarta);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // 2 columna


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
                    Intent intent = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });
        // Obtener los datos del intent anterior a este
        Restaurante resR = (Restaurante) intent.getSerializableExtra("res");

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
                    Intent intent = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestaurantePlatillos.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Mostrar los datos
        mostrarDatosRestauranteFirebase(resR.getId());


        //Manejo de los botones
        Button btResumen = findViewById(R.id.bt_ganancias);

        btResumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteResumen(v, resR);
            }
        });

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v, resR);
            }
        });

        Button btUbicacion = findViewById(R.id.bt_ubicacion);

        btUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteUbicacion(v,resR);
            }
        });



    }

    //Cambio vista
    public void vistaRestauranteResumen(View view, Restaurante res) {
        Intent intent = new Intent(this, SuperAdminRestauranteResumen.class);
        intent.putExtra("res",res);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view, Restaurante res) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
        intent.putExtra("res",res);
        startActivity(intent);
    }
    public void vistaRestauranteUbicacion(View view, Restaurante res){
        Intent intent = new Intent(this, SuperAdminRestauranteUbicacion.class);
        intent.putExtra("res",res);
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