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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SuperAdminRestauranteResumen extends AppCompatActivity {

    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Usuario usuario;
    ExtendedFloatingActionButton monto, cantidad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUserSa();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_restaurante_resumen);
        Intent intent = getIntent();
        Restaurante resR = (Restaurante) intent.getSerializableExtra("res");
        tvRestaurante = findViewById(R.id.tv_restaurante);
        tvHorario = findViewById(R.id.tv_horario);
//        tvAdminRes = findViewById(R.id.tv_adminRes);
//        tvEstado = findViewById(R.id.tv_estado);
        tvRestaurante.setText(resR.getNombre());
        tvHorario.setText("Horario de Atención: "+resR.getHorario());
        cantidad = findViewById(R.id.cantidadPedidos);
        monto = findViewById(R.id.recaudado);
        monto.setText("S/ %.2f"+resR.getMonto());
        cantidad.setText(resR.getTotalPedidos());
//        db.collection("Usuarios").document(resR.getAdmin()).get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Usuario admin = documentSnapshot.toObject(Usuario.class);
//                        tvAdminRes.setText(admin.getNombre()+" "+admin.getApellido());
//
//                    }
//                })
//                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));




        //Para las estadísticas
//        BarChart chartGanancias = findViewById(R.id.chartGanancias);
//        BarChart chartVentas = findViewById(R.id.chartVentas);

        // Configura el gráfico de barras para las ganancias mensuales
//        chartGanancias.getDescription().setEnabled(false);
//        chartGanancias.getLegend().setEnabled(true);
//        chartGanancias.getAxisRight().setEnabled(false);
//        chartGanancias.getAxisLeft().setAxisMinimum(0f);
//
//// Configura el gráfico de barras para las ventas mensuales
//        chartVentas.getDescription().setEnabled(false);
//        chartVentas.getLegend().setEnabled(true);
//        chartVentas.getAxisRight().setEnabled(false);
//        chartVentas.getAxisLeft().setAxisMinimum(0f);

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
//        chartGanancias.setData(dataGanancias);
//        chartVentas.setData(dataVentas);
//
//// Notifica al gráfico de barras que los datos han cambiado
//        chartGanancias.invalidate();
//        chartVentas.invalidate();



        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",usuario);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",usuario);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",usuario);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

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

    public void loadUserSa(){
        db.collection("Usuarios").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        usuario = documentSnapshot.toObject(Usuario.class);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));
    }
}