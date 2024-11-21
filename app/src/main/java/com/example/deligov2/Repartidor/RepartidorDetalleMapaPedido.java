package com.example.deligov2.Repartidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class RepartidorDetalleMapaPedido extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_repartidor_detalle_mapa_pedido);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Recogemos los valores del anterior activity y....
        Intent intent  = getIntent();
        TextView title = findViewById(R.id.title);
        title.setText("Mapa de pedido #" +  intent.getStringExtra("idPedido"));
        TextView destinoTienda = findViewById(R.id.destinoTienda);
        destinoTienda.setText( intent.getStringExtra("DestinoTienda"));
        TextView destinoFinal = findViewById(R.id.destinoFinal);
        destinoFinal.setText( intent.getStringExtra("DestinoFinal"));
        try {
            if (getIntent().getStringExtra("flag").equals("historial")) {
                //ocultamos el boton
                findViewById(R.id.btn_aceptar).setVisibility(View.INVISIBLE);
                findViewById(R.id.btn_aceptar).setClickable(false);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    public void retroceder(View view){
        onBackPressed();
    }

    public void aceptacionRepartidor2(View view )
    {
        Intent intent = new Intent(this, RepartidorAceptacionPedido.class);
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