package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class LandngActivity extends AppCompatActivity {
    private AppCompatButton create;
    private TextView txt_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landng);
        create = findViewById(R.id.btn_create);
        txt_login = findViewById(R.id.txt_login);

        create.setOnClickListener(view -> {
            startActivity(new Intent(LandngActivity.this, RegisterActivity.class));
            finish();

        });

        txt_login.setOnClickListener(view -> {
            startActivity(new Intent(LandngActivity.this, LoginActivity.class));
            finish();
        });
    }
}