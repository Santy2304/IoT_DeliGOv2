package com.example.deligov2.SuperAdmin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;

public class SuperAdminRegistroRestauranteCorrect extends AppCompatActivity {
    Button btContinuar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_restaurante_correct);

        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Restaurante nameR = (Restaurante) intent.getSerializableExtra("nr");
        Log.d("Restaurante Correct", "Nombre del restaurante: " + nameR.getNombre());


        //Manejo de botones
        btContinuar = (Button) findViewById(R.id.button);
        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaRegistroAdmin1(nameR);
            }
        });
    }

    //Cambio vistas
    public void vistaRegistroAdmin1(Restaurante res){
        Intent intent = new Intent(this, SuperAdminRegistroAdministrador1.class);

        intent.putExtra("nr1",res);
        startActivity(intent);
    }

}