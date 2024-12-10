package com.example.deligov2.LogIn.Registro;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.LogIn.InicioSesion.LoginInicioActivity;
import com.example.deligov2.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


public class LoginCrearCuentaEmailPassword extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crear_cuenta_email_password);
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

    public void irPasoFinal(View view) {
        // Obtén las referencias de los campos
        TextInputLayout emailLayout = findViewById(R.id.correo);
        TextInputLayout passwordLayout = findViewById(R.id.contrasena);
        TextInputLayout confirmPasswordLayout = findViewById(R.id.confirmar_contrasena);
        TextInputLayout nombreLayout = findViewById(R.id.nombre);
        TextInputLayout apellidoLayout = findViewById(R.id.apellido);
        TextInputEditText emailInput = (TextInputEditText) emailLayout.getEditText();
        TextInputEditText passwordInput = (TextInputEditText) passwordLayout.getEditText();
        TextInputEditText confirmPasswordInput = (TextInputEditText) confirmPasswordLayout.getEditText();
        TextInputEditText nombreInput = (TextInputEditText) nombreLayout.getEditText();
        TextInputEditText apellidoInput = (TextInputEditText) apellidoLayout.getEditText();

        // Limpia errores previos
        nombreLayout.setError(null);
        apellidoLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        // Obtén los valores de los campos
        String nombre = nombreInput != null ? nombreInput.getText().toString().trim() : "";
        String apellido = apellidoInput != null ? apellidoInput.getText().toString().trim() : "";
        String email = emailInput != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput != null ? passwordInput.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordInput != null ? confirmPasswordInput.getText().toString().trim() : "";

        // Validación de campos
        boolean isValid = true;

        if (nombre.isEmpty()) {
            nombreLayout.setError("El nombre no puede estar vacío.");
            isValid = false;
        }

        if (apellido.isEmpty()) {
            apellidoLayout.setError("El apellido no puede estar vacío.");
            isValid = false;
        }

        if (email.isEmpty()) {
            emailLayout.setError("El correo no puede estar vacío.");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Por favor, ingresa un correo válido.");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("La contraseña no puede estar vacía.");
            isValid = false;
        } else if (password.length() < 6) {
            passwordLayout.setError("La contraseña debe tener al menos 6 caracteres.");
            isValid = false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordLayout.setError("Por favor, confirma tu contraseña.");
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            confirmPasswordLayout.setError("Las contraseñas no coinciden.");
            isValid = false;
        }
        // Si todos los campos son válidos
        if (isValid) {
            // Continuar con el flujo
            // Aquí puedes agregar el código para enviar datos a Firebase u otra lógica
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Usuario registrado exitosamente
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                guardarUsuarioEnBaseDeDatos(user , nombre , apellido);
                            }
                        } else if (task.getException() != null) {
                            if (task.getException() instanceof FirebaseAuthWeakPasswordException) {
                                passwordLayout.setError("La contraseña es demasiado débil. Usa al menos 6 caracteres.");
                            } else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                emailLayout.setError("El correo electrónico tiene un formato inválido.");
                            } else if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                emailLayout.setError("Este correo ya está registrado. Usa otro correo o inicia sesión.");
                            } else if (task.getException() instanceof FirebaseNetworkException) {
                                emailLayout.setError("No hay conexión a Internet. Verifica tu red e inténtalo nuevamente.");
                            } else {
                                emailLayout.setError("Error inesperado: " + task.getException().getMessage());
                            }                        }
                    });
            Toast.makeText(this , "Si funciono" , Toast.LENGTH_SHORT).show();
        }
    }
    private void guardarUsuarioEnBaseDeDatos(FirebaseUser user,String nombre, String apellido) {
        // Obtener una referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Usuario usu = new Usuario();
        usu.setId(user.getUid());
        usu.setCorreo(user.getEmail());
        usu.setNombre(nombre);
        usu.setApellido(apellido);
        usu.setEstado(true);
        // Crear un objeto para guardar en Firestore
        // Guardar el usuario en la colección "users"
        db.collection("Usuarios").document(user.getUid())
                .set(usu)
                .addOnSuccessListener(aVoid -> {
                    // Usuario guardado exitosamente
                    Toast.makeText(this, "Registro exitoso. ¡Bienvenido!", Toast.LENGTH_SHORT).show();
                    // Redirigir al usuario a otra pantalla
                    Intent intent = new Intent(this, LoginCrearCuentaPrimerPaso.class); // Cambia HomeActivity a tu actividad principal
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Manejar errores al guardar en Firestore
                    Toast.makeText(this, "Error al guardar en la base de datos: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }



}