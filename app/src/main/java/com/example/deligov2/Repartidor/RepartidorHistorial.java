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
import java.util.Calendar;
import java.util.Date;

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
        ArrayList<PedidoRepartidor> listaFiltrada = new ArrayList<>();
        for(PedidoRepartidor p :  llenarDatos()){
            if(p.getEstado().equals("Entregado") || p.getEstado().equals("Recibido")){
                listaFiltrada.add(p);
            }
        }
        adapter.setLista(listaFiltrada);
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

    public void verDetalleCompraDelivery(View view){
        Intent intent = new Intent(this, RepartidorDetalleCompraDelivery.class);
        //Antes de lanzar un activity seteemos algunos valores;
        View ver = (View) view.getParent().getParent().getParent();
        int id =  ver.getId();
        PedidoRepartidor pedido  =new PedidoRepartidor();
        ArrayList<PedidoRepartidor> pedidos = llenarDatos();
        for(PedidoRepartidor p : pedidos){
            if(p.getIdPedidoRepartidor() == id){
                pedido = p;
            }
        }
        intent.putExtra("id" ,pedido.getIdPedidoRepartidor().toString());
        intent.putExtra("flag" , "historial");
        startActivity(intent);
    }
    public void verDetalleMapaPedido(View view){
        Intent intent = new Intent(this, RepartidorDetalleMapaPedido.class);
        View ver = (View) view.getParent().getParent().getParent();
        int id =  ver.getId();
        PedidoRepartidor pedido  =new PedidoRepartidor();
        ArrayList<PedidoRepartidor> pedidos = llenarDatos();
        for(PedidoRepartidor p : pedidos){
            if(p.getIdPedidoRepartidor() == id){
                pedido = p;
            }
        }
        intent.putExtra("idPedido",pedido.getIdPedidoRepartidor().toString());
        intent.putExtra("DestinoTienda" , pedido.getDireccionRestaurante());
        intent.putExtra("DestinoFinal",pedido.getDireccionDelivery());
        intent.putExtra("flag" , "historial");
        startActivity(intent);
    }
}