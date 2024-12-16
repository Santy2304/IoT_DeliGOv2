package com.example.deligov2.Administrador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.deligov2.Cliente.ClientePerfil;
import com.example.deligov2.LogIn.InicioSesion.LoginInicioActivity;
import com.example.deligov2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class AdministradorPerfilActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private ShapeableImageView fotoPerfil;
    private FloatingActionButton logoutButton;
    private MaterialTextView nombre, apellido, restaurante, correo;
    private ActivityResultLauncher<Intent> galleryLauncher, cameraLauncher;
    private Uri imageUri;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_perfil);

        // Inicializar Firebase
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();

        // Obtener referencias a los elementos de la interfaz de usuario
        fotoPerfil = findViewById(R.id.imgPerfil);
        logoutButton = findViewById(R.id.bt_exit);
        nombre = findViewById(R.id.name);
        apellido = findViewById(R.id.apellido);
        restaurante = findViewById(R.id.restaurante);
        correo = findViewById(R.id.correo);

        // Obtener el usuario actual
        userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();

        // Obtener los datos del usuario de la base de datos y cargar en la vista
        cargarDatosUsuario(userId);

        // Launchers para la edición de la foto de perfil
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageUri = selectedImageUri;
                            Glide.with(this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.user_icon)
                                    .into(fotoPerfil);
                            uploadImageToFirebase();
                        }
                    }
                }
        );

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Glide.with(this)
                                .load(imageUri)
                                .placeholder(R.drawable.user_icon)
                                .into(fotoPerfil);
                        uploadImageToFirebase();
                    }
                }
        );

        // Logout
        logoutButton.setOnClickListener(view -> {
            AuthUI.getInstance().signOut(this)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(this, LoginInicioActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar pila
                        startActivity(intent);
                        finish();
                    });
        });

        // Bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorPerfilActivity.this, AdministradorReportesActivity.class);
                    startActivity(intentReportes);
                    return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorPerfilActivity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorPerfilActivity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(AdministradorPerfilActivity.this, AdministradorPerfilActivity.class);
                    startActivity(intentProfile);
                    return true;
                }else {
                    return false;
                }

            }
        });

    }

    private void cargarDatosUsuario(String userId) {
        db.collection("Usuarios").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreStr = documentSnapshot.getString("nombre");
                        String apellidoStr = documentSnapshot.getString("apellido");
                        String correoStr = documentSnapshot.getString("correo");

                        // Cargar los datos en los elementos de la interfaz de usuario
                        nombre.setText(nombreStr);
                        apellido.setText(apellidoStr);
                        correo.setText(correoStr);
                        cargarImagenPerfil(userId);

                        String idRestaurante = documentSnapshot.getString("restaurante");
                        assert idRestaurante != null;
                        db.collection("restaurantes").document(idRestaurante).get()
                                .addOnSuccessListener(restauranteSnapshot -> {
                                    if (restauranteSnapshot.exists()) {
                                        String restauranteStr = restauranteSnapshot.getString("nombre");
                                        restaurante.setText(restauranteStr);
                                    } else {
                                        Log.e("Firestore", "No se encontró el restaurante con ID: " + idRestaurante);
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener restaurante", e));

                    } else {
                        Log.e("Firestore", "No se encontró el usuario con ID: " + userId);
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener usuario", e));
    }

    private void cargarImagenPerfil(String userId) {
        StorageReference imgRef = storage.getReference().child("users/" + userId + "/profile.jpg");
        imgRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.ic_errorimg)
                    .into(fotoPerfil);
        }).addOnFailureListener(e -> {
            Log.e("Storage", "Error al cargar la imagen de perfil", e);
            fotoPerfil.setImageResource(R.drawable.camara_icon);
        });
    }

    @SuppressLint("MissingInflatedId")
    public void showBottomSheetDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_photo, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout btnGallery = bottomSheetView.findViewById(R.id.btn_gallery);
        //LinearLayout btnCamera = bottomSheetView.findViewById(R.id.btn_camera);
        LinearLayout btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
            bottomSheetDialog.dismiss();
        });

        /*btnCamera.setOnClickListener(v -> {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            try {
                File photoFile = createImageFile();
                imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                cameraLauncher.launch(cameraIntent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            bottomSheetDialog.dismiss();
        });*/

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private boolean checkCameraPermission() {

        return true;
    }

    /*private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile;
        try {
            photoFile = createImageFile();
            imageUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraLauncher.launch(cameraIntent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference profileRef = storage.getReference().child("users/" + userId + "/profile.jpg");
            profileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        cargarImagenPerfil(userId);
                        Toast.makeText(this, "Imagen actualizada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }
}