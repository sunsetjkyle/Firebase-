package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth regAuth;
    private EditText register_email, register_password;
    private AppCompatButton register;
    private TextView reg_txt_login;
    FirebaseUser User;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        regAuth = FirebaseAuth.getInstance();

        reg_txt_login= findViewById(R.id.reg_login_txt);
        reg_txt_login.setOnClickListener(view -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });


        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);

        register = findViewById(R.id.register);
        register.setOnClickListener(view -> {
            String txt_email = register_email.getText().toString();
            String txt_password = register_password.getText().toString();
            if (TextUtils.isEmpty(txt_email)||TextUtils.isEmpty(txt_password)){
                Toast.makeText(RegisterActivity.this, "Can´t be empty!", Toast.LENGTH_SHORT).show();
            }
            else if (txt_password.length()<6){
                Toast.makeText(RegisterActivity.this, "Too short!", Toast.LENGTH_SHORT).show();
            }
            else {
                createNewUser(txt_email, txt_password);
            }
        });

    }

    private void createNewUser(String txt_email, String txt_password) {
        regAuth.createUserWithEmailAndPassword(txt_email, txt_password).addOnCompleteListener(RegisterActivity.this, task -> {
            if (task.isSuccessful()) {
                sendVerificationEmail();
            }
            else{
                Toast.makeText(RegisterActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
            }

        });

    }

    private void sendVerificationEmail() {
        User=regAuth.getCurrentUser();
        if (User!=null){
            User.sendEmailVerification().addOnSuccessListener(RegisterActivity.this, unused -> {
                Toast.makeText(RegisterActivity.this, "Verification email sent successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, SetupActivity.class));
                finish();
            }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Failed" , Toast.LENGTH_SHORT).show());
        }

    }
}