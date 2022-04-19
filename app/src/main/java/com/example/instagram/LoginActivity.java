package com.example.instagram;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth lAuth;
    private EditText log_email, log_password;
    private AppCompatButton log_in;
    private TextView reset, txt_signup;
    private FirebaseUser firebaseUser;
    private boolean emailAddresschecker;
    String login_email = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        lAuth = FirebaseAuth.getInstance();


        log_email = findViewById(R.id.login_email);
        log_password = findViewById(R.id.login_password);

        reset = findViewById(R.id.help);
        reset.setOnClickListener(view -> {
          toResetPassword();
        });

        txt_signup = findViewById(R.id.txt_signup);
        txt_signup.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
        log_in = findViewById(R.id.login);
        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String login_email = log_email.getText().toString();
                String login_password = log_password.getText().toString();

                if (login_email.isEmpty()) {
                    log_email.setError("Cannot be empty");
                }
                if (login_password.isEmpty()){
                    log_password.setError("Please enter your password");
                }
                else {
                    signInUser(login_email, login_password);

                }
            }
        });
    }

    private void toResetPassword() {


        Intent intent = new Intent(LoginActivity.this, PasswordresetActivity.class);
        intent.putExtra("email", login_email);
        startActivity(intent);
        Toast.makeText(this, "Reset password successfully", Toast.LENGTH_SHORT).show();

    }

    private void signInUser(String login_email, String login_password) {
        lAuth.signInWithEmailAndPassword(login_email, login_password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                checkEmailAdress();

            }
            else {
                Toast.makeText(LoginActivity.this, "Failed, try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkEmailAdress() {
        firebaseUser =lAuth.getCurrentUser();
        emailAddresschecker = firebaseUser.isEmailVerified();
        if (emailAddresschecker){
            Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else
        {
            Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
            lAuth.signOut();
        }
    }
}