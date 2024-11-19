package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.MainActivity;
import com.example.deligov2.R;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class LoginPrimeraVista extends AppCompatActivity {

    Button comenzarButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_primera_vista);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user!=null){
            if(user.isEmailVerified()){
                Log.d("msg-test", "Firebase uid: " + user.getUid());
                go();
            }

        }

        comenzarButton = findViewById(R.id.comenzarButton);

        comenzarButton.setOnClickListener(view -> {
            AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout.Builder(R.layout.login_layout)
                    .setGoogleButtonId(R.id.IniciarSesionButton)
                    .setEmailButtonId(R.id.IniciarSesionGoogle)
                    .build();



            Intent intent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    .setAuthMethodPickerLayout(authMethodPickerLayout)
                    .setTheme(R.style.Base_Theme_DeliGOv2)
                    .setAvailableProviders(Arrays.asList(
                            new AuthUI.IdpConfig.EmailBuilder().build(),
                            new AuthUI.IdpConfig.GoogleBuilder().build()
                    ))
                    .build();

            signInLauncher.launch(intent);
        });


    }
    ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if(user!=null){
                        user.reload().addOnCompleteListener(task -> {
                            if(user.isEmailVerified()){
                                Log.d("msg-test", "Firebase uid: " + user.getUid());
                                go();
                            }else {
                                user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                    Toast.makeText(LoginPrimeraVista.this,"Se le ha enviado un correo para validar la cuenta",Toast.LENGTH_SHORT).show();

                                });
                            }
                        });

                    }else {
                        Log.d("msg-test", "user es nulo");
                    }
                } else {
                    Log.d("msg-test", "Canceló el Log-in");
                }
            }
    );

    public void go(){
        Intent intent = new Intent(LoginPrimeraVista.this, LoginCrearCuentaPrimerPaso.class);
        startActivity(intent);
        finish();
    }
}