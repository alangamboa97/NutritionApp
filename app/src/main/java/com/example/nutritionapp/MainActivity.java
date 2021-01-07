package com.example.nutritionapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnRegister;
    Button btnLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar;
        actionBar = getSupportActionBar();
        actionBar.hide();//quitamos el action Bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#0174df"));
        actionBar.setBackgroundDrawable(colorDrawable);

        //iniciamos variable de botones
        btnRegister = (Button)findViewById(R.id.register_btn);
        btnLogin =  findViewById(R.id.login_btn);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciar RegisterActivity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //iniciar loginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

    }



}