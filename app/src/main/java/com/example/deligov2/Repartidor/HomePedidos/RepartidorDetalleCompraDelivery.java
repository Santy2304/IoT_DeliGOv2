package com.example.deligov2.Repartidor.HomePedidos;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorAceptacionPedido;
import com.example.deligov2.Repartidor.HomePedidos.Confirmaciones.RepartidorCancelacionPedido;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        setContentView(R.layout.activity_repartidor_detalle_compra_delivery);
        //Seteamos los valores
        loadUser(()->{
            loadPedidos(getIntent().getStringExtra("pedido") , ()->{
                ((TextView)findViewById(R.id.idPedido)).setText("#"+pedidoSupreme.getId());
                ((MaterialButton)findViewById(R.id.btn_estado)).setText(pedidoSupreme.getEstado());
                ((TextView)findViewById(R.id.direccion)).setText(pedidoSupreme.getDireccion());
                ((TextView)findViewById(R.id.ola)).setText("Precio por delivery: " + 20);
                ((ExtendedFloatingActionButton)findViewById(R.id.btn_aceptar)).setContentDescription(pedidoSupreme.getId());

                Float sum =  new Float(0);
                for(Integer j =0 ; j<pedidoSupreme.getIdListaPlatos().size(); j++){
                    sum = sum +  pedidoSupreme.getPreciosActuales().get(j) * pedidoSupreme.getListaCantidades().get(j);
                }
                ((TextView)findViewById(R.id.id_costo)).setText("Costo: " + sum);
                obtenerPlatillos(pedidoSupreme.getIdListaPlatos(), ()->{
                    RepartidorDetalleComidaAdapter adapter = new RepartidorDetalleComidaAdapter();
                    adapter.setContext(this);
                    adapter.setLista(convertComida(pedidoSupreme.getIdListaPlatos() , devolverPlatos(pedidoSupreme.getIdListaPlatos()) ,  pedidoSupreme.getListaCantidades()));
                    RecyclerView recyclerView = findViewById(R.id.lista);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                });
                //Empezamos con el adapter


            });
        });
    }

    public void retroceder(View view) {
        Intent intent = new Intent(this, RepartidorVistaHome.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
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
    public List<Comida> convertComida(ArrayList<String> listaIds, ArrayList<String> idComidas , ArrayList<Integer> listaCantidades){
        List<Comida> lista = new ArrayList<>();
        for (int i = 0 ; i < listaCantidades.size() ;  i++){
            Comida comida = new Comida();
            comida.setNombreComida(idComidas.get(i));
            comida.setCantidad(listaCantidades.get(i));
            comida.setIdComida(listaIds.get(i));
            comida.setIdRestaurante(pedidoSupreme.getIdRestaurante());
            lista.add(comida);
        }
        return lista;
    }

    public void aceptar(String idPedido ,  String idRepartidor , Runnable onsuccess  , Runnable onfailure){
        Map<String, Object> updates = new HashMap<>();
        updates.put("idRepartidor", idRepartidor);
        // Realizar el update
        db.collection("Pedidos")
                .document(idPedido)
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Éxito
                    onsuccess.run();
                })
                .addOnFailureListener(e -> {
                    // Error
                    onfailure.run();});
    }
    public void aceptarPedido(View view ) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Confirmar acción")
                .setMessage("¿Qué acción deseas realizar con esta solicitud?")
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        aceptar(view.getContentDescription().toString(), user.getUid(), () -> {
                                    startActivity(new Intent(RepartidorDetalleCompraDelivery.this, RepartidorAceptacionPedido.class));
                                }, () -> {
                                    Intent intent = new Intent(RepartidorDetalleCompraDelivery.this, RepartidorCancelacionPedido.class);
                                    startActivity(intent);
                                }
                        );
                    }
                })
                .setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción cuando se presiona "Cancelar" (cerrar el diálogo)
                        dialog.dismiss();
                    }
                })
                .setCancelable(true)
                .show();
    }
}