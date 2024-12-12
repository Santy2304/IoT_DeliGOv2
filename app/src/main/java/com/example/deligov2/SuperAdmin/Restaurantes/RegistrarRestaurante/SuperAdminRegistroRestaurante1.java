package com.example.deligov2.SuperAdmin.Restaurantes.RegistrarRestaurante;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SuperAdminRegistroRestaurante1 extends AppCompatActivity {
    Spinner tipoCategoria;
    private TextInputEditText tiHinicio;
    private TextInputEditText tiHfin;
    private int hora, minuto;
    Button btContinuar;
    Button btCancelar;
    private Usuario usuario;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int REQUEST_IMAGE_CAPTURE_LOGO = 3;
    private static final int REQUEST_IMAGE_PICK_LOGO = 4;
    private static final int REQUEST_IMAGE_CAPTURE_BANNER = 5;
    private static final int REQUEST_IMAGE_PICK_BANNER = 6;
    private static final int REQUEST_PERMISSIONS = 100;
    private FirebaseFirestore db;
    //Nombre del restaurante
    private FirebaseUser user;
    private TextInputEditText restauranteNombre;
    private FirebaseStorage storage;
    private String categoria;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    //para la foto
    private Uri imageUri;
    private Bitmap imageBitmap;

    private Uri imageUri2; //para el banner
    private Bitmap imageBitmap2;
    private ImageView currentImageView;

    private int currentRequestType;

    private boolean lastRequestWasForLogo = false;
    private boolean lastRequestWasForBanner = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user = firebaseAuth.getCurrentUser();
        loadUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_registro_restaurante1);
        // Obtener los datos del intent anterior a este
        Intent intent = getIntent();
        Usuario sa = (Usuario) intent.getSerializableExtra("sa");
        //Manejo del top app bar
//        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
//        topAppBar.setOnMenuItemClickListener(new MaterialToolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(@NonNull MenuItem item) {
//                if(item.getItemId()==R.id.log_event){
//                    Intent intent = new Intent(SuperAdminRegistroRestaurante1.this, SuperAdminVistaLogEvent.class);
//                    intent.putExtra("sa",sa);
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
                    Intent intent = new Intent(SuperAdminRegistroRestaurante1.this, SuperAdminRestaurante.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.principal){
                    Intent intent = new Intent(SuperAdminRegistroRestaurante1.this, SuperAdminHomeActivity.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intent = new Intent(SuperAdminRegistroRestaurante1.this, SuperAdminPerfil.class);
                    intent.putExtra("sa",sa);
                    startActivity(intent);
                    return true;
                }else{
                    return false;
                }

            }
        });

        //Para las categorías
        tipoCategoria = (Spinner) findViewById(R.id.spinner_categoria);

        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this, R.array.categoria_list, R.layout.spinner_item);
        activityAdapter.setDropDownViewResource(R.layout.spinner_item);
        tipoCategoria.setAdapter(activityAdapter);

        tipoCategoria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String activity = parent.getItemAtPosition(position).toString();

                if (activity.equals("Comida Rápida")) {
                    categoria = "Comida Rápida";
                } else if (activity.equals("Comida China")) {
                    categoria = "Comida China";

                } else if (activity.equals("Pizzeria")) {
                    categoria = "Pizzeria";
                } else if (activity.equals("Pescados y Mariscos")) {
                    categoria = "Pescados y Mariscos";
                } else if (activity.equals("Sushi")) {
                    categoria = "Sushi";
                }else if (activity.equals("Cafetería")){
                    categoria = "Cafeteria";

                } else if(activity.equals("Postres y Tortas")){
                    categoria = "Postres y Tortas";

                }else if(activity.equals("Sandwiches")){
                    categoria = "Sandwiches";

                } else {
                    categoria = "Comida Rápida";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categoria = "Comida Rápida";
            }
        });

        //Para la hora inicio y fin pista1:"El tercer perro de un castillo de espadas"
        tiHinicio = (TextInputEditText) findViewById(R.id.hora_inicio);
        tiHfin = (TextInputEditText) findViewById(R.id.hora_fin);
        tiHinicio.setOnClickListener(this::onClick);
        tiHfin.setOnClickListener(this::onClick);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String time = dateFormat.format(new Date());
        ImageView imageView = findViewById(R.id.imgLogo);
        TextView tvLogo = findViewById(R.id.tv_logo);
        ImageView imageView2 = findViewById(R.id.imgBanner);
        TextView tvBanner = findViewById(R.id.tv_banner);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastRequestWasForLogo = true;
                lastRequestWasForBanner = false;
                if (checkPermissions()) {
                    openGalleryOrCamera();
                }
            }
        });

        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lastRequestWasForLogo = false;
                lastRequestWasForBanner = true;
                if (checkPermissions()) {
                    openGalleryOrCamera2();
                }
            }
        });

        //Manejo de botones
        restauranteNombre = findViewById(R.id.restauranteNombre); //Se obtiene el nombre
        TextInputLayout nameLayout = findViewById(R.id.inputLayout);
        btContinuar = (Button) findViewById(R.id.continuar1);
        btContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = restauranteNombre.getText().toString().trim();
                String horaInicio = tiHinicio.getText().toString().trim();
                String horaFin = tiHfin.getText().toString().trim();
                Log.d("Registro", "Nombre del restaurante: " + nombre);
                Restaurante restA = new Restaurante();
                restA.setNombre(nombre);
                restA.setCategorias(categoria);
                boolean validInput = true;

                try {
                    if (nombre.isEmpty()) {
                        restauranteNombre.setError("Completar este campo");
                        validInput = false;
                    }

                    if (horaInicio.isEmpty()) {
                        tiHinicio.setError("Completar este campo");
                        validInput = false;
                    }

                    if (horaFin.isEmpty()) {
                        tiHfin.setError("Completar este campo");
                        validInput = false;
                    }

                    if (!horaInicio.isEmpty() && !horaFin.isEmpty()) {
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                        Date inicio = timeFormat.parse(horaInicio);
                        Date fin = timeFormat.parse(horaFin);

                        if (inicio != null && fin != null && !inicio.before(fin)) {
                            tiHinicio.setError("La hora de apertura debe ser menor que la hora de cierre");
                            validInput = false;
                        }
                    }

                    if (validInput) {
                        restA.setHorario(horaInicio+" - "+horaFin);

                        Intent intent = new Intent(SuperAdminRegistroRestaurante1.this, SuperAdminRegistroRestaurante2.class);
                        intent.putExtra("nameR", restA);

                        // Enviar la imagen seleccionada o capturada
                        if (imageUri != null) {
                            intent.putExtra("imageUri", imageUri.toString());
                            Log.d("DEBUG_IMAGE", "imageUri: " + imageUri.toString());
                        } else if (imageBitmap != null) {
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] byteArray = baos.toByteArray();
                            intent.putExtra("imageBitmap", byteArray);
                            Log.d("DEBUG_IMAGE", "Bitmap enviado.");
                        }
                        //banner
                        if (imageUri2 != null) {
                            intent.putExtra("imageUri2", imageUri2.toString());
                            Log.d("DEBUG_IMAGE", "imageUri2: " + imageUri2.toString());
                        } else if (imageBitmap2 != null) {
                            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                            imageBitmap2.compress(Bitmap.CompressFormat.JPEG, 100, baos2);
                            byte[] byteArray = baos2.toByteArray();
                            intent.putExtra("imageBitmap2", byteArray);
                            Log.d("DEBUG_IMAGE", "Bitmap enviado.");
                        }
                        startActivity(intent);
                        //vistaRegistroRestaurante2(restA,sa);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!validInput) return;
            }
        });

        btCancelar = (Button) findViewById(R.id.cancelar1);
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vistaPanelRestaurante();
            }
        });
        FloatingActionButton btAtras = findViewById(R.id.bt_atras);

        btAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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

    private void openGalleryOrCamera2() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooser = Intent.createChooser(pickPhotoIntent, "Selecciona la opción");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{takePictureIntent});

        startActivityForResult(chooser,REQUEST_IMAGE_PICK_BANNER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Decide cuál acción tomar según el contexto
                if (lastRequestWasForLogo) {
                    openGalleryOrCamera(); // para logo
                } else if (lastRequestWasForBanner) {
                    openGalleryOrCamera2(); // Abre cámara o galería para banner
                }
            } else {
                Log.e("DEBUG_PERMISSION", "Permisos denegados por el usuario.");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            // Manejo para logo
            if (requestCode == REQUEST_IMAGE_CAPTURE_LOGO || requestCode == REQUEST_IMAGE_PICK_LOGO) {
                handleImageResult(data, findViewById(R.id.imgLogo), findViewById(R.id.tv_logo));
            }
            // Manejo para banner
            else if (requestCode == REQUEST_IMAGE_CAPTURE_BANNER || requestCode == REQUEST_IMAGE_PICK_BANNER) {
                handleImageResult(data, findViewById(R.id.imgBanner), findViewById(R.id.tv_banner));
            }
            // Caso no manejado
            else {
                Log.e("DEBUG_IMAGE", "Código de solicitud no manejado: " + requestCode);
            }
        } else {
            Log.e("DEBUG_IMAGE", "onActivityResult recibió un código de resultado no OK o data nula.");
        }
    }

    private void handleImageResult(Intent data, ImageView imageView, TextView textView) {
        try {
            if (data.getExtras() != null && data.getExtras().get("data") instanceof Bitmap) {
                // Imagen capturada por la cámara
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                textView.setVisibility(View.INVISIBLE);

                // Almacena la imagen dependiendo del tipo
                if (imageView.getId() == R.id.imgLogo) {
                    imageBitmap = bitmap;
                    imageUri = null; // Limpia cualquier URI previo
                } else if (imageView.getId() == R.id.imgBanner) {
                    imageBitmap2 = bitmap;
                    imageUri2 = null; // Limpia cualquier URI previo
                }
            } else if (data.getData() != null) {
                // Imagen seleccionada de la galería
                Uri uri = data.getData();
                imageView.setImageURI(uri);
                textView.setVisibility(View.INVISIBLE);

                // Almacena la imagen dependiendo del tipo
                if (imageView.getId() == R.id.imgLogo) {
                    imageUri = uri;
                    imageBitmap = null; // Limpia cualquier Bitmap previo
                } else if (imageView.getId() == R.id.imgBanner) {
                    imageUri2 = uri;
                    imageBitmap2 = null; // Limpia cualquier Bitmap previo
                }
            } else {
                Log.e("DEBUG_IMAGE", "No se recibió imagen válida.");
            }
        } catch (Exception e) {
            Log.e("DEBUG_IMAGE", "Error al procesar la imagen: ", e);
        }
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

    //Manejo de los datos
    public void onClick(View v){
        if(v==tiHinicio){
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minuto = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                    tiHinicio.setText(hoursOfDay + ":" + minute);
                }
            }, hora, minuto, false);
            timePickerDialog.show();
        }

        if(v==tiHfin){
            final Calendar calendar = Calendar.getInstance();
            hora = calendar.get(Calendar.HOUR_OF_DAY);
            minuto = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int hoursOfDay, int minute) {
                    tiHfin.setText(hoursOfDay + ":" + minute);
                }
            }, hora, minuto, false);
            timePickerDialog.show();
        }
    }
    //Cambiar vista
    public void vistaRegistroRestaurante2(Restaurante restR, Usuario sa){
        Intent intent = new Intent(this, SuperAdminRegistroRestaurante2.class);
        intent.putExtra("nameR", restR);
        intent.putExtra("sa",sa);
        startActivity(intent);
        finish();
    }

    public void vistaPanelRestaurante(){
        Intent intent = new Intent(this, SuperAdminRestaurante.class);
        startActivity(intent);
        finish();
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
}