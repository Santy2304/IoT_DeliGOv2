package com.example.deligov2.LogIn.RecuperarContra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.R;
import com.google.firebase.auth.FirebaseAuth;

public class LoginRecuperarPasswordPrimerPaso extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_recuperar_password_primer_paso);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    // Método para retroceder asociado al onClick del botón
    public void retroceder(View view) {
        // Simular comportamiento de retroceso
        onBackPressed();
    }

    public void enviarCorreo(View view) {
        EditText emailEditText = findViewById(R.id.email);
        String email = emailEditText.getText().toString().trim();
        if (email.isEmpty()) {
            Toast.makeText(this, "Por favor, ingresa tu correo.", Toast.LENGTH_SHORT).show();
        } else {
            sendPasswordReset(email);
        }
    }

    private void sendPasswordReset(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Correo de recuperación enviado. Revisa tu bandeja de entrada.", Toast.LENGTH_LONG).show();
                        onBackPressed();
                    } else {
                        Toast.makeText(this, "Error al enviar el correo de recuperación.", Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Este correo no existe", Toast.LENGTH_LONG).show();
                });
    }
}