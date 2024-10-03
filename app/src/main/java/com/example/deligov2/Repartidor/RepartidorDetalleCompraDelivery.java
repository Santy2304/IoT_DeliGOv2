package com.example.deligov2.Repartidor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Adapters.RepartidorDetalleComidaAdapter;
import com.example.deligov2.Adapters.RepartidorPedidosAdapter;
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class RepartidorDetalleCompraDelivery extends AppCompatActivity {

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_detalle_compra_delivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Seteamos los valores
        String id = getIntent().getStringExtra("id");
        PedidoRepartidor pedido  =new PedidoRepartidor();
        ArrayList<PedidoRepartidor> pedidos = llenarDatos();
        for(PedidoRepartidor p : pedidos){
            if(p.getIdPedidoRepartidor().toString().equals(id)){
                pedido = p;
            }
        }
        Button estado = (Button)findViewById(R.id.btn_estado);
        estado.setText("Estado " + pedido.getEstado());
        TextView direccion =  findViewById(R.id.direccion);
        direccion.setText("Destino: " + pedido.getDireccionDelivery());
        TextView precio =  findViewById(R.id.ola);
        precio.setText("Precio por delivery:: " + pedido.getPrecioDelivery());
        TextView costo =  findViewById(R.id.id_costo);
        costo.setText("Costo: " + ( pedido.getPrecioDelivery() +10));
        //Usamos el adapter
        RepartidorDetalleComidaAdapter adapter = new RepartidorDetalleComidaAdapter();
        adapter.setContext(this);
        ArrayList<PedidoRepartidor> lis = llenarDatos();
        ArrayList<PedidoRepartidor> pedidosFiltrado = new ArrayList<>();
        ArrayList<Comida> comidas =  new ArrayList<>();
        for(PedidoRepartidor pes : lis) {
            if(pes.getIdPedidoRepartidor() == pedido.getIdPedidoRepartidor()){
                comidas =  pes.getComida();
            }
        }
        adapter.setLista(comidas);
        RecyclerView recyclerView = findViewById(R.id.lista);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(getIntent().getStringExtra("flag").equals("historial")){
             //ocultamos el boton
            findViewById(R.id.btn_rechazar).setClickable(false);
            findViewById(R.id.btn_rechazar).setVisibility(View.INVISIBLE);
        }
    }

    public void retroceder(View view) {
        onBackPressed();
    }
    public ArrayList<PedidoRepartidor> llenarDatos(){
        ArrayList<PedidoRepartidor> pedidos = new ArrayList<>();

        ArrayList<Comida> comidas1 =  new ArrayList<>();
        ArrayList<Comida> comidas2 =  new ArrayList<>();

        String[]  estados = { "Recibido" , "En preparacion" , "En camino" , "Entregado"};
        String[]  direccionesRestaurantes = { "Av. Javier Prado Este 1234, San Borja" ,
                "Av. Pardo y Aliaga 789, San Isidro"
                , "Calle Los Cedros 987, La Molina" ,
                "Av. La Marina 256, San Miguel"};
        String[]  direccionesDelivery = { "Av. Perú 1456, San Martín de Porres"
                , "Av. Canadá 789, La Victoria"
                , "Calle Las Magnolias 123, San Borja"
                , "Av. Universitaria 1550, Los Olivos"};
        Calendar calendar = Calendar.getInstance();

        Date[]  fechas  = new Date[4];
        for (int i = 0; i < fechas.length; i++) {
            // Añadir i días a la fecha actual
            calendar.add(Calendar.DAY_OF_YEAR, -i*3);
            fechas[i] = calendar.getTime(); // Convertir a Date y almacenar en el array
        }

        comidas1.add(new Comida(1, "Pizza", 2));
        comidas1.add(new Comida(2, "Hamburguesa", 3));
        comidas1.add(new Comida(3, "Helado", 3));
        comidas1.add(new Comida(4, "Pollo", 3));
        comidas1.add(new Comida(5, "Postre", 3));
        comidas1.add(new Comida(6, "Chaufa", 3));
        comidas1.add(new Comida(7, "Cangreburger", 3));

        comidas2.add(new Comida(10, "Tacos", 4));
        comidas2.add(new Comida(20, "Ensalada", 1));
        comidas2.add(new Comida(50, "Arraoz chaufa", 5));
        comidas2.add(new Comida(24, "Pescado frito", 3));
        comidas2.add(new Comida(34, "Pollo frito", 2));
        comidas2.add(new Comida(24, "Pescado frito", 3));
        comidas2.add(new Comida(34, "Pollo frito", 2));

        for(int i=20 ; i<100 ;  i++){
            PedidoRepartidor p =  new PedidoRepartidor();
            p.setIdPedidoRepartidor(i);
            if(i%4==0){
                p.setEstado(estados[0]);
                p.setDireccionRestaurante(direccionesRestaurantes[1]);
                p.setDireccionDelivery(direccionesDelivery[2]);
                p.setComida(comidas1);
                p.setFecha(fechas[1]);
            }
            if(i%4==1){
                p.setEstado(estados[1]);
                p.setDireccionRestaurante(direccionesRestaurantes[3]);
                p.setDireccionDelivery(direccionesDelivery[1]);
                p.setComida(comidas1);
                p.setFecha(fechas[2]);
            }
            if(i%4==2){
                p.setEstado(estados[2]);
                p.setDireccionRestaurante(direccionesRestaurantes[2]);
                p.setDireccionDelivery(direccionesDelivery[3]);
                p.setComida(comidas2);
                p.setFecha(fechas[0]);
            }
            if(i%4==3){
                p.setEstado(estados[3]);
                p.setDireccionRestaurante(direccionesRestaurantes[0]);
                p.setDireccionDelivery(direccionesDelivery[0]);
                p.setComida(comidas2);
                p.setFecha(fechas[2]);
            }
            p.setPrecioDelivery(20F +  (10 * i) );
            pedidos.add(p);
        }
        return pedidos;
    }

    public void rechazarPedido(View view){
        Intent  intent =  new Intent(this , RepartidorCancelacionPedido.class );
        finish();
        startActivity(intent);
    }

}