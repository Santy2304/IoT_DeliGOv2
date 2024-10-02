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

import com.example.deligov2.Adapters.RepartidorPedidosAdapter;
import com.example.deligov2.Beans.PedidoPorSolicitar;
import com.example.deligov2.R;

import java.util.ArrayList;
import java.util.Arrays;

public class RepartidorVistaHome extends AppCompatActivity {
    ArrayList<PedidoPorSolicitar> lista;

    int[] listaId ={
            32,21,12,12
    };
    String[] listaState = {
            "Recibido",
            "Por entregar",
            "Recibido",
            "Recibido"};
    float[]  listaPrice = {12.00F, 13.50F , 15.50F , 18.50F  };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_vista_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        lista  =  new  ArrayList<>() ;
        for(int i = 0; i<4; i++){
            PedidoPorSolicitar pedido = new PedidoPorSolicitar();
            pedido.setIdOrder(listaId[i]);
            pedido.setState(listaState[i]);
            pedido.setPrice(listaPrice[i]);
            lista.add(pedido);
        }
        RepartidorPedidosAdapter adapter = new RepartidorPedidosAdapter();
        adapter.setContext(this);
        adapter.setListaPedidosPorSolicitar(lista);
        RecyclerView recyclerView = findViewById(R.id.lista);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(RepartidorVistaHome.this));
    }

    public void verNotificacionesRepartidor(View view ){
        Intent intent = new Intent(RepartidorVistaHome.this, RepartidorNotificaciones.class);
        startActivity(intent);
    }

    public void verDetalleCompraDelivery(View view){
        Intent intent = new Intent(RepartidorVistaHome.this, RepartidorDetalleCompraDelivery.class);
        startActivity(intent);
    }
    public void verDetalleMapaPedido(View view){
        Intent intent = new Intent(RepartidorVistaHome.this, RepartidorDetalleMapaPedido.class);
        startActivity(intent);
    }

}