package com.example.deligov2.SuperAdmin.Restaurantes.DetallesRestaurante;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import com.example.deligov2.R;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SuperAdminRestauranteResumen extends AppCompatActivity {

    private TextView tvRestaurante, tvHorario, tvCategorias, tvAdminRes, tvEstado;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ArrayList<Platillo> listaPlatos = new ArrayList<>();
    Usuario usuario;
    TextView monto, cantidad;
    TextView foodName1, foodName2, foodName3;
    TextView Cant1, Cant2, Cant3;
    ImageView foodImage1, foodImage2, foodImage3;
    FloatingActionButton goBack;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ImageView logo;

    private PieChart pieChart;
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
        foodName1 = findViewById(R.id.foodName1);
        foodName2 = findViewById(R.id.foodName2);
        foodName3 = findViewById(R.id.foodName3);
        Cant1 = findViewById(R.id.Cant1);
        Cant2 = findViewById(R.id.Cant2);
        Cant3 = findViewById(R.id.Cant3);
        foodImage1 = findViewById(R.id.foodImage1);
        foodImage2 = findViewById(R.id.foodImage2);
        foodImage3 = findViewById(R.id.foodImage3);


        tvRestaurante = findViewById(R.id.tv_restaurante);
        logo = findViewById(R.id.logo);
        tvRestaurante.setText(resR.getNombre());
        cantidad = findViewById(R.id.cantidadPedidos);
        monto = findViewById(R.id.recaudado);
        monto.setText(String.format("S/ %.2f", resR.getMonto()));
        cantidad.setText(resR.getTotalPedidos()+"");

        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(view -> {
            Intent intent1 = new Intent(this,SuperAdminRestaurante.class);
            startActivity(intent1);
        });

        StorageReference storageReference = storage.getReference().child("restaurantes/"+resR.getId()+"/logo.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(logo);
        }).addOnFailureListener(e -> {
            logo.setImageResource(R.drawable.camara_icon);
        });



        db.collection("Platos").addSnapshotListener((snapshot, error)->{
            if (error != null) {
                Log.w("msg-test", "Listen failed.", error);
                return;
            }
            if (snapshot != null && !snapshot.isEmpty()) {
                listaPlatos.clear();
                for (DocumentSnapshot document : snapshot.getDocuments()) {
                    Platillo platillo = document.toObject(Platillo.class);
                    Log.w("msg-test", "Listen failed "+ document.getId());
                    if (platillo.getIdRestaurante().equals(resR.getId())){
                        listaPlatos.add(platillo);
                    }
                }
                List<Platillo> topPlatos = listaPlatos.stream()
                        .sorted((p1, p2) -> Integer.compare(p2.getCantVentaTotal(), p1.getCantVentaTotal()))
                        .limit(3)
                        .collect(Collectors.toList());

                if (topPlatos.size() >= 1) {
                    cargarDatosTop(foodImage1, foodName1, Cant1, topPlatos.get(0));
                }
                if (topPlatos.size() >= 2) {
                    cargarDatosTop(foodImage2, foodName2, Cant2, topPlatos.get(1));
                }
                if (topPlatos.size() >= 3) {
                    cargarDatosTop(foodImage3, foodName3, Cant3, topPlatos.get(2));
                }
            } else {
                Log.w("msg-test", "No platillos found for this restaurant.");
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
                    intent.putExtra("sa",usuario);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",usuario);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRestauranteResumen.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",usuario);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        pieChart = findViewById(R.id.pieChart);
        generarGraficoPie(resR.getId());

    }
    private void generarGraficoPie(String restaurantId) {
        List<PieEntry> entries = new ArrayList<>();

        // Paso 1: Obtener los platos relacionados al restaurante
        db.collection("Platos")
                .whereEqualTo("idRestaurante", restaurantId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String nombrePlato = document.getString("nombre");
                        Double cantVentaTotal = document.getDouble("cantRecaudadoTotal");

                        if (nombrePlato != null && cantVentaTotal != null) {
                            entries.add(new PieEntry(cantVentaTotal.floatValue(), nombrePlato));
                        }
                    }
                    if (entries.isEmpty()) {
                        Log.w("PieChart", "No hay datos para mostrar en el gráfico.");
                        pieChart.setNoDataText("No hay datos disponibles");
                        pieChart.setNoDataTextColor(Color.RED);
                        pieChart.invalidate();
                        return;
                    }

                    crearPieChart(entries);
                })
                .addOnFailureListener(e -> Log.e("Platos", "Error al obtener los platos: " + e.getMessage()));
    }

    private void crearPieChart(List<PieEntry> entries) {
        // Crear el conjunto de datos para el gráfico
        PieDataSet dataSet = new PieDataSet(entries, " ");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.GRAY});
        dataSet.setValueTextSize(18f);
        dataSet.setValueTextColor(Color.BLACK);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "S/ " + String.format("%.2f", value);
            }
        });

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        pieChart.setEntryLabelColor(Color.BLACK);

        Description description = new Description();
        description.setText(" ");
        pieChart.setDescription(description);

        // Refrescar el gráfico
        pieChart.invalidate();
    }

    public void vistaRestauranteHistorialVentas(View view, Restaurante res, Usuario sa) {
        Intent intent = new Intent(this, SuperAdminResturanteHistorialVentas.class);
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

    public void  cargarDatosTop(ImageView imageView, TextView name, TextView cant, Platillo platillo){

        name.setText(platillo.getNombre());
        cant.setText("Cantidad: "+platillo.getCantVentaTotal());
        StorageReference storageReference = storage.getReference().child("restaurantes/"+platillo.getIdRestaurante()+"/"+platillo.getId()+"/plato.jpg");

        storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            imageView.setImageResource(R.drawable.camara_icon);
        });
    }
}