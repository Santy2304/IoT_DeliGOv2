package com.example.deligov2.LogIn;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class LoginCrearCuentaTercerPaso extends AppCompatActivity {
    private ImageView imageView;
    private Uri selectedImageUri;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_crear_cuenta_tercer_paso);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        imageView = findViewById(R.id.imageViewFoto);

        Button btnElegirFoto = findViewById(R.id.btnElegirFoto);
        Button btnContinuar = findViewById(R.id.continuar1);

        // Lanzador para seleccionar una imagen de la galería
        ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImageUri);
                            imageView.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // Lanzador para tomar una foto con la cámara
        ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        selectedImageUri = saveBitmapToUri(photo);
                        imageView.setImageBitmap(photo);
                    }
                }
        );

        // Configurar el botón de elegir foto
        btnElegirFoto.setOnClickListener(v -> {
            String[] opciones = {"Tomar foto", "Seleccionar de la galería"};
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Elige una opción")
                    .setItems(opciones, (dialog, which) -> {
                        if (which == 0) { // Tomar foto
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraLauncher.launch(cameraIntent);
                        } else if (which == 1) { // Seleccionar de la galería
                            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryLauncher.launch(galleryIntent);
                        }
                    })
                    .show();
        });

        // Configurar el botón de continuar
        btnContinuar.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                // Aquí puedes subir la imagen al Firestore Storage o continuar al siguiente paso
                Toast.makeText(this, "Foto seleccionada correctamente", Toast.LENGTH_SHORT).show();
                //Ingresar al siguiente paso
                uploadImage(selectedImageUri, usuario.getId(), new UploadCallback() {
                    @Override
                    public void onSuccess(String downloadUrl) {
                        usuario.setFotoUrl(downloadUrl);
                        guardarDatosEnFirestore();
                    }
                    @Override
                    public void onFailure(Exception e) {

                    }
                });

            } else {
                Toast.makeText(this, "Por favor selecciona una foto", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void uploadImage(Uri imageUri, String userId, UploadCallback listener) {
        if (imageUri != null) {
            // Crea una referencia para la imagen
            StorageReference imageRef = storageReference.child("users/" + userId + "/profile.jpg");

            // Sube la imagen
            UploadTask uploadTask = imageRef.putFile(imageUri);
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                // Obtiene la URL de descarga
                imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String downloadUrl = uri.toString();
                    Log.d("FirebaseStorage", "URL de descarga: " + downloadUrl);
                    listener.onSuccess(downloadUrl);
                }).addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error al obtener URL de descarga", e);
                    listener.onFailure(e);
                });
            }).addOnFailureListener(e -> {
                Log.e("FirebaseStorage", "Error al subir la imagen", e);
                listener.onFailure(e);
            });
        } else {
            listener.onFailure(new Exception("La URI de la imagen es nula"));
        }
    }
    public interface UploadCallback {
        void onSuccess(String downloadUrl);

        void onFailure(Exception e);
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
    private void guardarDatosEnFirestore() {
        db.collection("Usuarios")
                .document(usuario.getId())
                .set(usuario)
                .addOnSuccessListener(unused -> {
                    irAlSiguientePaso();
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al registrar usuario", e);
                    Toast.makeText(this, "Error al registrar usuario.", Toast.LENGTH_SHORT).show();
                });
    }
    private void irAlSiguientePaso() {
        Intent intent = new Intent(this, LoginCrearCuentaCuartoPaso.class);
        startActivity(intent);
    }
    private Uri saveBitmapToUri(Bitmap bitmap) {
        try {
            // Crear un archivo temporal en el almacenamiento interno de la aplicación
            File file = new File(getCacheDir(), "profile" + System.currentTimeMillis() + ".jpg");

            // Abrir un flujo para escribir en el archivo
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); // Comprimir el Bitmap en formato JPEG
            fos.flush();
            fos.close();

            // Retornar el Uri del archivo
            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al guardar la imagen", Toast.LENGTH_SHORT).show();
            return null;
        }
    }
}
