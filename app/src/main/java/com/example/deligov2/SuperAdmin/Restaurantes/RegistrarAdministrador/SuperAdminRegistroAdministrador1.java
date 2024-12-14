package com.example.deligov2.SuperAdmin.Restaurantes.RegistrarAdministrador;

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

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

public class SuperAdminRegistroAdministrador1 extends AppCompatActivity {

    Spinner tipoDocumento;
    private TextInputEditText nameAdmin;
    private TextInputEditText apellidoAdmin;
    private TextInputEditText numDocumentAdmin;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_registro_administrador1);
        db = FirebaseFirestore.getInstance();
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");

        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminVistaLogEvent.class);
//                    startActivity(intent);
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRegistroAdministrador1.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
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

        Restaurante resR = (Restaurante) intent.getSerializableExtra("nr1");

        Log.d("ID RESTAURANTEEE FIREBASEEE", "ID: "+resR.getId());
        /*
        String nameR = intent.getStringExtra("nr1");
        Log.d("Registro Admin 1", "Nombre del restaurante: " + nameR);
        String cNameR;
        if(nameR != null){
            cNameR = nameR;
        }else{
            cNameR = "Nombre no disponible";
        }

         */
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
                vistaRegistroAdmin2(resR,name,apellido,dni);


            }
        });

    }

    public void vistaRegistroAdmin2(Restaurante resR, String name, String apellido, String num){
        Usuario ad = new Usuario();
        ad.setNumDocument(num);
        ad.setNombre(name);
        ad.setApellido(apellido);
        ad.setRestaurante(resR.getId());
        ad.setTipoDocumento("DNI");//POR CORREGIR
        Intent intent = new Intent(this, SuperAdminRegistroAdministrador2.class);
        intent.putExtra("nr2",resR);
        intent.putExtra("admin",ad);
        Log.d("ADMINiSTRADORRR", "nombre: "+name+apellido+num);
        startActivity(intent);
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }
}