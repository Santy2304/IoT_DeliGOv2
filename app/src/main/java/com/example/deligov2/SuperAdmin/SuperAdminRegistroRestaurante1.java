package com.example.deligov2.SuperAdmin;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuperAdminRegistroRestaurante1 extends AppCompatActivity {
    Spinner tipoCategoria;
    private TextInputEditText tiHinicio;
    private TextInputEditText tiHfin;
    private int hora, minuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_registro_restaurante1);

        /*
        //Para las categorías
        tipoCategoria = (Spinner) view.findViewById(R.id.spinner_categoria);

        tipoCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(getActivity(), "Categoría: " + adapterView.getItemAtPosition(i).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Para la hora inicio y fin pista1:"El tercer perro de un castillo de espadas"

        tiHinicio = (TextInputEditText) view.findViewById(R.id.hora_inicio);
        tiHfin = (TextInputEditText) view.findViewById(R.id.hora_fin);

        tiHinicio.setOnClickListener(this::onClick);
        tiHfin.setOnClickListener(this::onClick);

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = dateFormat.format(new Date());

         */
    }

    /*
    public void onClick(View v){
        if(v==tiHinicio){
            final Calendar calendar = Calendar.getInstance();

            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minuto = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                    tiHinicio.setText(hoursOfDay + ":" + minute);
                }
            }, hora, minuto, false);
            timePickerDialog.show();
        }

        if(v==tiHfin){
            final Calendar calendar = Calendar.getInstance();

            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minuto = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                    tiHfin.setText(hoursOfDay + ":" + minute);
                }
            }, hora, minuto, false);
            timePickerDialog.show();
        }

    }
     */
}