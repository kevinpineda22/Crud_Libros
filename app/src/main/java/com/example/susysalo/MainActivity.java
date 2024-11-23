package com.example.susysalo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    // instanciar
    EditText email, password;
    TextView message;
    Button login, register;
    FirebaseAuth xAuth= FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Referenciar
        email = findViewById(R.id.etEmail);
        password = findViewById(R.id.etPassword);
        message = findViewById(R.id.tvMessage);
        login = findViewById(R.id.btnLogin);
        register = findViewById(R.id.btnRegister);
        // Eventos
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             xAuth
                     .signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                     .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                         @Override
                         public void onComplete(@NonNull Task<AuthResult> task) {
                             if (task.isSuccessful()){
                                 startActivity(new Intent(getApplicationContext(),book.class));
                             }
                             else {
                                 message.setTextColor(Color.parseColor("#FF4545"));
                                 message.setText("Correo y/o contraseña incorrecta (s). intentelo de nuevo");

                             }
                         }
                     });
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),register.class));
            }
        });
    }
}