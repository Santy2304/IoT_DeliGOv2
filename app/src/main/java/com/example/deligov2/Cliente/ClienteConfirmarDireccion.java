package com.example.deligov2.Cliente;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.DTO.ReporteCliente;
import com.example.deligov2.DTO.Usuario;
import com.example.deligov2.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class ClienteConfirmarDireccion extends AppCompatActivity implements OnMapReadyCallback {
    //FIREBASE
    FirebaseAuth firebaseAuth;
    private Usuario usuario;
    FirebaseUser user;
    FirebaseFirestore db;
    //FIREBASE

    //MANEJO DE MAPAS
    private LatLng selectedLocation;
    private boolean direccionValida = false;
    private GoogleMap mMap;
    private TextInputEditText address;
    //MANEJO DE MAPAS

    //GESTIONAR EL MAPA
    Button confirmarButton;
    Carrito carrito;
    ArrayList<Platillo> lista = new ArrayList<>();
    //GESTIONAR EL MAPA

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //FIREBASE
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        //FIREBASE

        //CARGA LA VISTA
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_confirmar_direccion);
        //CARGA LA VISTA
        //OBTENEMOS ELEMENTOS DE LA VISTA
        confirmarButton = findViewById(R.id.confirm_Button);
        address =  findViewById(R.id.address);
        Intent intent = getIntent();
        //CARGAMOS EL MAPA
        ArrayList<Float> listaPrecios =  (ArrayList<Float>) intent.getSerializableExtra("listaPrecios");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        //RECOGE CAMBIOS EN EL INPUTTEXT PARA PODER MOSTRARLO EN EL MAPA
        address.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();
            private final long DELAY = 1000;
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                timer.cancel(); // Reinicia el temporizador si el usuario sigue escribiendo
            }

            @Override
            public void afterTextChanged(Editable editable) {
                timer.cancel(); // Reinicia el temporizador si el usuario sigue escribiendo
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        // Aquí se ejecuta la acción cuando el usuario deja de escribir
                        runOnUiThread(() -> {
                            // Código a ejecutar en el hilo principal
                            searchAddress(address.getText().toString());
                            Log.d("Input", "El usuario dejó de escribir. Texto actual: " + address.getText().toString());
                        });
                    }
                }, DELAY); // Ejecuta después del tiempo definido en DELAY
            }
        });
        //DESPUES DE CARGAR LOS DATOS DEL USUARIO
        loadUser(()->{

            ((TextView)findViewById(R.id.ubi)).setText(usuario.getDireccion());
            db.collection("Carritos").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            carrito = documentSnapshot.toObject(Carrito.class);
                            ArrayList<Integer> listaCantidadesUWU = carrito.getListaCantidades();
                            confirmarButton.setOnClickListener(view -> {
                                Pedido pedido = new Pedido();
                                if(direccionValida  && address.getText().toString().trim()!=null){
                                    pedido.setDireccion(address.getText().toString());
                                    pedido.setLatitud("" + selectedLocation.latitude);
                                    pedido.setLongitud("" + selectedLocation.longitude);
                                }else{
                                 pedido.setDireccion(usuario.getDireccion());
                                 pedido.setLongitud(usuario.getLongitud());
                                 pedido.setLatitud(usuario.getLatitud());
                                }
                                pedido.setIdRestaurante(carrito.getIdRestaurante());
                                pedido.setIdListaPlatos(carrito.getIdListaPlatos());

                                for(int i=0;i<carrito.getIdListaPlatos().size();i++){
                                    String id = carrito.getIdListaPlatos().get(i);
                                    int cantidadVendida = carrito.getListaCantidades().get(i);
                                    float cantidadRecaudado = cantidadVendida*listaPrecios.get(i);
                                    db.collection("Platos").document(carrito.getIdListaPlatos().get(i)).get()
                                            .addOnSuccessListener(documentSnapshot1 -> {
                                                if (documentSnapshot1.exists()) {
                                                    Platillo platillo = documentSnapshot1.toObject(Platillo.class);
                                                    db.collection("Platos").document(id)
                                                            .update("cantVentaTotal", cantidadVendida + platillo.getCantVentaTotal(),
                                                                    "cantRecaudadoTotal",cantidadRecaudado+platillo.getCantRecaudadoTotal())
                                                            .addOnSuccessListener(aVoid -> Log.d("Firestore", "cantVenta actualizada correctamente para " ))
                                                            .addOnFailureListener(e -> Log.e("Firestore", "Error al actualizar cantVenta", e));
                                                }
                                            })
                                            .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

                                }


                                ReporteCliente reporteCliente = new ReporteCliente();
                                reporteCliente.setIdCliente(user.getUid());
                                reporteCliente.setIdRestaurante(carrito.getIdRestaurante());

                                db.collection("ReportesClientes")
                                        .whereEqualTo("idCliente", reporteCliente.getIdCliente())
                                        .whereEqualTo("idRestaurante", reporteCliente.getIdRestaurante())
                                        .get()
                                        .addOnSuccessListener(querySnapshot -> {
                                            if (!querySnapshot.isEmpty()) {
                                                DocumentSnapshot documentSnapshot1 = querySnapshot.getDocuments().get(0);
                                                String docId = documentSnapshot1.getId();
                                                ReporteCliente reporteExistente = documentSnapshot1.toObject(ReporteCliente.class);

                                                if (reporteExistente != null) {
                                                    int nuevaCantidadPedidos = reporteExistente.getCantidadPedidos() + 1;
                                                    float nuevoTotalGastado = reporteExistente.getTotalGastado();

                                                    for (int i = 0; i <listaCantidadesUWU.size(); i++) {
                                                        int cantidadVendida = listaCantidadesUWU.get(i);
                                                        nuevoTotalGastado += cantidadVendida * listaPrecios.get(i);
                                                    }

                                                    Timestamp ultimoPedido = Timestamp.now();

                                                    db.collection("ReportesClientes").document(docId)
                                                            .update(
                                                                    "cantidadPedidos", nuevaCantidadPedidos,
                                                                    "totalGastado", nuevoTotalGastado,
                                                                    "ultimoPedido", ultimoPedido
                                                            )
                                                            .addOnSuccessListener(aVoid -> {
                                                                Log.d("Firestore", "Documento actualizado correctamente");
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Log.e("Firestore", "Error al actualizar el documento", e);
                                                            });
                                                }
                                            } else {
                                                reporteCliente.setCantidadPedidos(1);
                                                float totalGastado = 0;
                                                for (int i = 0; i < listaCantidadesUWU.size(); i++) {
                                                    int cantidadVendida = listaCantidadesUWU.get(i);
                                                    totalGastado += cantidadVendida * listaPrecios.get(i);
                                                }
                                                reporteCliente.setTotalGastado(totalGastado);
                                                reporteCliente.setUltimoPedido(Timestamp.now());

                                                db.collection("ReportesClientes").add(reporteCliente)
                                                        .addOnSuccessListener(documentReference -> {
                                                            Log.d("Firestore", "Documento creado con ID: " + documentReference.getId());
                                                        })
                                                        .addOnFailureListener(e -> {
                                                            Log.e("Firestore", "Error al crear el documento", e);
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error al realizar la consulta", e);
                                        });

                                pedido.setPreciosActuales(listaPrecios);
                                pedido.setId(generarIdAleatorio());
                                pedido.setListaCantidades(carrito.getListaCantidades());
                                pedido.setIdUsuario(user.getUid());
                                pedido.setEstado("Recibido");
                                pedido.setHora(Timestamp.now());
                                pedido.setCostoEnvio(carrito.getCostoEnvio());
                                Bitmap qrBitmap = generarQRCode(generarIdAleatorio());
                                guardarQRCodeEnFirebase(qrBitmap, pedido.getId());
                                db.collection("Pedidos").document(pedido.getId())
                                        .set(pedido)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Pedido realizado exitosamente", Toast.LENGTH_SHORT).show();

                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(this, "Error al realizar el pedido: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                        });

                                carrito.setIdListaPlatos(new ArrayList<>());
                                carrito.setListaCantidades(new ArrayList<>());
                                carrito.setIdRestaurante("");
                                db.collection("Carritos").document(user.getUid())
                                        .set(carrito)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Carrito vaciado.", Toast.LENGTH_SHORT).show();
                                        });
                                Intent intent1 = new Intent(this,ClienteConfirmacionCompra.class);
                                intent1.putExtra("id",pedido.getId());
                                startActivity(intent1);
                                finish();
                            });

                        }
                    })
                    .addOnFailureListener(e -> Log.e("Firestore", "Error al buscar usuario", e));

        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.restaurant){
                    Intent intentRestaurant = new Intent(ClienteConfirmarDireccion.this, ClienteHomeActivity.class);
                    startActivity(intentRestaurant);
                    return true;
                }else if(item.getItemId()==R.id.historial){
                    Intent intentPrincipal = new Intent(ClienteConfirmarDireccion.this, ClienteHistorialActivity.class);
                    startActivity(intentPrincipal);
                    return true;
                }else if(item.getItemId()==R.id.profile){
                    Intent intentProfile = new Intent(ClienteConfirmarDireccion.this, ClientePerfil.class);
                    startActivity(intentProfile);
                    return true;
                }else{
                    return false;
                }

            }
        });
    }


    public static String generarIdAleatorio() {
        Random random = new Random();
        String letras = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"; // Letras mayúsculas
        String numeros = "0123456789"; // Números

        char letra1 = letras.charAt(random.nextInt(letras.length()));
        char letra2 = letras.charAt(random.nextInt(letras.length()));

        char numero1 = numeros.charAt(random.nextInt(numeros.length()));
        char numero2 = numeros.charAt(random.nextInt(numeros.length()));

        char[] idArray = {letra1, letra2, numero1, numero2};
        for (int i = idArray.length - 1; i > 0; i--) {
            int index = random.nextInt(i + 1);
            char temp = idArray[index];
            idArray[index] = idArray[i];
            idArray[i] = temp;
        }

        return new String(idArray);
    }
    public void guardarQRCodeEnFirebase(Bitmap qrBitmap, String idPedido) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        qrBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();

        String nombreArchivo = "pedidos/" + idPedido + "/qr.jpg";
        StorageReference qrCodeRef = storageRef.child(nombreArchivo);

        UploadTask uploadTask = qrCodeRef.putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            qrCodeRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.d("FirebaseStorage", "QR Code guardado: " + downloadUrl);
            });
        }).addOnFailureListener(e -> {
            e.printStackTrace();
            Log.e("FirebaseStorage", "Error al guardar el QR Code en Firebase Storage");
        });
    }
    public Bitmap generarQRCode(String texto) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
            int tamaño = 500;
            com.google.zxing.common.BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, tamaño, tamaño);
            Bitmap bitmap = Bitmap.createBitmap(tamaño, tamaño, Bitmap.Config.RGB_565);
            for (int x = 0; x < tamaño; x++) {
                for (int y = 0; y < tamaño; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? android.graphics.Color.BLACK : android.graphics.Color.WHITE);
                }
            }
            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    public void loadUser(Runnable onSuccess){
        db.collection("Usuarios")
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        for (QueryDocumentSnapshot document : value) {
                            if(((document.toObject(Usuario.class)).getId()).equals(user.getUid())){
                                usuario = document.toObject(Usuario.class);
                            }
                        }
                        onSuccess.run();
                    }
                });
    }

    //GESTIONA EL MAPA
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng defaultLocation = new LatLng(-12.0464, -77.0428); // Lima, Perú
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 15));
        mMap.setOnMapClickListener(latLng -> {
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(latLng).title("Ubicación seleccionada"));
            selectedLocation = latLng;
            address.setText(obtenerDireccionDesdeLatLng(selectedLocation));
        });
    }
    private String obtenerDireccionDesdeLatLng(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> direcciones = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (direcciones != null && !direcciones.isEmpty()) {
                Address direccion = direcciones.get(0);
                direccionValida =true;
                return direccion.getAddressLine(0);
            }
        } catch (IOException e) {
            Log.e("GeocoderError", "No se pudo obtener la dirección", e);
            direccionValida =false;
        }
        return null;
    }
    public void searchAddress(String  address) {
        if (!address.isEmpty()) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocationName(address, 1);
                if (addresses != null && !addresses.isEmpty()) {
                    Address location = addresses.get(0);
                    selectedLocation = new LatLng(location.getLatitude(), location.getLongitude());
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions().position(selectedLocation).title("Ubicación encontrada"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
                    direccionValida =true;
                } else {
                    direccionValida =false;
                    Toast.makeText(this, "No se encontró la dirección", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al buscar la dirección", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Por favor, ingresa una dirección", Toast.LENGTH_SHORT).show();
        }
    }
    //GESTIONA EL MAPA

    //GESTION DE LOS BOTONES DE LA BARRA DE ABAJO
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
    //GESTION DE LOS BOTONES DE LA BARRA DE ABAJO
}