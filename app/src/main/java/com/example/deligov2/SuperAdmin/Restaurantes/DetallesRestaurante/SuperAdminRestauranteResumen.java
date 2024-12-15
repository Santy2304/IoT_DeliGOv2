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
        monto.setText("S/ %.2f"+resR.getMonto());
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

        pieChart = findViewById(R.id.pieChart);
        generarGraficoPie(resR.getId());

    }
    private void generarGraficoPie(String restaurantId) {
        // Lista para almacenar los platos válidos y su monto acumulado
        Map<String, Double> platosConMontos = new HashMap<>();

        // Paso 1: Obtener los platos relacionados al restaurante
        db.collection("Platos")
                .whereEqualTo("idRestaurante", restaurantId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<String> idsPlatos = new ArrayList<>();
                    Map<String, String> nombresPlatos = new HashMap<>();

                    Log.d("OLAALASAS", "PROBANDO"+restaurantId);

                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String idPlato = document.getId();
                        String nombre = document.getString("nombre");

                        idsPlatos.add(idPlato);
                        nombresPlatos.put(idPlato, nombre);
                    }

                    Log.d("COntinuando", "PASO2");
                    // Paso 2: Obtener los pedidos relacionados al restaurante
                    db.collection("Pedidos")
                            .whereEqualTo("idRestaurante", restaurantId)
                            .get()
                            .addOnSuccessListener(pedidosSnapshot -> {
                                for (QueryDocumentSnapshot pedido : pedidosSnapshot) {
                                    List<String> idListaPlatos = (List<String>) pedido.get("idListaPlatos");
                                    List<Long> listaCantidades = (List<Long>) pedido.get("listaCantidades");
                                    Log.d("Pedidos", "Lista Cantidades: " + listaCantidades);
                                    List<Double> preciosActuales = (List<Double>) pedido.get("preciosActuales");
                                    Log.d("Pedidos", "Precios Actuales: " + preciosActuales);

                                    if (idListaPlatos != null && listaCantidades != null && preciosActuales != null) {
                                        for (int i = 0; i < idListaPlatos.size(); i++) {
                                            String idPlato = idListaPlatos.get(i);

                                            if (idsPlatos.contains(idPlato)) {
                                                double cantidad = listaCantidades.get(i).doubleValue();
                                                double precio = preciosActuales.get(i);
                                                double monto = cantidad*precio;

                                                platosConMontos.put(idPlato, platosConMontos.getOrDefault(idPlato, 0.0) + monto);
                                            }
                                        }
                                    }
                                }
                                Log.d("MontosGAAAAA", platosConMontos.toString());

                                // Paso 3: Crear el gráfico con los datos procesados
                                crearPieChart(platosConMontos, nombresPlatos);
                            })
                            .addOnFailureListener(e -> Log.e("Pedidos", "Error al obtener pedidos: ", e));
                })
                .addOnFailureListener(e -> Log.e("Platos", "Error al obtener platos: ", e));
    }

    private void crearPieChart(Map<String, Double> platosConMontos, Map<String, String> nombresPlatos) {
        List<PieEntry> entries = new ArrayList<>();

        for (Map.Entry<String, Double> entry : platosConMontos.entrySet()) {
            String idPlato = entry.getKey();
            double monto = entry.getValue();
            String nombrePlato = nombresPlatos.get(idPlato);

            if (nombrePlato != null) {
                entries.add(new PieEntry((float) monto, nombrePlato));
            }
        }

        if (entries.isEmpty()) {
            Log.w("PieChart", "No hay datos para mostrar en el gráfico.");
            pieChart.setNoDataText("No hay datos disponibles");
            pieChart.setNoDataTextColor(Color.RED);
            pieChart.invalidate();
            return;
        }

        //Este es el PIE CHART
        PieDataSet dataSet = new PieDataSet(entries, "Platos");
        dataSet.setColors(new int[]{Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA});
        dataSet.setValueTextSize(12f);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);

        Description description = new Description();
        description.setText("Montos por Plato");
        pieChart.setDescription(description);

        //Esto es para refrescar el grafico
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