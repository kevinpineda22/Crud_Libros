package com.example.applibraryfbello;

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

public class register extends AppCompatActivity {
    EditText email, password;
    TextView message;
    Button register;
    // Instanciar a FirebaseAuthentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        email = findViewById(R.id.etEmailR);
        password = findViewById(R.id.etPasswordR);
        message = findViewById(R.id.tvMessageR);
        register = findViewById(R.id.btnRegisterR);
        // Eventos
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mEmail = email.getText().toString();
                String mPassword = password.getText().toString();
                if (!mEmail.isEmpty() && !mPassword.isEmpty()){
                    mAuth
                            .createUserWithEmailAndPassword(mEmail, mPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        message.setTextColor(Color.parseColor("#347928"));
                                        message.setText("Cuenta creada correctamente.");
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), book.class));
                                    }
                                    else{
                                        message.setTextColor(Color.parseColor("#C62E2E"));
                                        message.setText("No se creó la cuenta. Inténtelo de nuevo.");
                                    }
                                }
                            });
                }
                else{
                    message.setTextColor(Color.parseColor("#C62E2E"));
                    message.setText("Debe ingresar todos los datos");
                }
            }
        });

    }
}