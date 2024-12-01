package com.example.deligov2.LogIn.Registro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Calendar;

import android.widget.Toast;

// Clase LoginCrearCuentaPrimerPaso
public class LoginCrearCuentaPrimerPaso extends AppCompatActivity {
    private EditText numeroDocumento, telefono, birthdayField;
    private Spinner spinnerDocs;
    private Button continuarButton;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crear_cuenta_primer_paso);
        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        // Referencias a vistas
        numeroDocumento = findViewById(R.id.numeroDocumentoId);
        telefono = findViewById(R.id.telefonoText);
        birthdayField = findViewById(R.id.birthday);
        spinnerDocs = findViewById(R.id.spinnerDocs);
        continuarButton = findViewById(R.id.continuar1);
        // Configurar el selector de fecha
        birthdayField.setOnClickListener(v -> showDatePicker());
        // Configurar el botón continuar
        continuarButton.setOnClickListener(v -> {
            if (validarFormulario()) {
                guardarDatosEnFirestore();
            }
        });
    }

    // Método para validar el formulario
    private boolean validarFormulario() {
        boolean esValido = true;

        // Validar Spinner
        if (spinnerDocs.getSelectedItemPosition() == 0) {
            Toast.makeText(this, "Seleccione un tipo de documento.", Toast.LENGTH_SHORT).show();
            esValido = false;
        }

        // Validar número de documento
        String documento = numeroDocumento.getText().toString().trim();
        if (documento.isEmpty()) {
            numeroDocumento.setError("El número de documento es obligatorio.");
            esValido = false;
        } else if (!documento.matches("\\d+")) {
            numeroDocumento.setError("El número de documento solo debe contener números.");
            esValido = false;
        }

        // Validar teléfono
        String telefonoTexto = telefono.getText().toString().trim();
        if (telefonoTexto.isEmpty()) {
            telefono.setError("El número de teléfono es obligatorio.");
            esValido = false;
        } else if (!telefonoTexto.matches("\\d{9}")) {
            telefono.setError("El número de teléfono debe tener 9 dígitos.");
            esValido = false;
        }

        // Validar fecha de nacimiento
        String fechaNacimiento = birthdayField.getText().toString().trim();
        if (fechaNacimiento.isEmpty()) {
            birthdayField.setError("La fecha de nacimiento es obligatoria.");
            esValido = false;
        } else if (!esFechaValida(fechaNacimiento)) {
            birthdayField.setError("La fecha de nacimiento no puede ser en el futuro.");
            Toast.makeText(this, "Debe ingresar una fecha de nacimiento válida", Toast.LENGTH_SHORT).show();
            esValido = false;
        }

        return esValido;
    }

    // Método para mostrar el selector de fecha
    private void showDatePicker() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String formattedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear);
                    birthdayField.setText(formattedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    // Verificar si la fecha es válida
    private boolean esFechaValida(String fecha) {
        try {
            String[] partes = fecha.split("/");
            int dia = Integer.parseInt(partes[0]);
            int mes = Integer.parseInt(partes[1]);
            int año = Integer.parseInt(partes[2]);

            Calendar fechaSeleccionada = Calendar.getInstance();
            fechaSeleccionada.set(año, mes - 1, dia);

            return !fechaSeleccionada.after(Calendar.getInstance());
        } catch (Exception e) {
            return false;
        }
    }

    // Guardar datos en Firestore
    private void guardarDatosEnFirestore() {
        usuario.setTipoDocumento(spinnerDocs.getSelectedItem().toString());
        usuario.setNumDocument(numeroDocumento.getText().toString());
        usuario.setNumeroTelefono(telefono.getText().toString());
        usuario.setDate(birthdayField.getText().toString());
        db.collection("Usuarios")
                .document(usuario.getId())
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    irAlSiguientePaso(usuario);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al registrar usuario", e);
                    Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
                });
    }

    // Ir al siguiente paso
    private void irAlSiguientePaso(Usuario usuario) {
        Intent intent = new Intent(LoginCrearCuentaPrimerPaso.this, LoginCrearCuentaSegundoPaso.class);
        intent.putExtra("usuario", usuario);
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

