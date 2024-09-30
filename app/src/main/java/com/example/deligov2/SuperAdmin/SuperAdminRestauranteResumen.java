package com.example.deligov2.SuperAdmin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
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
    }
}