package com.example.deligov2.Administrador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Administrador.Adapters.ImagesAdapter;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdministradorRegistroPlato2Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnUploadImage, btnFinalize;
    private RecyclerView rvImages;
    private ImagesAdapter imagesAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private Platillo plato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_administrador_registro_plato_parte2);

        // Lógica para la actividad de registro de plato parte 2 (imagenes)
        // Recibir el objeto Plato
        plato = (Platillo) getIntent().getSerializableExtra("plato");
        if (plato == null) {
            Toast.makeText(this, "Error: Datos del plato no encontrados", Toast.LENGTH_SHORT).show();
            finish();
        }
        // Inicializar Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        // Capturar componentes de la vista
        btnUploadImage = findViewById(R.id.btnUploadImage);
        btnFinalize = findViewById(R.id.btnFinalizar);
        rvImages = findViewById(R.id.rvImages);

        // Configurar el RecyclerView
        imagesAdapter = new ImagesAdapter(imageUris, this);
        rvImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvImages.setAdapter(imagesAdapter);

        // Botón para subir imágenes
        btnUploadImage.setOnClickListener(view -> openFileChooser());

        // Botón para finalizar
        btnFinalize.setOnClickListener(view -> finalizeRegistration());

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona Imágenes"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            if (data.getClipData() != null) {
                // Selección múltiple
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    imageUris.add(imageUri);
                }
            } else if (data.getData() != null) {
                // Selección única
                Uri imageUri = data.getData();
                imageUris.add(imageUri);
            }
            imagesAdapter.notifyDataSetChanged();
        }
    }

    private void finalizeRegistration() {
        if (imageUris.size() < 2) {
            Toast.makeText(this, "Debes subir al menos 2 imágenes", Toast.LENGTH_SHORT).show();
            return;
        }

        // Subir las imágenes
        for (int i = 0; i < imageUris.size(); i++) {
            boolean isFirstImage = (i == 0);
            uploadImageToFirebase(imageUris.get(i), isFirstImage);
        }

        Toast.makeText(this, "Imágenes subidas exitosamente", Toast.LENGTH_SHORT).show();
        // Redirigir o finalizar actividad
        finish();
    }

    private void uploadImageToFirebase(Uri imageUri, boolean isFirstImage) {
        // Ruta de almacenamiento
        String idRestaurante = plato.getIdRestaurante(); // Obtener el ID del restaurante
        String idPlato = plato.getId();               // Obtener el ID del plato
        String fileName = isFirstImage ? "plato.jpg" : System.currentTimeMillis() + ".jpg";
        String storagePath = "restaurantes/" + idRestaurante + "/" + idPlato + "/" + fileName;

        // Referencia en Firebase Storage
        StorageReference fileReference = storageReference.child(storagePath);

        // Subir la imagen al storage
        fileReference.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> Log.d("Firebase", "Imagen subida: " + storagePath))
                .addOnFailureListener(e -> Log.e("Firebase", "Error al subir imagen", e));
    }
}
