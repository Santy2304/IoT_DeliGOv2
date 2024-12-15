package com.example.deligov2.Repartidor.HomePedidos.Confirmaciones;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.ProcesosTracking.RepartidorTrackingEstadoEnCamino;
import com.example.deligov2.Repartidor.ProcesosTracking.RepartidorTrackingEstadoRecibido;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RepartidorAceptacionPedido extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repartidor_aceptacion_pedido);
        String ola =  getIntent().getStringExtra("idPedido");
        TextView textView = findViewById(R.id.texto);
        textView.setText("Ahora quedas a cargo del pedido #" + ola);
    }

    public void verTrackingPedidoRepartidor(View view){
        Intent intent = new Intent(RepartidorAceptacionPedido.this, RepartidorTrackingEstadoEnCamino.class);
        intent.putExtra("idPedido", getIntent().getStringExtra("idPedido"));
        startActivity(intent);
    }

    public void loadUser(){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                            }
                        }
                    }
                });
    }

}