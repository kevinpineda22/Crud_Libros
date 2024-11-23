package com.example.susysalo;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class book extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText Referencia, name, Unidad, precio;
    Switch sAvailable;
    Button bSave, bSearch, bEdit, bDelete;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        Referencia = findViewById(R.id.etidBook);
        name = findViewById(R.id.etName);
        Unidad = findViewById(R.id.etCantidadDeLibros); // Nuevo campo
        precio = findViewById(R.id.etPrecio); // Nuevo campo
        sAvailable = findViewById(R.id.swAvalable);
        message = findViewById(R.id.tvMessageB);
        bSave = findViewById(R.id.btnSavee);
        bSearch = findViewById(R.id.btnSearch);
        bEdit = findViewById(R.id.btnedit);
        bDelete = findViewById(R.id.btndelete);


        // Evento de buscar libro
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mReferencia = Referencia.getText().toString();

                if (!mReferencia.isEmpty()) {
                    db.collection("productos")
                            .whereEqualTo("Referencia", mReferencia)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) { // Si el producto se encuentra
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Asignar los valores del producto a los campos
                                                name.setText(document.getString("name"));
                                                Unidad.setText(String.valueOf(document.getLong("Unidad")));
                                                precio.setText(String.valueOf(document.getDouble("precio")));
                                                sAvailable.setChecked(document.getDouble("available") == 1);

                                                // Mostrar mensaje de éxito
                                                message.setTextColor(Color.parseColor("#3D5300")); // Verde
                                                message.setText("¡Producto encontrado con éxito!");
                                            }
                                        } else { // Si no se encuentra el producto
                                            message.setTextColor(Color.parseColor("#E6370A")); // Rojo
                                            message.setText("Referencia del producto NO EXISTE. Inténtelo con otra.");
                                        }
                                    } else { // Si ocurre un error en la consulta
                                        message.setTextColor(Color.parseColor("#E6370A"));
                                        message.setText("Error al buscar el producto. Inténtelo nuevamente.");
                                    }
                                }
                            });
                } else { // Si la referencia está vacía
                    message.setTextColor(Color.parseColor("#E6370A")); // Rojo
                    message.setText("Ingrese la referencia del producto.");
                }
            }
        });


        // Evento de guardar libro
        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mReferencia = Referencia.getText().toString();
                String mName = name.getText().toString();
                int mUnidad = Integer.parseInt(Unidad.getText().toString());
                double mPrecio = Double.parseDouble(precio.getText().toString());
                int mAvailable = sAvailable.isChecked() ? 1 : 0;

                if (checkData(mReferencia, mName, mUnidad, mPrecio)) {
                    // Crear el diálogo de confirmación
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Confirmar")
                            .setMessage("¿Está seguro de agregar este producto?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario confirma, proceder a guardar el producto
                                    db.collection("productos")
                                            .whereEqualTo("Referencia", mReferencia)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (task.getResult().isEmpty()) {
                                                            Map<String, Object> mapBook = new HashMap<>();
                                                            mapBook.put("Referencia", mReferencia);
                                                            mapBook.put("name", mName);
                                                            mapBook.put("Unidad", mUnidad);
                                                            mapBook.put("precio", mPrecio);
                                                            mapBook.put("available", mAvailable);
                                                            db.collection("productos")
                                                                    .add(mapBook)
                                                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                                        @Override
                                                                        public void onSuccess(DocumentReference documentReference) {
                                                                            message.setTextColor(Color.parseColor("#3D5300"));
                                                                            message.setText("Producto agregado exitosamente");
                                                                        }
                                                                    })
                                                                    .addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            message.setTextColor(Color.parseColor("#E6370A"));
                                                                            message.setText("No se agregó el Producto. Inténtelo más tarde...");
                                                                        }
                                                                    });
                                                        } else {
                                                            message.setTextColor(Color.parseColor("#E6370A"));
                                                            message.setText("Referencia del Producto EXISTENTE. Inténtelo con otro..");
                                                        }
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario cancela, no hacer nada
                                    dialog.dismiss();
                                }
                            })
                            .show(); // Mostrar el diálogo
                } else {
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe diligenciar todos los datos...");
                }
            }
        });

        // Evento de editar libro
        bEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mReferencia = Referencia.getText().toString();
                String mName = name.getText().toString();
                int mUnidad = Integer.parseInt(Unidad.getText().toString());
                double mPrecio = Double.parseDouble(precio.getText().toString());
                int mAvailable = sAvailable.isChecked() ? 1 : 0;

                // Verificar si los datos son válidos
                if (checkData(mReferencia, mName, mUnidad, mPrecio)) {

                    // Crear el diálogo de confirmación
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Confirmar")
                            .setMessage("¿Seguro que deseas editar este producto?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario confirma, proceder a actualizar el producto
                                    db.collection("productos")
                                            .whereEqualTo("Referencia", mReferencia)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().isEmpty()) {
                                                            // Si la referencia existe, actualizar el producto
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String docId = document.getId();
                                                                db.collection("productos").document(docId)
                                                                        .update(
                                                                                "name", mName,
                                                                                "Unidad", mUnidad,
                                                                                "precio", mPrecio,
                                                                                "available", mAvailable
                                                                        )
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                message.setTextColor(Color.parseColor("#3D5300"));
                                                                                message.setText("Producto actualizado exitosamente.");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                message.setTextColor(Color.parseColor("#E6370A"));
                                                                                message.setText("Error al actualizar el producto.");
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            // Si la referencia no existe, mostrar un mensaje de error
                                                            message.setTextColor(Color.parseColor("#E6370A"));
                                                            message.setText("No se encontró un producto con esta referencia.");
                                                        }
                                                    } else {
                                                        // Si la consulta falla, mostrar un mensaje de error
                                                        message.setTextColor(Color.parseColor("#E6370A"));
                                                        message.setText("Error al verificar el producto.");
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario cancela, no hacer nada
                                    dialog.dismiss();
                                }
                            })
                            .show(); // Mostrar el diálogo
                } else {
                    // Si los datos no son válidos, mostrar mensaje de error
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe diligenciar todos los datos...");
                }
            }
        });


// Evento de eliminar libro
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mReferencia = Referencia.getText().toString();

                if (!mReferencia.isEmpty()) {
                    // Crear el diálogo de confirmación
                    new AlertDialog.Builder(view.getContext())
                            .setTitle("Confirmar")
                            .setMessage("¿Estás seguro de que deseas eliminar este producto?")
                            .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario confirma, proceder a eliminar el producto
                                    db.collection("productos")
                                            .whereEqualTo("Referencia", mReferencia)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        if (!task.getResult().isEmpty()) {
                                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                                String docId = document.getId();
                                                                db.collection("productos").document(docId)
                                                                        .delete()
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                message.setTextColor(Color.parseColor("#3D5300"));
                                                                                message.setText("Producto eliminado exitosamente.");
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                message.setTextColor(Color.parseColor("#E6370A"));
                                                                                message.setText("Error al eliminar el producto.");
                                                                            }
                                                                        });
                                                            }
                                                        } else {
                                                            // Si la referencia no existe, mostrar un mensaje de error
                                                            message.setTextColor(Color.parseColor("#E6370A"));
                                                            message.setText("No se encontró un producto con esta referencia.");
                                                        }
                                                    } else {
                                                        // Si la consulta falla, mostrar un mensaje de error
                                                        message.setTextColor(Color.parseColor("#E6370A"));
                                                        message.setText("Error al buscar el producto.");
                                                    }
                                                }
                                            });
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Si el usuario cancela, no hacer nada
                                    dialog.dismiss();
                                }
                            })
                            .show(); // Mostrar el diálogo
                } else {
                    // Si no se ingresa referencia, mostrar mensaje de error
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe ingresar la referencia del producto para eliminarlo.");
                }
            }
        });


    }

    private boolean checkData(String mReferencia, String mName, int mUnidad, double mPrecio) {
        // Verificar que los campos no estén vacíos o no sean inválidos
        return !mReferencia.isEmpty() &&
                !mName.isEmpty() &&
                mUnidad > 0 &&  // Asegurarse de que la cantidad sea mayor a 0
                mPrecio > 0;             // Asegurarse de que el precio sea mayor a 0
    }

}






