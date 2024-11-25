package com.example.deligov2.LogIn.Registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.LogIn.InicioSesion.LoginInicioActivity;
import com.example.deligov2.R;

public class LoginCrearCuentaEmailPassword extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_crear_cuenta_email_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void retroceder(View view){
        showConfirmationDialog("Confirmación", "¿Estás seguro de que deseas salir , perderás todo tu progreso?" , ()->{
            Intent intent = new Intent(this, LoginInicioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            finish(); // Finaliza el Activity actual
        });
    }
    public void showConfirmationDialog(String title, String message, Runnable onConfirmAction) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Configura el título y mensaje del diálogo
        builder.setTitle(title);
        builder.setMessage(message);

        // Botón de confirmación
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Ejecuta la acción de confirmación
                if (onConfirmAction != null) {
                    onConfirmAction.run();
                }
            }
        });

        // Botón de cancelación
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cierra el diálogo
            }
        });

        // Crea y muestra el diálogo
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}