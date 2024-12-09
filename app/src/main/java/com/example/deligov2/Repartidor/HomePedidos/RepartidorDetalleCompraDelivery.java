package com.example.deligov2.Repartidor.HomePedidos;

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
import com.example.deligov2.Beans.Comida;
import com.example.deligov2.Beans.PedidoRepartidor;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RepartidorDetalleCompraDelivery extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    private Pedido pedidoSupreme;
    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        //Cargamos firebase
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        //Cargamos vista
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_detalle_compra_delivery);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Seteamos los valores
        loadUser(()->{
            loadPedidos(getIntent().getStringExtra("pedido") , ()->{
                ((TextView)findViewById(R.id.totalProductos)).setText("Total de productos: " + pedidoSupreme.getIdListaPlatos().size());
                ((MaterialButton)findViewById(R.id.btn_estado)).setText(pedidoSupreme.getEstado());
                ((TextView)findViewById(R.id.direccion)).setText(pedidoSupreme.getDireccion());
                ((TextView)findViewById(R.id.ola)).setText("Precio por delivery: " + 20);
                Float sum =  new Float(0);
                for(Integer j =0 ; j<pedidoSupreme.getIdListaPlatos().size(); j++){
                    sum = sum +  pedidoSupreme.getPreciosActuales().get(j) * pedidoSupreme.getListaCantidades().get(j);
                }
                ((TextView)findViewById(R.id.id_costo)).setText("Costo: " + sum);
                obtenerPlatillos(pedidoSupreme.getIdListaPlatos(), ()->{
                    RepartidorDetalleComidaAdapter adapter = new RepartidorDetalleComidaAdapter();
                    adapter.setContext(this);
                    adapter.setLista(convertComida(pedidoSupreme.getPreciosActuales() , devolverPlatos(pedidoSupreme.getIdListaPlatos()) ,  pedidoSupreme.getListaCantidades()));
                    RecyclerView recyclerView = findViewById(R.id.lista);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                });
                //Empezamos con el adapter

            });
        });
    }

    public void retroceder(View view) {
        onBackPressed();
    }
    public void rechazarPedido(View view){
        Intent  intent =  new Intent(this , RepartidorCancelacionPedido.class );
        finish();
        startActivity(intent);
    }
    public void loadUser(Runnable runnable){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                runnable.run();
                            }
                        }
                    }
                });
    }
    public void loadPedidos(String idPedido, Runnable run){
        db.collection("Pedidos")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            Pedido pedido = document.toObject(Pedido.class);
                            if(pedido.getId().equals(idPedido)){
                                pedidoSupreme =  pedido;
                                run.run();
                            }
                        }
                    }
                });
    }
    ArrayList<Platillo> listaPlatillos = new ArrayList<>() ;

    public void obtenerPlatillos(ArrayList<String> listaIds, Runnable runnable){
        for (int i = 0 ; i < listaIds.size() ; i++){
            db.collection("Platos")
                    .addSnapshotListener((value, error) -> {
                        if (value != null) {
                            for (QueryDocumentSnapshot document : value) {
                                Platillo plato = document.toObject(Platillo.class);
                                if(listaIds.contains( plato.getId()) ){
                                    listaPlatillos.add(plato);
                                }
                            }
                            runnable.run();
                        }
                    });
        }
    }

    public ArrayList<String> devolverPlatos(ArrayList<String> ids){
        ArrayList<String> nuevo = new ArrayList<>();
        for(int i =0 ; i < ids.size() ; i++){
            //Buscamos en el arreglo
            for( Platillo p :  listaPlatillos){
                if(p.getId().equals(ids.get(i))){
                    nuevo.add(p.getNombre());
                    break;
                }
            }
        }
        return nuevo;
    }
    public List<Comida> convertComida(ArrayList<Float> listaPrecios, ArrayList<String> idComidas , ArrayList<Integer> listaCantidades){
        List<Comida> lista = new ArrayList<>();
        for (int i = 0 ; i < listaCantidades.size() ;  i++){
            Comida comida = new Comida();
            comida.setNombreComida(idComidas.get(i));
            comida.setCantidad(listaCantidades.get(i));
            comida.setIdComida(i);
            lista.add(comida);
        }
        return lista;
    }
}