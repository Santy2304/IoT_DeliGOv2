package com.example.deligov2.Cliente;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClientePlatosAdapter;
import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.A;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClienteCarrito extends AppCompatActivity {

    FirebaseFirestore db;
    FloatingActionButton notiButton;
    FloatingActionButton returnRestaurant;
    Button orderButton;
    Button vaciarButton;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    ArrayList<Platillo> lista = new ArrayList<>();
    ArrayList<String> idsPlatos = new ArrayList<>();
    Carrito carrito;
    ClienteCarritoAdapter adapter;
    ArrayList<Integer> cantidades = new ArrayList<>();
    TextView totalTextView, costoEnvioText, costoProductosText;
    Double costoEnvio;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_carrito);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        notiButton = findViewById(R.id.noti_button);
        vaciarButton = findViewById(R.id.vaciarButton);
        returnRestaurant = findViewById(R.id.comprarMas);
        orderButton = findViewById(R.id.orderButton);

        totalTextView = findViewById(R.id.costoTotal);
        costoEnvioText = findViewById(R.id.costoEnvio);
        costoProductosText = findViewById(R.id.precioProdcutos);

        costoEnvio = Math.random() * 5 + 1;
        costoEnvioText.setText(String.format("S/%.2f", costoEnvio));

        db.collection("Carrito").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        carrito = documentSnapshot.toObject(Carrito.class);
                        idsPlatos = carrito.getIdListaPlatos();

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
                                for (int i = 0; i < lista.size(); i++) {
                                    cantidades.add(1); // Cada platillo comienza con cantidad 1
                                }

                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));


        adapter = new ClienteCarritoAdapter();
        adapter.setContext(this);
        adapter.setListaPlatosCarrito(lista);
        adapter.setCantidades(cantidades);
        adapter.setOnDataChangeListener(() -> recalcularMontos());

        RecyclerView recyclerView = findViewById(R.id.recy);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(ClienteCarrito.this));


        orderButton.setOnClickListener(view -> {
            List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
            List<Integer> listaActualizadaCantidades = adapter.getCantidades();

            if (listaActualizadaPlatos.isEmpty()) {
                Toast.makeText(this, "El carrito está vacío.", Toast.LENGTH_SHORT).show();
                return;
            }
            ArrayList<String> idListaPlatos = new ArrayList<>();
            ArrayList<Float> preciosActuales = new ArrayList<>();

            for (Platillo platillo : listaActualizadaPlatos) {
                idListaPlatos.add(platillo.getId());
                preciosActuales.add(platillo.getPrecio());
            }

            Pedido pedido = new Pedido();
            pedido.setIdRestaurante(carrito.getIdRestaurante());
            pedido.setId(UUID.randomUUID().toString());
            pedido.setIdListaPlatos(idListaPlatos);
            pedido.setPreciosActuales(preciosActuales);
            pedido.setListaCantidades(new ArrayList<>(listaActualizadaCantidades));
            pedido.setIdUsuario(user.getUid());
            pedido.setEstado("Recibido");
            pedido.setHora(ZonedDateTime.now());

            db.collection("Pedidos").document(pedido.getId())
                    .set(pedido)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Pedido realizado exitosamente", Toast.LENGTH_SHORT).show();
                        adapter.getListaPlatosCarrito().clear();
                        adapter.getCantidades().clear();
                        adapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al realizar el pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    });
        });

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteNotificacionesActivity.class);
            startActivity(intent);
        });

        returnRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteRestaurantActivity.class);
            startActivity(intent);
        });

        vaciarButton.setOnClickListener(view -> {
            lista.clear();
            cantidades.clear();
            adapter.notifyDataSetChanged();
            recalcularMontos();
            db.collection("Carrito").document(user.getUid())
                    .update("idListaPlatos", new ArrayList<>())
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Carrito vaciado.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al vaciar el carrito.", Toast.LENGTH_SHORT).show());
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteCarrito.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteCarrito.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteCarrito.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

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
    // Mueve el método aquí
    public void deleteFood(View view) {
        // Necesitas obtener la posición del item que se clicó
        RecyclerView recyclerView = findViewById(R.id.recy);
        int position = recyclerView.getChildAdapterPosition(view);
        ClienteCarritoAdapter adapter = new ClienteCarritoAdapter();

    }

    private void recalcularMontos() {
        List<Platillo> listaPlatos = adapter.getListaPlatosCarrito();
        List<Integer> cantidades = adapter.getCantidades();

        double total = 0;
        for (int i = 0; i < listaPlatos.size(); i++) {
            Platillo platillo = listaPlatos.get(i);
            total += platillo.getPrecio() * cantidades.get(i);
        }

        costoProductosText.setText(String.format("S/%.2f", total));
        total += costoEnvio;
        totalTextView.setText(String.format("S/%.2f", total));
    }

}