package com.example.deligov2.SuperAdmin.Restaurantes.DetallesRestaurante;

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

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.example.deligov2.R;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SuperAdminRestauranteResumen extends AppCompatActivity {

    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_resumen);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

        //Para las estadísticas
        BarChart chartGanancias = findViewById(R.id.chartGanancias);
        BarChart chartVentas = findViewById(R.id.chartVentas);

        // Configura el gráfico de barras para las ganancias mensuales
        chartGanancias.getDescription().setEnabled(false);
        chartGanancias.getLegend().setEnabled(true);
        chartGanancias.getAxisRight().setEnabled(false);
        chartGanancias.getAxisLeft().setAxisMinimum(0f);

// Configura el gráfico de barras para las ventas mensuales
        chartVentas.getDescription().setEnabled(false);
        chartVentas.getLegend().setEnabled(true);
        chartVentas.getAxisRight().setEnabled(false);
        chartVentas.getAxisLeft().setAxisMinimum(0f);

// Agrega datos al gráfico de barras para las ganancias mensuales
        ArrayList<BarEntry> entriesGanancias = new ArrayList<>();
        entriesGanancias.add(new BarEntry(1, 1000));
        entriesGanancias.add(new BarEntry(2, 1200));
        entriesGanancias.add(new BarEntry(3, 1500));
        entriesGanancias.add(new BarEntry(4, 1800));

// Agrega datos al gráfico de barras para las ventas mensuales
        ArrayList<BarEntry> entriesVentas = new ArrayList<>();
        entriesVentas.add(new BarEntry(1, 800));
        entriesVentas.add(new BarEntry(2, 1000));
        entriesVentas.add(new BarEntry(3, 1200));
        entriesVentas.add(new BarEntry(4, 1500));

// Crea un conjunto de datos para las ganancias mensuales
        BarDataSet dataSetGanancias = new BarDataSet(entriesGanancias, "Ganancias");
        dataSetGanancias.setColor(ColorTemplate.MATERIAL_COLORS[0]);
        dataSetGanancias.setValueTextColor(ColorTemplate.MATERIAL_COLORS[0]);

// Crea un conjunto de datos para las ventas mensuales
        BarDataSet dataSetVentas = new BarDataSet(entriesVentas, "Ventas");
        dataSetVentas.setColor(ColorTemplate.MATERIAL_COLORS[1]);
        dataSetVentas.setValueTextColor(ColorTemplate.MATERIAL_COLORS[1]);

// Crea un conjunto de datos para el gráfico de barras
        BarData dataGanancias = new BarData(dataSetGanancias);
        BarData dataVentas = new BarData(dataSetVentas);
        dataGanancias.setValueTextSize(12f);
        dataVentas.setValueTextSize(12f);

// Asigna el conjunto de datos al gráfico de barras
        chartGanancias.setData(dataGanancias);
        chartVentas.setData(dataVentas);

// Notifica al gráfico de barras que los datos han cambiado
        chartGanancias.invalidate();
        chartVentas.invalidate();

        // Obtener las referencias para los datos
        tvRestaurante = findViewById(R.id.tv_restaurante);
        tvHorario = findViewById(R.id.tv_horario);
        tvCategorias = findViewById(R.id.tv_categorias);
        tvAdminRes = findViewById(R.id.tv_adminRes);
        tvEstado = findViewById(R.id.tv_estado);

        // Obtener los datos del intent anterior a este
        Restaurante resR = (Restaurante) intent.getSerializableExtra("res");
        Log.d("RESTAURANTE IDDDD RESUMEN", "OLA: "+resR.getId()+ resR.getAdmin());


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
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminVistaLogEvent.class);
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

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Manejo de los botones
        Button btCarta = findViewById(R.id.bt_carta);

        btCarta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestaurantePlatillos(v, resR,sa);
            }
        });

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v, resR,sa);
            }
        });

        Button btUbicacion = findViewById(R.id.bt_ubicacion);

        btUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteUbicacion(v, resR,sa);
            }
        });


        //Mostrar los datos
        mostrarDatosRestauranteFirebase(resR.getId());


    }

    //Cambio vista
    public void vistaRestaurantePlatillos(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        intent.putExtra("res",res);
        intent.putExtra("sa",sa);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
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

                        db.collection("Usuarios")
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