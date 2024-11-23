package com.example.susysalo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
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
                if (!Referencia.getText().toString().isEmpty()) {
                    db.collection("productos")
                            .whereEqualTo("Referencia", Referencia.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) { // Si lo encuentra
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Asignar el contenido de los campos a los datos de pantalla
                                                name.setText(document.getString("name"));
                                                Unidad.setText(String.valueOf(document.getLong("Unidad")));;
                                                precio.setText(String.valueOf(document.getDouble("precio")));

                                                sAvailable.setChecked(document.getDouble("available") == 1);
                                            }
                                        } else {
                                            message.setTextColor(Color.parseColor("#E6370A"));
                                            message.setText("El id del libro NO EXISTE. Inténtelo con otro...");
                                        }
                                    }
                                }
                            });
                } else {
                    message.setTextColor(Color.parseColor("#E6370A"));
                    message.setText("Debe ingresar el id del libro para actulizar el contenido...");
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

                if (checkData(mReferencia, mName, mUnidad,mPrecio)) {
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
                                                            message.setText("Libro agregado exitosamente, con id: " + documentReference.getId());
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            message.setTextColor(Color.parseColor("#E6370A"));
                                                            message.setText("No se agregó el libro. Inténtelo más tarde...");
                                                        }
                                                    });
                                        } else {
                                            message.setTextColor(Color.parseColor("#E6370A"));
                                            message.setText("Id del libro EXISTENTE. Inténtelo con otro...");
                                        }
                                    }
                                }
                            });
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

                if (checkData(mReferencia, mName ,mUnidad,mPrecio)) {
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
                                                                message.setText("Libro actualizado exitosamente.");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                message.setTextColor(Color.parseColor("#E6370A"));
                                                                message.setText("Error al actualizar el libro.");
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            });
                }
            }
        });

        // Eliminar libro
        bDelete.setOnClickListener(new View.OnClickListener() {
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
                                        if (!task.getResult().isEmpty()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String docId = document.getId();
                                                db.collection("productos").document(docId)
                                                        .delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                message.setTextColor(Color.parseColor("#3D5300"));
                                                                message.setText("Libro eliminado exitosamente.");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                message.setTextColor(Color.parseColor("#E6370A"));
                                                                message.setText("Error al eliminar el libro.");
                                                            }
                                                        });
                                            }
                                        }
                                    }
                                }
                            });
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






