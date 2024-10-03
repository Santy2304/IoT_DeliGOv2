package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.example.deligov2.R;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class SuperAdminRestauranteResumen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_restaurante_resumen);

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
                    Intent intentRestaurant = new Intent(SuperAdminRestauranteResumen.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRestauranteResumen.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRestauranteResumen.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
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
                vistaRestaurantePlatillos(v);
            }
        });

        Button btVentas = findViewById(R.id.bt_ventas);

        btVentas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteHistorialVentas(v);
            }
        });

        Button btUbicacion = findViewById(R.id.bt_ubicacion);

        btUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRestauranteUbicacion(v);
            }
        });


    }

    //Cambio vista
    public void vistaRestaurantePlatillos(View view) {
        Intent intent = new Intent(this, SuperAdminRestaurantePlatillos.class);
        startActivity(intent);
    }

    public void vistaRestauranteHistorialVentas(View view) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
        startActivity(intent);
    }
    public void vistaRestauranteUbicacion(View view){
        Intent intent = new Intent(this, SuperAdminRestauranteUbicacion.class);
        startActivity(intent);
    }
}