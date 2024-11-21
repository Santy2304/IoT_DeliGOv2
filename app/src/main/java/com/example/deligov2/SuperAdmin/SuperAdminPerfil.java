package com.example.deligov2.SuperAdmin;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.deligov2.Beans.Usuario;
import com.example.deligov2.Cliente.ClientePerfil;
import com.example.deligov2.LogIn.LoginPrimeraVista;
import com.example.deligov2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SuperAdminPerfil extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_PERMISSIONS = 100;
    private FirebaseFirestore db;

    private MaterialTextView nombre,apellido, numDni,correo;
    private ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_super_admin_perfil);

        db = FirebaseFirestore.getInstance();
        // Obtener los datos del intent anterior a este
        /*
        Intent intent = getIntent();

        Usuario sa = (Usuario) intent.getSerializableExtra("sa");
        Log.d("PERFIL SUPERA AMDID","SA PERFIL: "+sa.getId());

         */
        nombre = findViewById(R.id.name);
        apellido = findViewById(R.id.apellido);
        numDni = findViewById(R.id.n_dni);
        correo = findViewById(R.id.correo);
        imagen = findViewById(R.id.imgSAperfil);


        // Cargar imagen desde Firebase Storage
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference()
                .child("users/" + "ClcUvl7d43Rz0aEqbLteSw22eH22" + "/profile.jpg");

        storageRef.getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(imagen.getContext())
                            .load(uri)
                            .placeholder(R.drawable.ic_loading)
                            .error(R.drawable.ic_errorimg)
                            .into(imagen);
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error al cargar la imagen: ", e);
                    imagen.setImageResource(R.drawable.ic_errorimg);
                });


        db.collection("Usuarios")
                .document("ClcUvl7d43Rz0aEqbLteSw22eH22") //sa.getId(), por las ... envie entre todos el sa
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String nombreUsuario = documentSnapshot.getString("nombre");
                        String apellidoUsuario = documentSnapshot.getString("apellido");
                        String dniUsuario = documentSnapshot.getString("numDocument");
                        String correoUsuario = documentSnapshot.getString("correo");

                        nombre.setText(nombreUsuario != null ? nombreUsuario : "---");
                        apellido.setText(apellidoUsuario != null ? apellidoUsuario : "---");
                        numDni.setText(dniUsuario != null ? dniUsuario : "---");
                        correo.setText(correoUsuario != null ? correoUsuario : "---");
                    } else {
                        Log.d("Firestore", "No se encontró el usuario con ID: " +"ClcUvl7d43Rz0aEqbLteSw22eH22") ;
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al obtener el usuario: ", e);
                });

        //Manejo del top app bar
        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);


        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(@NonNull MenuItem item) {
                if(item.getItemId()==R.id.log_event){
                    Intent intent = new Intent(SuperAdminPerfil.this, SuperAdminVistaLogEvent.class);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }
            }
        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intent = new Intent(SuperAdminPerfil.this, SuperAdminRestaurante.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminPerfil.this, SuperAdminHomeActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminPerfil.this, SuperAdminPerfil.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    return true;
                }else{
                    return false;
                }

            }
        });
        //Manejo de los botones
        Button btExit = findViewById(R.id.bt_exit);

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });

        Button button = findViewById(R.id.bt_fotoSA);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    openGalleryOrCamera();
                }
            }
        });
    }


    //Manejo vistas
    public void cerrarSesion(){
        AuthUI.getInstance().signOut(SuperAdminPerfil.this)
                .addOnCompleteListener(task -> {
                    Intent intent = new Intent(SuperAdminPerfil.this, LoginPrimeraVista.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar pila
                    startActivity(intent);
                    finish();
                });
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
            return false;
        } else {
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryOrCamera();
            } else {
                //No sé ayuda
            }
        }
    }

    private void openGalleryOrCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(pickPhotoIntent, "Selecciona la opción");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});
        startActivityForResult(chooser, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                // Guarda el Bitmap como archivo temporal y obtiene su URI
                Uri tempUri = guardarBitmapComoUri(imageBitmap);
                if (tempUri != null) {
                    subirImagenAFirebase(tempUri);
                }
            } else if (requestCode == REQUEST_IMAGE_PICK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    subirImagenAFirebase(selectedImageUri);
                }
            }
        }
    }

    private void subirImagenAFirebase(Uri imagenUri) {
        String userId = "ClcUvl7d43Rz0aEqbLteSw22eH22";
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users/" + userId + "/profile.jpg");

        // Eliminar imagen anterior si existe
        storageRef.delete()
                .addOnSuccessListener(aVoid -> Log.d("FirebaseStorage", "Imagen anterior eliminada."))
                .addOnFailureListener(e -> Log.d("FirebaseStorage", "No se encontró imagen previa, continuando."));

        // Subir nueva imagen
        storageRef.putFile(imagenUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Log.d("FirebaseStorage", "Imagen subida exitosamente.");
                    storageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                ImageView imageView = findViewById(R.id.imgSAperfil);
                                Glide.with(imageView.getContext())
                                        .load(uri)
                                        .placeholder(R.drawable.ic_loading)
                                        .error(R.drawable.ic_errorimg)
                                        .into(imageView);
                            });
                })
                .addOnFailureListener(e -> Log.e("FirebaseStorage", "Error al subir la imagen: ", e));
    }

    private Uri guardarBitmapComoUri(Bitmap bitmap) {
        try {
            File tempFile = File.createTempFile("temp_image", ".jpg", getCacheDir());
            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
            return Uri.fromFile(tempFile);
        } catch (IOException e) {
            Log.e("BitmapToUri", "Error al guardar Bitmap temporalmente", e);
            return null;
        }
    }
}