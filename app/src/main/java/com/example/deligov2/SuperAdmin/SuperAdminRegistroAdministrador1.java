package com.example.deligov2.SuperAdmin;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SuperAdminRegistroAdministrador1 extends AppCompatActivity {

    Spinner tipoDocumento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_administrador1);

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);

        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        tipoDocumento = (Spinner) findViewById(R.id.spinner);

        tipoDocumento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(SuperAdminRegistroAdministrador1.this, "Tipo de Documento: " + adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Manejo de botones
        Button btContinuar = findViewById(R.id.continuar1);
        TextInputEditText adminName = findViewById(R.id.adminNombre);
        TextInputLayout nameLayout = findViewById(R.id.nameLayout);

        TextInputEditText adminApellido = findViewById(R.id.adminApellido);
        TextInputLayout apellidoLayout = findViewById(R.id.apellidoLayout);

        TextInputEditText adminDni = findViewById(R.id.adminNumeroDocumento);
        TextInputLayout dniLayout = findViewById(R.id.dniLayout);

        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = adminName.getText().toString().trim();
                String apellido = adminApellido.getText().toString().trim();
                String dni = adminDni.getText().toString().trim();

                if (name.isEmpty()) {
                    nameLayout.setError("Complete este campo");
                    nameLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }else{
                    nameLayout.setError(null);
                }

                if (apellido.isEmpty()) {
                    apellidoLayout.setError("Complete este campo");
                    apellidoLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }else{
                    apellidoLayout.setError(null);

                }

                if (dni.isEmpty()) {
                    dniLayout.setError("Complete este campo");
                    dniLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }else{
                    dniLayout.setError(null);
                    if (!(dni.length()==8)) {
                        dniLayout.setError("El documento debe tener 8 digítos");
                        dniLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                        return;
                    }else{
                        dniLayout.setError(null);
                    }
                }

                if(!(name.isEmpty() && apellido.isEmpty() && dni.isEmpty() && dni.length()!=8)){
                    nameLayout.setError(null);
                    apellidoLayout.setError(null);
                    dniLayout.setError(null);
                    vistaRegistroAdmin2();
                }
            }
        });

        Button btCancelar = findViewById(R.id.cancelar1);

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaPanelRestaurante();
            }
        });
    }

    public void vistaRegistroAdmin2(){
        Intent intent = new Intent(this, SuperAdminRegistroAdministrador2.class);
        startActivity(intent);
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }
}