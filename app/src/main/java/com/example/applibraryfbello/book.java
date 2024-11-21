package com.example.applibraryfbello;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.ktx.Firebase;

import java.util.HashMap;
import java.util.Map;

public class book extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    EditText idbook, author, name;
    Switch sAvailable;
    Button bSave, bSearch, bEdit, bDelete, blist;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        idbook = findViewById(R.id.etidBook);
        name = findViewById(R.id.etName);
        author = findViewById(R.id.etAuthor);
        sAvailable = findViewById(R.id.swAvalable);
        message = findViewById(R.id.tvMessageB);
        bSave = findViewById(R.id.btnSavee);
        bSearch = findViewById(R.id.btnSearch);
        bEdit = findViewById(R.id.btnedit);
        bDelete = findViewById(R.id.btndelete);
        blist = findViewById(R.id.btnlist);

        // Evento de buscar libro
        bSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!idbook.getText().toString().isEmpty()) {
                    db.collection("book")
                            .whereEqualTo("idbook", idbook.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) { // Si lo encuentra
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                // Asignar el contenido de los campos a los datos de pantalla
                                                name.setText(document.getString("name"));
                                                author.setText(document.getString("author"));
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
                String mIdBook = idbook.getText().toString();
                String mName = name.getText().toString();
                String mAuthor = author.getText().toString();
                int mAvailable = sAvailable.isChecked() ? 1 : 0;

                if (checkData(mIdBook, mName, mAuthor)) {
                    db.collection("book")
                            .whereEqualTo("idbook", idbook.getText().toString())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (task.getResult().isEmpty()) {
                                            Map<String, Object> mapBook = new HashMap<>();
                                            mapBook.put("idbook", mIdBook);
                                            mapBook.put("name", mName);
                                            mapBook.put("author", mAuthor);
                                            mapBook.put("available", mAvailable);
                                            db.collection("book")
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
                String mIdBook = idbook.getText().toString();
                String mName = name.getText().toString();
                String mAuthor = author.getText().toString();
                int mAvailable = sAvailable.isChecked() ? 1 : 0;

                if (checkData(mIdBook, mName, mAuthor)) {
                    db.collection("book")
                            .whereEqualTo("idbook", mIdBook)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) { // Si el libro existe
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String docId = document.getId(); // Obtener el ID del documento
                                                db.collection("book").document(docId)
                                                        .update(
                                                                "name", mName,
                                                                "author", mAuthor,
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
                                                                message.setText("Error al actualizar el libro. Inténtelo más tarde.");
                                                            }
                                                        });
                                            }
                                        } else {
                                            message.setTextColor(Color.parseColor("#E6370A"));
                                            message.setText("El libro con este ID no existe.");
                                        }
                                    } else {
                                        message.setTextColor(Color.parseColor("#E6370A"));
                                        message.setText("Error al buscar el libro.");
                                    }
                                }
                            });
                } else {
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe diligenciar todos los datos.");
                }
            }
        });

        // Evento de eliminar libro
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mIdBook = idbook.getText().toString();

                if (!mIdBook.isEmpty()) {
                    db.collection("book")
                            .whereEqualTo("idbook", mIdBook)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        if (!task.getResult().isEmpty()) { // Si el libro existe
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                String docId = document.getId(); // Obtener el ID del documento
                                                db.collection("book").document(docId)
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
                                                                message.setText("Error al eliminar el libro. Inténtelo más tarde.");
                                                            }
                                                        });
                                            }
                                        } else {
                                            message.setTextColor(Color.parseColor("#E6370A"));
                                            message.setText("El libro con este ID no existe.");
                                        }
                                    } else {
                                        message.setTextColor(Color.parseColor("#E6370A"));
                                        message.setText("Error al buscar el libro.");
                                    }
                                }
                            });
                } else {
                    message.setTextColor(Color.parseColor("#FF4545"));
                    message.setText("Debe ingresar el id del libro a eliminar.");
                }
            }
        });

    }

    private boolean checkData(String mIdBook, String mName, String mAuthor) {
        return !mIdBook.isEmpty() && !mName.isEmpty() && !mAuthor.isEmpty();
    }
}




