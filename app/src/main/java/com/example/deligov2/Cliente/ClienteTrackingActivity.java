package com.example.deligov2.Cliente;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClienteDetalleCompraAdapter;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.NotiActivity;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.slider.Slider;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ClienteTrackingActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    String channelId = "noti";
    Button repartidorButton;
    TextView recibidoText;
    TextView preparacionText;
    TextView listoText;
    TextView caminoText, costoTotal,dateText;
    TextView entregadoTexT;
    Button qrButton;
    Slider slider;
    FloatingActionButton backButton;
    ArrayList<Platillo> lista = new ArrayList<>();
    ArrayList<String> idsPlatos = new ArrayList<>();
    ArrayList<Integer> cantidades = new ArrayList<>();
    ArrayList<Float> listaPrecios = new ArrayList<>();

    ClienteDetalleCompraAdapter adapter;
    String idPedido;
    Pedido pedido;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_tracking);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        crearCanalNotificacion();
        slider = findViewById(R.id.slider);
        recibidoText = findViewById(R.id.recibido);
        preparacionText = findViewById(R.id.preparacion);
        listoText = findViewById(R.id.listo);
        caminoText = findViewById(R.id.camino);
        entregadoTexT = findViewById(R.id.entregado);
        idPedido = getIntent().getStringExtra("idOrder");
        costoTotal = findViewById(R.id.costoTotal);
        dateText = findViewById(R.id.hourText);
        slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(Slider slider, float value, boolean fromUser) {
                String title;
                String content;

                if(value < 50 && value >= 35){
                    title = "Tu pedido está en preparación";
                    content = "Los cocineros ya leyeron tu pedido y lo están cocinando";
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(getResources().getColor(R.color.blue));
                    lanzarNotificacion(title, content);
                } else if (value < 64 && value >= 52) {
                    title = "Tu pedido está listo";
                    recibidoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
                    content = "El pedido ya está cocinado y a la espera de un repartidor que lo recoja.";
                    listoText.setTextColor(getResources().getColor(R.color.blue));
                    lanzarNotificacion(title, content);
                } else if (value < 85 && value >= 70) {
                    title = "Tu pedido está en camino";
                    content = "El repartidor ha tomado tu pedido y está en camino al destino.";
                    caminoText.setTextColor(getResources().getColor(R.color.blue));
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
                    entregadoTexT.setTextColor(Color.BLACK);
                    lanzarNotificacion(title, content);
                } else if (value == 100) {
                    title = "Tu pedido ha llegado";
                    content = "El repartidor ha llegado al destino.";
                    entregadoTexT.setTextColor(getResources().getColor(R.color.blue));
                    recibidoText.setTextColor(Color.BLACK);
                    listoText.setTextColor(Color.BLACK);
                    caminoText.setTextColor(Color.BLACK);
                    preparacionText.setTextColor(Color.BLACK);
                    lanzarNotificacion(title, content);
                }
            }
        });

        db.collection("Pedidos").document(idPedido).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        pedido = documentSnapshot.toObject(Pedido.class);
                        idsPlatos = pedido.getIdListaPlatos();
                        cantidades = pedido.getListaCantidades();
                        listaPrecios = pedido.getPreciosActuales();
                        db.collection("Platos").addSnapshotListener((snapshot, error)->{
                            if (error != null) {
                                Log.w("msg-test", "Listen failed.", error);
                                return;
                            }
                            if (snapshot != null && !snapshot.isEmpty()) {
                                lista.clear();
                                for (DocumentSnapshot document : snapshot.getDocuments()) {
                                    Platillo platillo = document.toObject(Platillo.class);
                                    Log.w("msg-test", "Listen failed "+ document.getId());
                                    if (idsPlatos.contains(platillo.getId())){
                                        lista.add(platillo);
                                    }
                                }

                                float montoTotal = pedido.getCostoEnvio();
                                for (int i = 0; i < lista.size(); i++) {
                                    montoTotal += listaPrecios.get(i) * cantidades.get(i);
                                }

                                costoTotal.setText(String.format("S/ %.2f",montoTotal));

                                Timestamp timestamp = pedido.getHora();
                                Date date = timestamp.toDate();
                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                                String fechaFormateada = dateFormat.format(date);
                                dateText.setText(fechaFormateada);

                                adapter = new ClienteDetalleCompraAdapter();
                                adapter.setContext(this);
                                adapter.setListafood(lista);
                                adapter.setListaCantidades(cantidades);
                                adapter.setListaPrecios(listaPrecios);

                                RecyclerView recyclerView = findViewById(R.id.recycler);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ClienteTrackingActivity.this));
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));



        repartidorButton = findViewById(R.id.repartidorButton);
        qrButton = findViewById(R.id.qrButton);
        backButton = findViewById(R.id.atrasTracking);

        repartidorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteVeRepartidor.class);
            startActivity(intent);
        });

        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteQR.class);
            startActivity(intent);
        });

        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHistorialActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteTrackingActivity.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteTrackingActivity.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteTrackingActivity.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }


    public void crearCanalNotificacion(){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Canal notificaciones default",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Canal para notificaciones con prioridad default");
            channel.enableVibration(true);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            askPermission();

        }
    }

    public void askPermission(){
        //android.os.Build.VERSION_CODES.TIRAMISU == 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) ==
                        PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(ClienteTrackingActivity.this,
                    new String[]{POST_NOTIFICATIONS},
                    101);
        }

    }

    public void lanzarNotificacion(String title,String texto) {
        Intent intent = new Intent(this, ClienteTrackingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle(title)
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(1, builder.build());
        }
    }



    public void verPerfil(View view){
        Intent intent = new Intent(this, ClientePerfil.class);
        startActivity(intent);
    }

    public void verHistorial(View view){
        Intent intent = new Intent(this, ClienteHistorialActivity.class);
        startActivity(intent);
    }

    public void verHome(View view){
        Intent intent = new Intent(this, ClienteHomeActivity.class);
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.historial){
            startActivity(new Intent(this, ClienteHistorialActivity.class));
            return true;
        } else if (item.getItemId()==R.id.restaurant) {
            startActivity(new Intent(this, ClienteRestaurantActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            startActivity(new Intent(this, ClientePerfil.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);

        }

    }
}