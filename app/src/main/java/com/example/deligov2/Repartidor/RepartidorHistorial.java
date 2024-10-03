package com.example.deligov2.Repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.RepartidorHistorialPedidosAdapter;
import com.example.deligov2.Adapters.RepartidorPedidosAdapter;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.R;

import java.util.ArrayList;

public class RepartidorHistorial extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_historial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RepartidorHistorialPedidosAdapter adapter = new RepartidorHistorialPedidosAdapter();
        adapter.setContext(this);
        adapter.setLista(llenarDatos());
        RecyclerView recyclerView = findViewById(R.id.lista);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    public void verPerfil(View view){
        Intent intent = new Intent(this, PerfilRepartidor.class);
        startActivity(intent);
    }
    public void verNotificacionesRepartidor(View view ){
        Intent intent = new Intent(this, RepartidorNotificaciones.class);
        view.getId();
        startActivity(intent);
    }
    public void retroceder(View view){
        onBackPressed();
    }
    public ArrayList<PedidoRepartidor> llenarDatos(){
        ArrayList<PedidoRepartidor> pedidos = new ArrayList<>();

        for (int i = 30; i <= 48; i++) {
            // Crear una lista de comidas para cada pedido
            ArrayList<Comida> comidas = new ArrayList<>();

            // Asignar comidas fijas
            if (i % 2 == 0) {
                // Para los pedidos pares
                comidas.add(new Comida(1, "Pizza", 2));
                comidas.add(new Comida(2, "Hamburguesa", 3));
                comidas.add(new Comida(3, "Helado", 3));
                comidas.add(new Comida(4, "Pollo", 3));
                comidas.add(new Comida(5, "Postre", 3));
                comidas.add(new Comida(6, "Chaufa", 3));
                comidas.add(new Comida(7, "Cangreburger", 3));

            } else {
                // Para los pedidos impares
                comidas.add(new Comida(10, "Tacos", 4));
                comidas.add(new Comida(20, "Ensalada", 1));
                comidas.add(new Comida(50, "Arraoz chaufa", 5));
                comidas.add(new Comida(24, "Pescado frito", 3));
                comidas.add(new Comida(34, "Pollo frito", 2));
                comidas.add(new Comida(24, "Pescado frito", 3));
                comidas.add(new Comida(34, "Pollo frito", 2));
            }

            String estado = (i % 2 == 0) ? "Recibido" : "En preparación";

            PedidoRepartidor pedido = new PedidoRepartidor();
            pedido.setIdPedidoRepartidor(i);
            pedido.setComida(comidas);
            pedido.setEstado(estado);
            pedido.setDireccionDelivery("Direccion delivery");
            pedido.setDireccionRestaurante("Dirección restaurante");
            pedido.setPrecioDelivery(10F +  10*i);
            pedidos.add(pedido);
        }
        return pedidos;
    }
}