package com.example.deligov2.LogIn;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.R;

import java.util.Calendar;

public class LoginCrearCuentaPrimerPaso extends AppCompatActivity {

    private EditText birthdayField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crear_cuenta_primer_paso);

        // Encuentra la vista del campo de cumpleaños
        birthdayField = findViewById(R.id.birthday);

        // Configura el evento de clic en el campo de fecha
        birthdayField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        Button continuarButton = findViewById(R.id.continuar1);
       // continuarButton.setEnabled(false);




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

    public void verLoginCrearCuentaSegundoPaso(View view) {
        // Código existente para ir a la siguiente pantalla
        startActivity(new Intent(this, LoginCrearCuentaSegundoPaso.class));
    }
}
