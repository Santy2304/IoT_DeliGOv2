package com.example.deligov2.Cliente;

import android.content.Intent;
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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClienteDetalleCompraAdapter;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.sax.TemplatesHandler;

public class ClienteDetalleCompra extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    Button verRepartidorButton;
    Button qrButton;
    Button goBackButton;
    ArrayList<Platillo> lista = new ArrayList<>();
    ArrayList<String> idsPlatos = new ArrayList<>();
    ArrayList<Integer> cantidades = new ArrayList<>();
    ArrayList<Float> listaPrecios = new ArrayList<>();
    ClienteDetalleCompraAdapter adapter;
    String idPedido;
    Pedido pedido;
    TextView costoTotal, costoProductos, costoEnvio,dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_detalle_compra);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        idPedido = getIntent().getStringExtra("idOrder");
        costoTotal=findViewById(R.id.costoso);
        costoProductos=findViewById(R.id.costoProducts);
        costoEnvio=findViewById(R.id.costoEnvio);
        dateText=findViewById(R.id.hourText);
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
                                costoEnvio.setText(String.format("S/ %.2f",pedido.getCostoEnvio()));
                                costoProductos.setText(String.format("S/ %.2f",montoTotal- pedido.getCostoEnvio()));
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

                                RecyclerView recyclerView = findViewById(R.id.linearLayout10);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ClienteDetalleCompra.this));
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));


        goBackButton = findViewById(R.id.goBackButton);

        verRepartidorButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteVeRepartidor.class);
            startActivity(intent);
        });

        qrButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteQR.class);
            startActivity(intent);
        });

        goBackButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHistorialActivity.class);
            startActivity(intent);
        });
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteDetalleCompra.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteDetalleCompra.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteDetalleCompra.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

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