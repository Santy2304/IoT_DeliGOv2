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

import java.io.Serializable;
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

        db.collection("Carritos").document(user.getUid()).get()
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

                                adapter = new ClienteCarritoAdapter();
                                adapter.setContext(this);
                                adapter.setListaPlatosCarrito(lista);
                                adapter.setCantidades(cantidades);
                                adapter.setOnDataChangeListener(() -> recalcularMontos());

                                RecyclerView recyclerView = findViewById(R.id.recy);
                                recyclerView.setAdapter(adapter);
                                recyclerView.setLayoutManager(new LinearLayoutManager(ClienteCarrito.this));
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));





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

            carrito.setListaCantidades(new ArrayList<>(listaActualizadaCantidades));
            carrito.setIdListaPlatos(idListaPlatos);

            db.collection("Carritos")
                    .document(user.getUid())
                    .set(carrito)
                    .addOnSuccessListener(unused -> {
                        Log.d("msg-test","Data guardada exitosamente");
                        Intent intent = new Intent(this, ClienteConfirmarDireccion.class);
                        intent.putExtra("listaPlatillos", (Serializable) listaActualizadaPlatos);
                        startActivity(intent);
                    })
                    .addOnFailureListener(e -> e.printStackTrace());

//            adapter.getListaPlatosCarrito().clear();
//            adapter.getCantidades().clear();
//            adapter.notifyDataSetChanged();
        });

        notiButton.setOnClickListener(view -> {
            List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
            if(listaActualizadaPlatos.isEmpty()){
                Intent intent = new Intent(this,ClienteNotificacionesActivity.class);
                startActivity(intent);
            }else{
                actualizarCarrito(new Intent(this,ClienteNotificacionesActivity.class));
//                Intent intent = new Intent(this,ClienteNotificacionesActivity.class);
//                startActivity(intent);
            }

        });

        returnRestaurant.setOnClickListener(view -> {
            List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
            if(listaActualizadaPlatos.isEmpty()){
                Intent intent = new Intent(this,ClienteRestaurantActivity.class);
                startActivity(intent);
            }else{
                actualizarCarrito(new Intent(this,ClienteRestaurantActivity.class));
//                Intent intent = new Intent(this,ClienteRestaurantActivity.class);
//                startActivity(intent);

            }
        });

        vaciarButton.setOnClickListener(view -> {
            lista.clear();
            cantidades.clear();
            adapter.notifyDataSetChanged();
            recalcularMontos();
            carrito.setIdListaPlatos(new ArrayList<>());
            carrito.setListaCantidades(new ArrayList<>());
            carrito.setIdRestaurante("");
            db.collection("Carritos").document(user.getUid())
                    .set(carrito)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Carrito vaciado.", Toast.LENGTH_SHORT).show();
                    });
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
                    if(listaActualizadaPlatos.isEmpty()){
                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClienteHomeActivity.class);
                        startActivity(intentRestaurant);
                    }else{
                        actualizarCarrito(new Intent(ClienteCarrito.this, ClienteHomeActivity.class));
//                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClienteHomeActivity.class);
//                        startActivity(intentRestaurant);
                    }
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
                    if(listaActualizadaPlatos.isEmpty()){
                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClienteHistorialActivity.class);
                        startActivity(intentRestaurant);
                    }else{
                        actualizarCarrito(new Intent(ClienteCarrito.this, ClienteHistorialActivity.class));
//                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClienteHistorialActivity.class);
//                        startActivity(intentRestaurant);
                    }
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
                    if(listaActualizadaPlatos.isEmpty()){
                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClientePerfil.class);
                        startActivity(intentRestaurant);
                    }else{
                        actualizarCarrito(new Intent(ClienteCarrito.this, ClientePerfil.class));
//                        Intent intentRestaurant = new Intent(ClienteCarrito.this, ClientePerfil.class);
//                        startActivity(intentRestaurant);
                    }
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
    public void actualizarCarrito(Intent intent){
        List<Platillo> listaActualizadaPlatos = adapter.getListaPlatosCarrito();
        List<Integer> listaActualizadaCantidades = adapter.getCantidades();
        ArrayList<String> idListaPlatos = new ArrayList<>();
        for (Platillo platillo : listaActualizadaPlatos) {
            idListaPlatos = new ArrayList<>();
            idListaPlatos.add(platillo.getId());
        }
        carrito.setIdListaPlatos(idListaPlatos);
        carrito.setListaCantidades(new ArrayList<>(listaActualizadaCantidades));
        db.collection("Carritos")
                .document(user.getUid())
                .set(carrito)
                .addOnSuccessListener(unused -> {
                    Log.d("msg-test","Data guardada exitosamente");
                    startActivity(intent);
                })
                .addOnFailureListener(e -> e.printStackTrace());
    }

}