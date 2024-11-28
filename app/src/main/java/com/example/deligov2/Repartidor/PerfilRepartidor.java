package com.example.deligov2.Repartidor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
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

public class PerfilRepartidor extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private Usuario usuario;
    private FirebaseStorage storage ;
    private StorageReference storageRef;
    MaterialTextView name, lastName, email, cellphone,mainLocation;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_perfil_repartidor);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ShapeableImageView image = findViewById(R.id.shapeableImageView);
        storageRef = storage.getReference().child("users/" + user.getUid() + "/profile.jpg");
        // Usa Glide para cargar la imagen
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.user_icon)
                    .error(R.drawable.xd)
                    .into(image);
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show();
        });

        name= findViewById(R.id.name);
        lastName= findViewById(R.id.lastName);
        email= findViewById(R.id.email);
        cellphone= findViewById(R.id.cellphone);
        mainLocation= findViewById(R.id.mainLocation);
        //editCellphone = findViewById(R.id.edit_cellphone);

        loadUser();
    }
    public void loadUser(){
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

    public void salir(View view){
        firebaseAuth.signOut();
        Intent intent = new Intent(this, LoginInicioActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show();
        finish();
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
            if (checkCameraPermission()) {
                openCamera();
                bottomSheetDialog.dismiss();
            }
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
}