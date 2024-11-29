package com.example.deligov2.Cliente;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.deligov2.DTO.Carrito;
import com.example.deligov2.DTO.Pedido;
import com.example.deligov2.DTO.Platillo;
import com.example.deligov2.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class ClienteConfirmarDireccion extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    Button confirmarButton;
    Carrito carrito;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliente_confirmar_direccion);
        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        confirmarButton = findViewById(R.id.confirm_Button);

        Intent intent = getIntent();
        List<Platillo> listaPlatillos = (List<Platillo>) intent.getSerializableExtra("listaPlatillos");

        db.collection("Carritos").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        carrito = documentSnapshot.toObject(Carrito.class);
                        confirmarButton.setOnClickListener(view -> {
                            Pedido pedido = new Pedido();
                            pedido.setIdRestaurante(carrito.getIdRestaurante());
                            pedido.setIdListaPlatos(carrito.getIdListaPlatos());
                            ArrayList<Float> preciosActuales = new ArrayList<>();

                            for (Platillo platillo : listaPlatillos) {
                                preciosActuales.add(platillo.getPrecio());
                            }

                            pedido.setId(generarIdAleatorio());
                            pedido.setListaCantidades(carrito.getListaCantidades());
                            pedido.setIdUsuario(user.getUid());
                            pedido.setEstado("Pendiente");
                            pedido.setHora(Timestamp.now());

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

}