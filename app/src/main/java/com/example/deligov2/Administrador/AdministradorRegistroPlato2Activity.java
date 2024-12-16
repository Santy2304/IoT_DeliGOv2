package com.example.deligov2.Administrador;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.deligov2.Administrador.Adapters.ImagesAdapter;
import com.example.deligov2.DTO.LogSuper;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdministradorRegistroPlato2Activity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private Button btnFinalize;
    private ImageView btnUploadImage;
    private RecyclerView rvImages;
    private ImagesAdapter imagesAdapter;
    private List<Uri> imageUris = new ArrayList<>();
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore db;
    private Platillo plato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        db = FirebaseFirestore.getInstance();

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

        // Navegación por medio del bottom Navigation
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_admin);
        bottomNavigationView.setSelectedItemId(R.id.principal);

        bottomNavigationView.setOnNavigationItemSelectedListener(item ->  {

            mostrarDialogoCancelar(() -> {

                if(item.getItemId()==R.id.reports){
                    Intent intentReportes = new Intent(AdministradorRegistroPlato2Activity.this, AdministradorReportesActivity.class);
                    startActivity(intentReportes);
                    //return true;
                }else if(item.getItemId()==R.id.information){
                    Intent intentInformation = new Intent(AdministradorRegistroPlato2Activity.this, AdministradorInfoRestauranteActivity.class);
                    startActivity(intentInformation);
                    //return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(AdministradorRegistroPlato2Activity.this, AdministradorRestauranteActivity.class);
                    startActivity(intentPrincipal);
                    //return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(AdministradorRegistroPlato2Activity.this, AdministradorPerfilActivity.class);
                    startActivity(intentProfile);
                    //return true;
                }
            });
            return false;
        });

    }

    private void mostrarDialogoCancelar(Runnable accionConfirmada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar acción");
        builder.setMessage("Se perderá el progreso. ¿Estás seguro de que deseas cancelar?");
        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            // Ejecutar la acción pasada como parámetro
            if (accionConfirmada != null) {
                accionConfirmada.run();
            }
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> {
            // Cerrar el diálogo
            dialog.dismiss();
        });
        builder.show();
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
        db.collection("Platos").document(plato.getId()).set(plato)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Plato registrado con éxito", Toast.LENGTH_SHORT).show();
                    // Subir las imágenes
                    for (int i = 0; i < imageUris.size(); i++) {
                        boolean isFirstImage = (i == 0);
                        uploadImageToFirebase(imageUris.get(i), isFirstImage);
                    }

                    db.collection("restaurantes").document(plato.getIdRestaurante()).get()
                            .addOnSuccessListener(documentSnapshot1 -> {
                                if (documentSnapshot1.exists()) {
                                    Restaurante restaurante = documentSnapshot1.toObject(Restaurante.class);
                                    LogSuper logSuper = new LogSuper();
                                    logSuper.setTipo("Plato");
                                    logSuper.setInfo("Se ha creado el platillo "+plato.getNombre() +" en el restaurante "+restaurante.getNombre());
                                    logSuper.setFecha(Timestamp.now());
                                    logSuper.setIdImage(plato.getIdRestaurante()+"/"+plato.getId());
                                    db.collection("Logs").add(logSuper)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Toast.makeText(this, "Pedido realizado exitosamente", Toast.LENGTH_SHORT).show();

                                            })
                                            .addOnFailureListener(e -> {
                                                Toast.makeText(this, "Error al realizar el pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });

                                }
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));


                    Toast.makeText(this, "Imágenes subidas exitosamente", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, AdministradorRegistroPlatoExitosoActivity.class);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // Mostrar un mensaje de error
                    Toast.makeText(this, "Error al registrar el plato", Toast.LENGTH_SHORT).show();
                });
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
