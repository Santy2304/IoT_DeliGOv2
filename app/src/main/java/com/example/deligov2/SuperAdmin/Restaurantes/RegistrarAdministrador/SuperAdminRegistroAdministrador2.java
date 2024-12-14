package com.example.deligov2.SuperAdmin.Restaurantes.RegistrarAdministrador;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.deligov2.DTO.Restaurante;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.example.deligov2.SuperAdmin.Home.SuperAdminHomeActivity;
import com.example.deligov2.SuperAdmin.Restaurantes.SuperAdminRestaurante;
import com.example.deligov2.SuperAdmin.SuperAdminPerfil;
import com.example.deligov2.SuperAdmin.SuperAdminVistaLogEvent;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class SuperAdminRegistroAdministrador2 extends AppCompatActivity {
    private MaterialTextView adminRestaurante;
    private TextInputEditText adminCorreo;
    private Usuario adminN;
    String canal2 = "importante Otro";

    private FirebaseFirestore db;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE_LOGO = 3;
    private static final int REQUEST_IMAGE_PICK_LOGO = 4;
    private static final int REQUEST_PERMISSIONS = 100;
    //para la foto
    private Uri imageUri;
    private Bitmap imageBitmap;

    //Para guardar la foto
    private StorageReference storageReference;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_registro_administrador2);

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminVistaLogEvent.class);
//                    startActivity(intent);
//                    return true;
//                }else{
//                    return false;
//                }
//            }
//        });

        //Manejo del botton_navbar
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.restaurant);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminRestaurante.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intentPrincipal = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminHomeActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(SuperAdminRegistroAdministrador2.this, SuperAdminPerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });

        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Restaurante resR = (Restaurante) intent.getSerializableExtra("nr2");
        //String nameR = intent.getStringExtra("nr2");
        Usuario ad = (Usuario) intent.getSerializableExtra("admin");
        Log.d("PROBANDOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO", "Nombre ADMIN"+ad.getNombre()+ad.getApellido() + " _ " + ad.getRestaurante());

        adminRestaurante = findViewById(R.id.adminRestaurante);
        adminCorreo = findViewById(R.id.adminCorreo);
        adminRestaurante.setText(resR.getNombre());


        //Manejo de botones
        Button btContinuar = findViewById(R.id.aceptar);
        TextInputEditText adminCorreo = findViewById(R.id.adminCorreo);
        TextInputLayout emailLayout = findViewById(R.id.emailLayout);

        crearCanalesNotificacion();

        ImageView imageView = findViewById(R.id.imgAdmin);
        TextView tvLogo = findViewById(R.id.tv_foto);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkPermissions()) {
                    openGalleryOrCamera();
                }
            }
        });

        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = adminCorreo.getText().toString().trim();

                if (email.isEmpty()) {
                    emailLayout.setError("El correo no puede estar vacío");
                    emailLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }

                if (!email.contains("@")) {
                    emailLayout.setError("El correo debe contener un '@'");
                    emailLayout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.md_theme_error)));
                    return;
                }
                emailLayout.setError(null);

                adminN = new Usuario();
                adminN.setNombre(ad.getNombre());
                adminN.setApellido(ad.getApellido());
                adminN.setCorreo(email);
                adminN.setEstado(true);
                adminN.setRestaurante(resR.getId());
                //adminN.setUbicacionRestaurante(resR.getDireccion());
                adminN.setNumDocument(ad.getNumDocument());

                notificarAsignarAdminRestaurante(adminN,ad.getNombre(),resR.getNombre());
                vistaRegistroAdminCorrect();

                //Foto
                byte[] byteArray = null;
                String imageStr = null;
                if (imageUri != null) {
                    imageStr = imageUri.toString();
                    Log.d("DEBUG_IMAGE", "imageUri: " + imageUri.toString());
                } else if (imageBitmap != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byteArray = baos.toByteArray();
                }

                registrarAdministradorFirebase(adminN,resR.getId(), imageStr,byteArray);
            }
        });

    }

    public void vistaRegistroAdminCorrect(){
        Intent intent = new Intent(this, SuperAdminRegistroAdminCorrect.class);
        startActivity(intent);
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
    }

    //Notificar cuando se registra un admin al restaurante
    public void crearCanalesNotificacion() {

        NotificationChannel channel = new NotificationChannel(canal2,
                "Canal notificaciones default",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Canal para notificaciones con prioridad default");
        channel.enableVibration(true);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        pedirPermisos();
    }

    public void pedirPermisos() {
        // TIRAMISU = 33
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_DENIED) {

            ActivityCompat.requestPermissions(SuperAdminRegistroAdministrador2.this, new String[]{POST_NOTIFICATIONS}, 101);
        }
    }

    public void notificarAsignarAdminRestaurante(Usuario admin, String name, String nameR){

        //Crear notificación
        //Agregar información a la notificación que luego sea enviada a la actividad que se abre
        Intent intent = new Intent(this, SuperAdminVistaLogEvent.class);
        intent.putExtra("admin",admin);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, canal2)
                .setSmallIcon(R.drawable.deligo)
                .setContentTitle("Deligo events")
                .setContentText("Se ha registrado el administrador "+ name +" al restaurante "+nameR)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Notification notification = builder.build();

        //Lanzar notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (ActivityCompat.checkSelfPermission(this, POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(282, notification);
        }
    }

    /*
    private void registrarAdministradorFirebase(Usuario admin, String resId, String imageUriString, byte[] imageByteArray) {
        admin.setDate("_");
        admin.setDireccion("_");
        //Falta setear foto po default --> feat: ya está hecho
        admin.setNumeroTelefono("_");
        admin.setReferencia("_");
        admin.setRol("Administrador");

        db.collection("Usuarios")
                .add(admin)
                .addOnSuccessListener(documentReference -> {
                    String idAdmin = documentReference.getId();
                    //admin.setIdAdmin(idAdmin);
                    // Actualizar el campo id en Firestore con el ID generado
                    Map<String , Object> data = new HashMap<>();
                    data.put("id", idAdmin);
                    data.put("correo", admin.getCorreo());
                    documentReference.update(data)
                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "ID actualizado correctamente en el documento."))
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar el campo ID", e));

                    Log.d("Firestore", "Administrador registrado con ID: " + idAdmin);

                    //Aquí se sube la imagen a fireStorage
                    subirImagenAFirebaseStorage(idAdmin, imageUriString, imageByteArray);

                    // Buscar el restaurante por id en su colección y actualizar el campo admin
                    db.collection("restaurantes").document(resId)
                            .get()
                            .addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()) {
                                    db.collection("restaurantes").document(resId)
                                            .update("admin", idAdmin)
                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "Campo admin actualizado correctamente para el restaurante con ID: " + resId))
                                            .addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar el campo admin del restaurante", e));
                                } else {
                                    Log.w("Firestore", "No se encontró el restaurante con ID: " + resId);
                                }
                            })
                            .addOnFailureListener(e -> Log.w("Firestore", "Error al buscar el restaurante en Firestore", e));

                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error al registrar el admin", e);
                });
    }

     */
    private void registrarAdministradorFirebase(Usuario admin, String resId, String imageUriString, byte[] imageByteArray) {
        admin.setDate("_");
        admin.setDireccion("_");
        //Falta setear foto por defecto --> feat: ya está hecho
        admin.setNumeroTelefono("_");
        admin.setReferencia("_");
        admin.setRol("Administrador");

        // Credenciales para Firebase Authentication
        String email = admin.getCorreo();
        String password = "D3L1G02X24"; //le puse una contra por defecto

        FirebaseAuth auth = FirebaseAuth.getInstance();

        // Crear usuario en Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Usuario creado en Firebase Authentication
                        FirebaseUser firebaseUser = task.getResult().getUser();
                        if (firebaseUser != null) {
                            String idAdmin = firebaseUser.getUid();
                            admin.setId(idAdmin);

                            // Guardar los datos en Firestore
                            db.collection("Usuarios")
                                    .document(idAdmin)
                                    .set(admin)
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("Firestore", "Administrador registrado en Firestore con ID: " + idAdmin);

                                        // Subir imagen a Firebase Storage
                                        subirImagenAFirebaseStorage(idAdmin, imageUriString, imageByteArray);

                                        // Actualizar restaurante con el ID del admin
                                        db.collection("restaurantes").document(resId)
                                                .update("admin", idAdmin)
                                                .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Campo admin actualizado correctamente para el restaurante con ID: " + resId))
                                                .addOnFailureListener(e -> Log.w("Firestore", "Error al actualizar el campo admin del restaurante", e));
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("Firestore", "Error al registrar el admin en Firestore", e);
                                    });
                        }
                    } else {
                        Log.w("FirebaseAuth", "Error al crear el usuario en Firebase Authentication", task.getException());
                    }
                });
    }

    private void openGalleryOrCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(pickPhotoIntent, "Selecciona la opción");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});

        startActivityForResult(chooser,REQUEST_IMAGE_PICK_LOGO);
    }

    private boolean checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSIONS);
            return false;
        }
        return true;
    }

    private void handleImageResult(Intent data, ImageView imageView, TextView textView) {
        try {
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                // Imagen capturada por la cámara
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                textView.setVisibility(View.INVISIBLE);

                // Almacena la imagen dependiendo del tipo
                if (imageView.getId() == R.id.imgAdmin) {
                    imageBitmap = bitmap;
                    imageUri = null;
                }
            } else if (data.getData() != null) {
                // Imagen seleccionada de la galería
                Uri uri = data.getData();
                imageView.setImageURI(uri);
                textView.setVisibility(View.INVISIBLE);

                // Almacena la imagen dependiendo del tipo
                if (imageView.getId() == R.id.imgAdmin) {
                    imageUri = uri;
                    imageBitmap = null;
                }
            } else {
                Log.e("DEBUG_IMAGE", "No se recibió imagen válida.");
            }
        } catch (Exception e) {
            Log.e("DEBUG_IMAGE", "Error al procesar la imagen: ", e);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Manejo para logo
            if (requestCode == REQUEST_IMAGE_CAPTURE_LOGO || requestCode == REQUEST_IMAGE_PICK_LOGO) {
                handleImageResult(data, findViewById(R.id.imgAdmin), findViewById(R.id.tv_foto));
            }
            // Caso no manejado
            else {
                Log.e("DEBUG_IMAGE", "Código de solicitud no manejado: " + requestCode);
            }
        } else {
            Log.e("DEBUG_IMAGE", "onActivityResult recibió un código de resultado no OK o data nula.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGalleryOrCamera();
            } else {
                Log.e("DEBUG_PERMISSION", "Permisos denegados por el usuario.");
            }
        }
    }

    private void subirImagenAFirebaseStorage(String idUser, String imageUriString, byte[] imageByteArray) {
        if (imageUriString != null) {
            // Subir desde Uri
            Uri imageUri = Uri.parse(imageUriString);
            StorageReference imageRef = storageReference.child("users/" + idUser + "/profile.jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("Firebase Storage", "Imagen subida correctamente desde Uri.");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firebase Storage", "Error al subir la imagen desde Uri", e);
                    });

        } else if (imageByteArray != null) {
            // Subir desde byte[]
            StorageReference imageRef = storageReference.child("users/" + idUser + "/profile.jpg");

            imageRef.putBytes(imageByteArray)
                    .addOnSuccessListener(taskSnapshot -> {
                        Log.d("Firebase Storage", "Imagen subida correctamente desde byte[].");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("Firebase Storage", "Error al subir la imagen desde byte[]", e);
                    });

        } else {
            Log.w("Firebase Storage", "No se encontró una imagen para subir.");
        }
    }
}