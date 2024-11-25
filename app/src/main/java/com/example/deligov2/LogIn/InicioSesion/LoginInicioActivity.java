package com.example.deligov2.LogIn.InicioSesion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.Administrador.AdministradorHomeActivity;
import com.example.deligov2.Cliente.ClienteHomeActivity;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.LogIn.LoginPrimeraVista;
import com.example.deligov2.LogIn.RecuperarContra.LoginRecuperarPasswordPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaCuartoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaPrimerPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaSegundoPaso;
import com.example.deligov2.LogIn.Registro.LoginCrearCuentaTercerPaso;
import com.example.deligov2.R;
import com.example.deligov2.Repartidor.RepartidorVistaHome;
import com.example.deligov2.SuperAdmin.SuperAdminHomeActivity;
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
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button comenzarButton;
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
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_inicio);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
                        redireccion();
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
        Intent intent = new Intent(this, AdministradorHomeActivity.class);
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

                                    if(usuario.getRol().equals("Cliente")){
                                        goCliente();
                                    } else if (usuario.getRol().equals("Repartidor")) {
                                        goRepartidor();
                                    } else if (usuario.getRol().equals("Administrador")) {
                                        goAdmin();
                                    }else if (usuario.getRol().equals("SuperAdmin")){
                                        goSuper(usuario);
                                    }
                                } else {
                                    Intent intent = new Intent(this,LoginCrearCuentaPrimerPaso.class);
                                    startActivity(intent);
                                    finish();
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
                        redireccion();
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


}