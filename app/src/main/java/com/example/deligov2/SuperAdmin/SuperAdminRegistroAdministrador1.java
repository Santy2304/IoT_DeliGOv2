package com.example.deligov2.SuperAdmin;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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

import com.example.deligov2.Beans.Administrador;
import com.example.deligov2.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class SuperAdminRegistroAdministrador1 extends AppCompatActivity {

    Spinner tipoDocumento;
    private TextInputEditText nameAdmin;
    private TextInputEditText apellidoAdmin;
    private TextInputEditText numDocumentAdmin;

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
        //Manejo de datos
        nameAdmin = findViewById(R.id.adminNombre);
        apellidoAdmin = findViewById(R.id.adminApellido);
        numDocumentAdmin =findViewById(R.id.adminNumeroDocumento);

        String nameA = nameAdmin.getText().toString().trim();
        String apellidoA = apellidoAdmin.getText().toString().trim();
        String numA = numDocumentAdmin.getText().toString().trim();
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        String nameR = intent.getStringExtra("nr1");
        Log.d("Registro Admin 1", "Nombre del restaurante: " + nameR);
        String cNameR;
        if(nameR != null){
            cNameR = nameR;
        }else{
            cNameR = "Nombre no disponible";
        }

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
                }

                if (apellido.isEmpty()) {
                    apellidoLayout.setError("Complete este campo");
                    apellidoLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
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
                    }
                }

                nameLayout.setError(null);
                apellidoLayout.setError(null);
                dniLayout.setError(null);
                vistaRegistroAdmin2(cNameR,nameA,apellidoA,numA);


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

    public void vistaRegistroAdmin2(String nameR, String name, String apellido, String num){
        Intent intent = new Intent(this, SuperAdminRegistroAdministrador2.class);
        intent.putExtra("nr2",nameR);
        intent.putExtra("adminName",name);
        intent.putExtra("adminApellido",apellido);
        intent.putExtra("adminDoc",num);
        startActivity(intent);
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }
}