package com.example.deligov2.Administrador;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdministradorEditarPlatoActivity extends AppCompatActivity {
    FirebaseFirestore db;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseStorage storage;
    MaterialTextView foodName, foodPrice, foodDesc;
    ShapeableImageView image1, image2;
    Platillo platillo;
    private StorageReference storageRef;
    StorageReference storageReference2;
    private Uri imageUri;
    FloatingActionButton backButton;
    private ActivityResultLauncher<Intent> galleryLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher2;

    private ActivityResultLauncher<Intent> cameraLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        user = auth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_administrador_editar_plato);
        Intent intent = getIntent();
        platillo = (Platillo) intent.getSerializableExtra("plato");
        foodName = findViewById(R.id.foodname);
        foodPrice = findViewById(R.id.foodPrice);
        foodDesc = findViewById(R.id.descriptionFood);
        image1 = findViewById(R.id.image1);
        image2 = findViewById(R.id.image2);
        foodDesc.setText(platillo.getDescripcion());
        foodPrice.setText(String.format(" %.2f", platillo.getPrecio()));
        foodName.setText(platillo.getNombre());
        backButton = findViewById(R.id.goBackButton);
        image1.setOnClickListener(v -> showBottomSheetDialog(v, "image1"));

        // Click para editar image2
        image2.setOnClickListener(v -> showBottomSheetDialog(v, "image2"));

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
                                    .into(image1);
                            uploadImageToFirebase();
                        }
                    }
                }
        );

        galleryLauncher2 = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri selectedImageUri = result.getData().getData();
                        if (selectedImageUri != null) {
                            imageUri = selectedImageUri;
                            Glide.with(this)
                                    .load(imageUri)
                                    .placeholder(R.drawable.user_icon)
                                    .into(image2);
                            uploadImageToFirebase2();
                        }
                    }
                }
        );

        backButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(this, AdministradorRestauranteActivity.class);
            startActivity(intent1);
            finish();
        });
        storageRef = storage.getReference().child("restaurantes/"+platillo.getIdRestaurante()+"/"+platillo.getId()+"/plato.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.camara_icon)
                    .error(R.drawable.camara_icon)
                    .into(image1);
        }).addOnFailureListener(e -> {
            image1.setImageResource(R.drawable.camara_icon);
        });

        StorageReference directoryReference = storage.getReference().child("restaurantes/" + platillo.getIdRestaurante() + "/" + platillo.getId());
        directoryReference.listAll()
                .addOnSuccessListener(listResult -> {
                    for (StorageReference fileRef : listResult.getItems()) {
                        if (!fileRef.getName().equals("plato.jpg")) {
                            storageReference2 = fileRef;
                            fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                Glide.with(this)
                                        .load(uri)
                                        .placeholder(R.drawable.camara_icon)
                                        .error(R.drawable.camara_icon)
                                        .into(image2);
                            }).addOnFailureListener(e -> {
                                image2.setImageResource(R.drawable.camara_icon);
                            });
                            break;
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("FirebaseStorage", "Error al listar archivos", e);
                    image2.setImageResource(R.drawable.camara_icon);
                });

    }

    public void editNameFood(View view ){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_phone, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextInputEditText bottomEditCellphone = bottomSheetView.findViewById(R.id.bottom_edit_cellphone);
        bottomEditCellphone.setInputType(InputType.TYPE_CLASS_TEXT);
        TextInputLayout inputLayout = bottomSheetView.findViewById(R.id.hint);
        inputLayout.setHint("Ingrese nuevo nombre");
        TextView title = bottomSheetView.findViewById(R.id.title);
        title.setText("Editar nombre del plato");
        Button bottomSaveButton = bottomSheetView.findViewById(R.id.bottom_save_button);
        Button bottomCancelButton = bottomSheetView.findViewById(R.id.bottom_cancel_button);

        bottomEditCellphone.setText(platillo.getNombre());

        bottomSaveButton.setOnClickListener(view2 -> {
            String newName = bottomEditCellphone.getText().toString().trim();

            if (newName.isEmpty()) {
                bottomEditCellphone.setError("No ha ingreseado texto");
                return;
            }

            // Guardar en Firestore

            db.collection("Platos").document(platillo.getId())
                    .update("nombre", newName)
                    .addOnSuccessListener(aVoid -> {
                        foodName.setText(newName);
                        bottomSheetDialog.dismiss();
                        Toast.makeText(this, "Nombre Actualizado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                    });
        });
        bottomCancelButton.setOnClickListener(view3 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    public void editPriceFood(View view ){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_phone, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextInputEditText bottomEditCellphone = bottomSheetView.findViewById(R.id.bottom_edit_cellphone);
        TextInputLayout inputLayout = bottomSheetView.findViewById(R.id.hint);
        inputLayout.setHint("Ingrese nuevo precio");
        TextView title = bottomSheetView.findViewById(R.id.title);
        title.setText("Editar precio del plato");
        Button bottomSaveButton = bottomSheetView.findViewById(R.id.bottom_save_button);
        Button bottomCancelButton = bottomSheetView.findViewById(R.id.bottom_cancel_button);

        bottomEditCellphone.setText(""+platillo.getPrecio());

        bottomSaveButton.setOnClickListener(view2 -> {
            String newPrice = bottomEditCellphone.getText().toString().trim();

            if (newPrice.isEmpty()) {
                bottomEditCellphone.setError("No ha ingreseado nada");
                return;
            }
            if (!newPrice.matches("^\\d*(\\.\\d{1,2})?$")) {
                bottomEditCellphone.setError("Ingrese un número válido con máximo 2 decimales");
                return;
            }
            float precio = Float.parseFloat(newPrice);

            // Guardar en Firestore

            db.collection("Platos").document(platillo.getId())
                    .update("precio", precio)
                    .addOnSuccessListener(aVoid -> {
                        foodPrice.setText(String.format(" %.2f", precio));
                        bottomSheetDialog.dismiss();
                        Toast.makeText(this, "Precio Actualizado", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar el nombre", Toast.LENGTH_SHORT).show();
                    });
        });
        bottomCancelButton.setOnClickListener(view3 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    public void editDescFood(View view ){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_phone, null);
        bottomSheetDialog.setContentView(bottomSheetView);
        TextInputEditText bottomEditCellphone = bottomSheetView.findViewById(R.id.bottom_edit_cellphone);
        bottomEditCellphone.setInputType(InputType.TYPE_CLASS_TEXT);
        TextInputLayout inputLayout = bottomSheetView.findViewById(R.id.hint);
        inputLayout.setHint("Ingrese nueva descripción");
        TextView title = bottomSheetView.findViewById(R.id.title);
        title.setText("Editar descripción del plato");
        Button bottomSaveButton = bottomSheetView.findViewById(R.id.bottom_save_button);
        Button bottomCancelButton = bottomSheetView.findViewById(R.id.bottom_cancel_button);

        bottomEditCellphone.setText(platillo.getDescripcion());

        bottomSaveButton.setOnClickListener(view2 -> {
            String newDesc = bottomEditCellphone.getText().toString().trim();

            if (newDesc.isEmpty()) {
                bottomEditCellphone.setError("No ha ingreseado texto");
                return;
            }

            // Guardar en Firestore

            db.collection("Platos").document(platillo.getId())
                    .update("descripcion", newDesc)
                    .addOnSuccessListener(aVoid -> {
                        foodDesc.setText(newDesc);
                        bottomSheetDialog.dismiss();
                        Toast.makeText(this, "Descripción Actualizada", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al actualizar la descripción", Toast.LENGTH_SHORT).show();
                    });
        });
        bottomCancelButton.setOnClickListener(view3 -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
    }

    @SuppressLint("MissingInflatedId")
    public void showBottomSheetDialog(View view, String image) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_edit_photo, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout btnGallery = bottomSheetView.findViewById(R.id.btn_gallery);
        LinearLayout btnCancel = bottomSheetView.findViewById(R.id.btn_cancel);

        btnGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            if(image.equals("image1")){
                galleryLauncher.launch(intent);
            }else{
                galleryLauncher2.launch(intent);
            }
            bottomSheetDialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());
        bottomSheetDialog.show();
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
                                .into((ShapeableImageView) findViewById(R.id.image1));
                        Toast.makeText(this, "Imagen actualizada", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }

    private void uploadImageToFirebase2() {
        if (imageUri != null) {
            StorageReference profileRef = storageReference2;
            profileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> profileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        Glide.with(this)
                                .load(uri)
                                .placeholder(R.drawable.user_icon)
                                .into((ShapeableImageView) findViewById(R.id.image2));
                        Toast.makeText(this, "Imagen actualizada", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Error al subir la imagen", Toast.LENGTH_SHORT).show());
        }
    }
}
