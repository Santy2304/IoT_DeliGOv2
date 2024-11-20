package com.example.deligov2.LogIn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;

public class LoginCrearCuentaPrimerPaso extends AppCompatActivity {

    private EditText birthdayField;
    FirebaseFirestore db;
    FirebaseUser user;
    EditText numeroDocumento, telefono, birthday;
    Spinner spinnerDocs;
    Button continuarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crear_cuenta_primer_paso);
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        numeroDocumento = findViewById(R.id.numeroDocumentoId);
        telefono = findViewById(R.id.telefonoText);
        spinnerDocs = findViewById(R.id.spinnerDocs);
        continuarButton = findViewById(R.id.continuar1);

        // Encuentra la vista del campo de cumpleaños
        birthdayField = findViewById(R.id.birthday);

        // Configura el evento de clic en el campo de fecha
        birthdayField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        continuarButton = findViewById(R.id.continuar1);
        continuarButton.setEnabled(false);

        numeroDocumento.addTextChangedListener(watcher);
        telefono.addTextChangedListener(watcher);
        birthday.addTextChangedListener(watcher);

        // Agrega listener al Spinner
        spinnerDocs.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                checkFields();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        continuarButton.setOnClickListener(view -> {
            Usuario usuario = new Usuario();
            usuario.setCorreo(user.getEmail());
            String[] nameParts = user.getDisplayName().split(" ");
            usuario.setNombre(nameParts[0]);
            usuario.setApellido(nameParts[1]);
            usuario.setDate(birthday.getText().toString());
            usuario.setEstado(true);
            usuario.setNumDocument(numeroDocumento.getText().toString());
            usuario.setNumeroTelefono(telefono.getText().toString());
            usuario.setTipoDocumento(spinnerDocs.getSelectedItem().toString());
            usuario.setId(user.getUid());

            db.collection("Usuarios")
                    .document(usuario.getId())
                    .set(usuario)
                    .addOnSuccessListener(unused -> {
                        Log.d("msg-test","Data guardada exitosamente");
                    })
                    .addOnFailureListener(e -> e.printStackTrace());

            Intent intent = new Intent(LoginCrearCuentaPrimerPaso.this,LoginCrearCuentaSegundoPaso.class);
            intent.putExtra("usuario",usuario);
            startActivity(intent);

        });



    }
    private final TextWatcher watcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            checkFields();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void checkFields() {
        String doc = numeroDocumento.getText().toString().trim();
        String tel = telefono.getText().toString().trim();
        String bday = birthday.getText().toString().trim();
        boolean isSpinnerSelected = spinnerDocs.getSelectedItemPosition() != 0;

        continuarButton.setEnabled(!doc.isEmpty() && !tel.isEmpty() && !bday.isEmpty() && isSpinnerSelected);
    }

    // Método para mostrar el DatePickerDialog
    private void showDatePicker() {
        // Obtener la fecha actual
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Crear y mostrar el DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    // Formatear la fecha seleccionada
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);

                    // Establecer la fecha en el campo de texto
                    birthdayField.setText(formattedDate);
                },
                year, month, day
        );

        datePickerDialog.show();
    }


}
