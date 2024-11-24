package com.example.deligov2.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.ClienteCarritoAdapter;
import com.example.deligov2.Adapters.ClienteHistorialAdapter;
import com.example.deligov2.Beans.Ordenes;
import com.example.deligov2.Beans.Platillo;
import com.example.deligov2.Beans.Restaurante;
import com.example.deligov2.Beans.VentaPlatilloSA;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClienteCarrito extends AppCompatActivity {
    ArrayList<VentaPlatilloSA> lista;
    String[] nombresPlatos = {
            "Hamburguesa Royal",
            "Americana",
            "Tocino con Queso",
            "La Peruana",

    };
    float[] Precios  = {
            8,
            13,
            11,
            15,
    };
    int[] idRestaurante ={
            1,1,1,1
    };

    int[] cantidad = {
            1,1,1,1
    };
    int[] idPlato = {
            1,2,3,4
    };
    FirebaseFirestore db;
    FloatingActionButton notiButton;
    FloatingActionButton returnRestaurant;
    Button orderButton;
    Button vaciarButton;
    private List<Platillo> listaPlatosSeleccionados;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_carrito);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
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

        listaPlatosSeleccionados = (List<Platillo>) getIntent().getSerializableExtra("listaPlatillos");

        if (listaPlatosSeleccionados != null) {
            for (Platillo plato : listaPlatosSeleccionados) {
                VentaPlatilloSA ventaPlatilloSA = new VentaPlatilloSA(1,plato.getNombre(),plato.getPrecio(),1,1);
                lista.add(ventaPlatilloSA);

            }
        }

//
//        lista = new ArrayList<>();
//        for(int i=0;i<4;i++){
//            VentaPlatilloSA ventaPlatilloSA = new VentaPlatilloSA(idPlato[i],nombresPlatos[i],Precios[i],cantidad[i],idRestaurante[i]);
//            lista.add(ventaPlatilloSA);
//        }
        ClienteCarritoAdapter adapter = new ClienteCarritoAdapter();
        adapter.setContext(this);
        adapter.setListaPlatosVentas(lista);

        RecyclerView recyclerView = findViewById(R.id.recy);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        notiButton = findViewById(R.id.noti_button);
        vaciarButton = findViewById(R.id.vaciarButton);
        returnRestaurant = findViewById(R.id.comprarMas);
        orderButton = findViewById(R.id.orderButton);

        notiButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteNotificacionesActivity.class);
            startActivity(intent);
        });

        returnRestaurant.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteRestaurantActivity.class);
            startActivity(intent);
        });

//        orderButton.setOnClickListener(view -> {
//            db.collection("Pedidos")
//                    .document(usuario.getId())
//                    .set(usuario)
//                    .addOnSuccessListener(unused -> {
//                        irAlSiguientePaso(usuario);
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("Firestore", "Error al registrar usuario", e);
//                        Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
//                    });
//            Intent intent = new Intent(this, ClienteConfirmarDireccion.class);
//            startActivity(intent);
//
//        });

        vaciarButton.setOnClickListener(view -> {
            Intent intent = new Intent(this,ClienteHomeActivity.class);
            startActivity(intent);
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
    // Mueve el método aquí
    public void deleteFood(View view) {
        // Necesitas obtener la posición del item que se clicó
        RecyclerView recyclerView = findViewById(R.id.recy);
        int position = recyclerView.getChildAdapterPosition(view);
        ClienteCarritoAdapter adapter = new ClienteCarritoAdapter();

        // Ahora elimina el ítem de la lista en tu Adapter
        // Debes tener acceso a tu lista de datos
        adapter.setListaPlatosVentas((List<VentaPlatilloSA>) adapter.getListaPlatosVentas().remove(position));
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, adapter.getListaPlatosVentas().size());
    }

}