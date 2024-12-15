package com.example.deligov2.Cliente;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.LogIn.InicioSesion.LoginInicioActivity;
import com.example.deligov2.LogIn.LoginPrimeraVista;
import com.example.deligov2.R;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ClientePerfil extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    FloatingActionButton logout;
    FloatingActionButton goBackButton;
    MaterialTextView name, lastName, email, cellphone,mainLocation;
    private Usuario usuario;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_perfil);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();

        ShapeableImageView image = findViewById(R.id.shapeableImageView);
        storageRef = storage.getReference().child("users/" + user.getUid() + "/profile.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.xd)
                    .into(image);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        });

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
                                    .into(image);
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
                                .into(image);
                        uploadImageToFirebase();
                    }
                }
        );

        name= findViewById(R.id.name);
        lastName= findViewById(R.id.lastName);
        email= findViewById(R.id.email);
        cellphone= findViewById(R.id.cellphone);
        mainLocation= findViewById(R.id.mainLocation);

        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                                name.setText(usuario.getNombre());
                                lastName.setText(usuario.getApellido());
                                email.setText(usuario.getCorreo());
                                cellphone.setText(usuario.getNumeroTelefono());
                                mainLocation.setText(usuario.getDireccion());
                            }
                        }
                    }
                });

        logout = findViewById(R.id.logOut);
        logout.setOnClickListener(view -> {
            AuthUI.getInstance().signOut(ClientePerfil.this)
                    .addOnCompleteListener(task -> {
                        Intent intent = new Intent(ClientePerfil.this, LoginInicioActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpiar pila
                        startActivity(intent);
                        finish();
                    });
        });


        goBackButton = findViewById(R.id.goBackButtonPerfil);

        goBackButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, ClienteHomeActivity.class);
            startActivity(intent);
        });


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.profile);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClientePerfil.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClientePerfil.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClientePerfil.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.cliente_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==R.id.historial){
            startActivity(new Intent(this, ClienteHistorialActivity.class));
            return true;
        } else if (item.getItemId()==R.id.restaurant) {
            startActivity(new Intent(this, ClienteRestaurantActivity.class));
            return true;
        } else if (item.getItemId()==R.id.profile) {
            startActivity(new Intent(this, ClientePerfil.class));
            return true;
        }else{
            return super.onOptionsItemSelected(item);

        }

    }
    public void editPhoneNumber(View view ){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_phone, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextInputEditText bottomEditCellphone = bottomSheetView.findViewById(R.id.bottom_edit_cellphone);
        Button bottomSaveButton = bottomSheetView.findViewById(R.id.bottom_save_button);
        Button bottomCancelButton = bottomSheetView.findViewById(R.id.bottom_cancel_button);

        bottomEditCellphone.setText(""+usuario.getNumeroTelefono());

        bottomSaveButton.setOnClickListener(view2 -> {
            String newPhone = bottomEditCellphone.getText().toString().trim();

            if (newPhone.isEmpty()) {
                bottomEditCellphone.setError("El número de teléfono no puede estar vacío");
                return;
            }

            if (newPhone.length() != 9 || !newPhone.matches("\\d+")) {
                bottomEditCellphone.setError("Por favor, ingrese un número válido de 10 dígitos");
                return;
            }

            // Guardar en Firestore

            db.collection("Usuarios").document(user.getUid())
                    .update("numeroTelefono", newPhone)
                    .addOnSuccessListener(aVoid -> {
                        cellphone.setText(newPhone); // Actualiza la vista principal
                        bottomSheetDialog.dismiss();
                        Toast.makeText(this, "Número actualizado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar el número", Toast.LENGTH_SHORT).show();
                    });
        });
        bottomCancelButton.setOnClickListener(view3 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    @SuppressLint("MissingInflatedId")
    public void showBottomSheetDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_photo, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout btnGallery = bottomSheetView.findViewById(R.id.btn_gallery);
        LinearLayout btnCamera = bottomSheetView.findViewById(R.id.btn_camera);
        LinearLayout btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
            bottomSheetDialog.dismiss();
        });

        btnCamera.setOnClickListener(v -> {
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
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    private boolean checkCameraPermission() {

        return true;
    }

    private void openCamera() {
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
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            StorageReference profileRef = storageRef;
            profileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.user_icon)
                                .into((ShapeableImageView) findViewById(R.id.shapeableImageView));
                        Toast.makeText(this, "Imagen actualizada", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }


}