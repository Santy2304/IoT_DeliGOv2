package com.example.deligov2.LogIn.InicioSesion;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Administrador.AdministradorRestauranteActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.LogIn.RecuperarContra.LoginRecuperarPasswordPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaCuartoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaEmailPassword;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaSegundoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaTercerPaso;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.HomePedidos.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
public class LoginInicioActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_POST_NOTIFICATIONS = 1;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    private List<Usuario> userList;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 1001; // Código de solicitud para Google Sign-In
    private static final String TAG = "GoogleSignIn";
    private TextInputEditText emailInput, passwordInput;
    private TextInputLayout emailLayout, passwordLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        userList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_inicio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_CODE_POST_NOTIFICATIONS);
            }
        }
        redireccion();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Obtén el ID del cliente desde google-services.json
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        Button googleSignInButton = findViewById(R.id.IniciarSesionGoogle);
        googleSignInButton.setOnClickListener(v -> signInWithGoogle());
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        emailLayout = findViewById(R.id.correo);
        passwordLayout = findViewById(R.id.contrasena);
    }
    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                // Obtén la cuenta de Google desde el intent
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account);
                }
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
                Toast.makeText(this, "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        Log.d(TAG, "signInWithCredential:success");
                        user = FirebaseAuth.getInstance().getCurrentUser();
                        //Una vez autenticado
                        if(user!=null){
                            user.reload().addOnCompleteListener(task2 -> {
                                if (user.isEmailVerified()) {
                                    db.collection("Usuarios").document(user.getUid()).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                                    assert usuario != null;
                                                    if(usuario.getRol().equals("Administrador")){
                                                        goAdmin();
                                                    }else {
                                                        if (usuario.getNumeroTelefono() != null) {
                                                            if (usuario.getDireccion() != null) {
                                                                if (usuario.getFotoUrl() != null) {
                                                                    if (usuario.getRol() != null) {
                                                                        if(usuario.isEstado()){
                                                                            if (usuario.getRol().equals("Cliente")) {
                                                                                goCliente();
                                                                            } else if (usuario.getRol().equals("Repartidor")) {
                                                                                goRepartidor();
                                                                            } else if (usuario.getRol().equals("Administrador")) {
                                                                                goAdmin();
                                                                            } else {
                                                                                goSuper(usuario);
                                                                            }
                                                                        }else{
                                                                            //ESTAS BANEADO
                                                                            showBannedUserAlert();
                                                                        }
                                                                    } else {
                                                                        Intent intent = new Intent(this, LoginCrearCuentaCuartoPaso.class);
                                                                        startActivity(intent);
                                                                    }
                                                                } else {
                                                                    Intent intent = new Intent(this, LoginCrearCuentaTercerPaso.class);
                                                                    startActivity(intent);
                                                                }
                                                            } else {
                                                                Intent intent = new Intent(this, LoginCrearCuentaSegundoPaso.class);
                                                                startActivity(intent);
                                                            }
                                                        } else {
                                                            Intent intent = new Intent(this, LoginCrearCuentaPrimerPaso.class);
                                                            startActivity(intent);
                                                        }
                                                    }

                                                }else{
                                                    guardarUsuarioEnBaseDeDatos(user);
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                                    Log.d("msg-test", "Firebase uid: " + user.getUid());
                                }
                            });
                        }
                    } else {
                        // Falló el inicio de sesión
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(this, "Error en la autenticación", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void goCliente(){
        Intent intent = new Intent(this, ClienteHomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void goRepartidor(){
        Intent intent = new Intent(this, RepartidorVistaHome.class);
        startActivity(intent);
        finish();
    }
    public void goAdmin(){
        Intent intent = new Intent(this, AdministradorRestauranteActivity.class);
        startActivity(intent);
        finish();
    }
    public void goSuper(Usuario user){
        Intent intent = new Intent(this, SuperAdminHomeActivity.class);
        intent.putExtra("sa",user);
        Log.d("PROBANDO 123","LOGIN OLA" + user.getId()+"-"+user.getNombre());
        startActivity(intent);
        finish();
    }
    public void loadUsers(){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null) {
                        if(userList !=null){
                            userList.clear();
                        }
                        // Limpiar la lista antes de agregar nuevos datos
                        if(value != null){
                            for (QueryDocumentSnapshot document : value) {
                                Usuario user2 = document.toObject(Usuario.class);
                                userList.add(user2); // Agregar usuario a la lista
                            }
                        }
                    }
                });
    }
    public void redireccion(){
        if(user!=null){
            user.reload().addOnCompleteListener(task -> {
                if(user.isEmailVerified()){
                    Log.d("msg-test", "Firebase uid: " + user.getUid());
                    db.collection("Usuarios").document(user.getUid()).get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                    assert usuario != null;
                                    if (usuario.getNumeroTelefono() != null) {
                                        if (usuario.getDireccion() != null) {
                                            if (usuario.getFotoUrl() != null) {
                                                if (usuario.getRol() != null) {
                                                    if(usuario.isEstado()){
                                                        if (usuario.getRol().equals("Cliente")) {
                                                            goCliente();
                                                        } else if (usuario.getRol().equals("Repartidor")) {
                                                            if(usuario.getAprobado().equals("Aceptado")){
                                                                goRepartidor();
                                                            }else if (usuario.getAprobado().equals("PorValidar")) {
                                                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                                // Configura el título y el mensaje
                                                                builder.setTitle("Esperar confirmación");
                                                                builder.setMessage("Tu cuenta todavía no ha sido aceptada. Por favor, contacta al soporte para más información.");

                                                                // Botón para cerrar el diálogo
                                                                builder.setPositiveButton("Aceptar", (dialog, which) -> {
                                                                    // Opcional: Cierra la aplicación o redirige al usuario a la pantalla de soporte
                                                                    dialog.dismiss();

                                                                });

                                                                // Botón adicional (opcional), como "Contacto"
                                                                builder.setNegativeButton("Contactar Soporte", (dialog, which) -> {
                                                                    // Redirige al usuario a un soporte externo (correo, página, etc.)
                                                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                                    intent.setData(Uri.parse("mailto:hineill.cespedes@pucp.edu.pe"));
                                                                    startActivity(Intent.createChooser(intent, "Contactar Soporte"));
                                                                });

                                                                // Crear y mostrar el diálogo
                                                                AlertDialog dialog = builder.create();
                                                                dialog.setCancelable(false); // Evita que el usuario lo cierre sin interactuar
                                                                dialog.show();
                                                                AuthUI.getInstance().signOut(this)
                                                                        .addOnCompleteListener(task1 -> {

                                                                        });
                                                            }else if(usuario.getAprobado().equals("Rechazado")){
                                                                AuthUI.getInstance().signOut(this)
                                                                        .addOnCompleteListener(task1 -> {
                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                                            // Configura el título y el mensaje
                                                                            builder.setTitle("RECHAZADO");
                                                                            builder.setMessage("Tu cuenta fue rechazada de esta página . Por favor, contacta al soporte para más información.");

                                                                            // Botón para cerrar el diálogo
                                                                            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                                                                                // Opcional: Cierra la aplicación o redirige al usuario a la pantalla de soporte
                                                                                dialog.dismiss();

                                                                            });

                                                                            // Botón adicional (opcional), como "Contacto"
                                                                            builder.setNegativeButton("Contactar Soporte", (dialog, which) -> {
                                                                                // Redirige al usuario a un soporte externo (correo, página, etc.)
                                                                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                                                intent.setData(Uri.parse("mailto:hineill.cespedes@pucp.edu.pe"));
                                                                                startActivity(Intent.createChooser(intent, "Contactar Soporte"));
                                                                            });

                                                                            // Crear y mostrar el diálogo
                                                                            AlertDialog dialog = builder.create();
                                                                            dialog.setCancelable(false); // Evita que el usuario lo cierre sin interactuar
                                                                            dialog.show();
                                                                        });
                                                            }                                                        } else if (usuario.getRol().equals("Administrador")) {
                                                            //update de su id con su uid

                                                            goAdmin();
                                                        } else {
                                                            goSuper(usuario);
                                                        }
                                                    }else{
                                                        //ESTAS BANEADO
                                                        showBannedUserAlert();
                                                    }
                                                } else {
                                                    Intent intent = new Intent(this, LoginCrearCuentaCuartoPaso.class);
                                                    startActivity(intent);
                                                }
                                            } else {
                                                Intent intent = new Intent(this, LoginCrearCuentaTercerPaso.class);
                                                startActivity(intent);
                                            }
                                        } else {
                                            Intent intent = new Intent(this, LoginCrearCuentaSegundoPaso.class);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Intent intent = new Intent(this, LoginCrearCuentaPrimerPaso.class);
                                        startActivity(intent);
                                    }
                                }
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));
                }else {
                    user.sendEmailVerification().addOnCompleteListener(task1 -> {
                        Toast.makeText(this,"Se le ha enviado un correo para validar la cuenta",Toast.LENGTH_SHORT).show();
                    });
                }
            });

        }else {
            Log.d("msg-test", "user es nulo");
        }
    }
    public void forgotPassword(View view){
        startActivity(new Intent(this, LoginRecuperarPasswordPrimerPaso.class));
    }
    public void iniciarSesion(View view) {
        // Obtén los valores de los campos de texto
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        // Limpia errores previos
        emailLayout.setError(null);
        passwordLayout.setError(null);
        // Validación de campos
        if (email.isEmpty()) {
            emailLayout.setError("Por favor, ingresa tu correo electrónico.");
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Por favor, ingresa tu contraseña.");
            return;
        }
        // Autenticar usuario con Firebase
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Inicio de sesión exitoso
                        user = firebaseAuth.getCurrentUser();
                        if(user!=null){
                            user.reload().addOnCompleteListener(task2 -> {
                                if (user.isEmailVerified()) {
                                    db.collection("Usuarios").document(user.getUid()).get()
                                            .addOnSuccessListener(documentSnapshot -> {
                                                if (documentSnapshot.exists()) {
                                                    Usuario usuario = documentSnapshot.toObject(Usuario.class);
                                                    assert usuario != null;
                                                    if (usuario.getNumeroTelefono() != null) {
                                                        if (usuario.getDireccion() != null) {
                                                            if (usuario.getFotoUrl() != null) {
                                                                if (usuario.getRol() != null) {
                                                                    if(usuario.isEstado()){
                                                                        if (usuario.getRol().equals("Cliente")) {
                                                                            goCliente();
                                                                        } else if (usuario.getRol().equals("Repartidor")) {
                                                                            if(usuario.getAprobado().equals("Aceptado")){
                                                                                goRepartidor();
                                                                            }else if (usuario.getAprobado().equals("PorValidar")) {
                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                                                // Configura el título y el mensaje
                                                                                builder.setTitle("Esperar confirmación");
                                                                                builder.setMessage("Tu cuenta todavía no ha sido aceptada. Por favor, contacta al soporte para más información.");

                                                                                // Botón para cerrar el diálogo
                                                                                builder.setPositiveButton("Aceptar", (dialog, which) -> {
                                                                                    // Opcional: Cierra la aplicación o redirige al usuario a la pantalla de soporte
                                                                                    dialog.dismiss();

                                                                                });

                                                                                // Botón adicional (opcional), como "Contacto"
                                                                                builder.setNegativeButton("Contactar Soporte", (dialog, which) -> {
                                                                                    // Redirige al usuario a un soporte externo (correo, página, etc.)
                                                                                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                                                    intent.setData(Uri.parse("mailto:hineill.cespedes@pucp.edu.pe"));
                                                                                    startActivity(Intent.createChooser(intent, "Contactar Soporte"));
                                                                                });

                                                                                // Crear y mostrar el diálogo
                                                                                AlertDialog dialog = builder.create();
                                                                                dialog.setCancelable(false); // Evita que el usuario lo cierre sin interactuar
                                                                                dialog.show();
                                                                                AuthUI.getInstance().signOut(this)
                                                                                        .addOnCompleteListener(task1 -> {

                                                                                        });
                                                                            }else if(usuario.getAprobado().equals("Rechazado")){
                                                                                AuthUI.getInstance().signOut(this)
                                                                                        .addOnCompleteListener(task1 -> {
                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                                                                                            // Configura el título y el mensaje
                                                                                            builder.setTitle("RECHAZADO");
                                                                                            builder.setMessage("Tu cuenta fue rechazada de esta página . Por favor, contacta al soporte para más información.");

                                                                                            // Botón para cerrar el diálogo
                                                                                            builder.setPositiveButton("Aceptar", (dialog, which) -> {
                                                                                                // Opcional: Cierra la aplicación o redirige al usuario a la pantalla de soporte
                                                                                                dialog.dismiss();

                                                                                            });

                                                                                            // Botón adicional (opcional), como "Contacto"
                                                                                            builder.setNegativeButton("Contactar Soporte", (dialog, which) -> {
                                                                                                // Redirige al usuario a un soporte externo (correo, página, etc.)
                                                                                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                                                                                intent.setData(Uri.parse("mailto:hineill.cespedes@pucp.edu.pe"));
                                                                                                startActivity(Intent.createChooser(intent, "Contactar Soporte"));
                                                                                            });

                                                                                            // Crear y mostrar el diálogo
                                                                                            AlertDialog dialog = builder.create();
                                                                                            dialog.setCancelable(false); // Evita que el usuario lo cierre sin interactuar
                                                                                            dialog.show();
                                                                                        });
                                                                            }
                                                                        } else if (usuario.getRol().equals("Administrador")) {
                                                                            goAdmin();
                                                                        } else {
                                                                            goSuper(usuario);
                                                                        }
                                                                    }else{
                                                                        //ESTAS BANEADO
                                                                        showBannedUserAlert();
                                                                    }
                                                                } else {
                                                                    Intent intent = new Intent(this, LoginCrearCuentaCuartoPaso.class);
                                                                    startActivity(intent);
                                                                }
                                                            } else {
                                                                Intent intent = new Intent(this, LoginCrearCuentaTercerPaso.class);
                                                                startActivity(intent);
                                                            }
                                                        } else {
                                                            Intent intent = new Intent(this, LoginCrearCuentaSegundoPaso.class);
                                                            startActivity(intent);
                                                        }
                                                    } else {
                                                        Intent intent = new Intent(this, LoginCrearCuentaPrimerPaso.class);
                                                        startActivity(intent);
                                                    }
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                                    Log.d("msg-test", "Firebase uid: " + user.getUid());
                                }
                            });
                        }
                    } else {
                        // Fallo en el inicio de sesión
                        handleFirebaseAuthError(task.getException());
                    }
                });
    }
    private void handleFirebaseAuthError(Exception exception) {
        if (exception == null) {
            emailLayout.setError("Ocurrió un error desconocido.");
            return;
        }

        String errorMessage = exception.getMessage();
        if (errorMessage != null) {
            if (errorMessage.contains("There is no user record")) {
                emailLayout.setError("No existe una cuenta con este correo.");
            } else if (errorMessage.contains("The password is invalid")) {
                passwordLayout.setError("Contraseña incorrecta.");
            } else if (errorMessage.contains("A network error")) {
                emailLayout.setError("Error de red. Verifica tu conexión.");
            } else {
                emailLayout.setError("Error: " + errorMessage);
            }
        }
    }
    public void CrearCuenta(View view){
        startActivity(new Intent( this , LoginCrearCuentaEmailPassword.class));
    }
    private void guardarUsuarioEnBaseDeDatos(FirebaseUser user) {
        // Obtener una referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Usuario usu = new Usuario();
        usu.setId(user.getUid());
        usu.setCorreo(user.getEmail());
        String[] nombresApellidos = user.getDisplayName().split(" ");
        usu.setNombre(nombresApellidos[0]);
        usu.setApellido(nombresApellidos.length > 1 ? nombresApellidos[1] : "");
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
    private void showBannedUserAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Configura el título y el mensaje
        builder.setTitle("Cuenta Suspendida");
        builder.setMessage("Tu cuenta ha sido baneada. Por favor, contacta al soporte para más información.");

        // Botón para cerrar el diálogo
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Opcional: Cierra la aplicación o redirige al usuario a la pantalla de soporte
            dialog.dismiss();

        });

        // Botón adicional (opcional), como "Contacto"
        builder.setNegativeButton("Contactar Soporte", (dialog, which) -> {
            // Redirige al usuario a un soporte externo (correo, página, etc.)
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:hineill.cespedes@pucp.edu.pe"));
            startActivity(Intent.createChooser(intent, "Contactar Soporte"));
        });

        // Crear y mostrar el diálogo
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false); // Evita que el usuario lo cierre sin interactuar
        dialog.show();
        AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(task1 -> {

                });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso otorgado
                Log.d("Permiso", "Permiso de notificaciones concedido.");
            } else {
                // Permiso denegado
                Log.d("Permiso", "Permiso de notificaciones denegado.");
            }
        }
    }
}