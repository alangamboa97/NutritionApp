package com.example.nutritionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    EditText nameText, emailText, psswdText;
    Button btnReg;
    TextView textAccount;
    private FirebaseAuth mAuth;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Crear Cuenta");
        //actionBar.hide();
        //boton regresar(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#30EDF0"));
        actionBar.setBackgroundDrawable(colorDrawable);

        nameText = findViewById(R.id.textName);
        emailText = findViewById(R.id.textEmail);
        psswdText = findViewById(R.id.textPsswd);
        btnReg = (Button)findViewById(R.id.btn_reg);
        textAccount = findViewById(R.id.have_account);
        mAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registrando...");

        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = nameText.getText().toString().trim();
                String email = emailText.getText().toString().trim();
                String psswd = psswdText.getText().toString().trim();

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    emailText.setError("Email Inválido");
                    emailText.setFocusable(true);

                }
                else if(psswd.length() < 6){
                    psswdText.setError("Contrasen debe ser almenos 6 caracteres");
                    psswdText   .setFocusable(true);

                }else{
                    registerUser(nombre, email, psswd);
                }

            }
        });

        textAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });




    }

    private void registerUser(String nombre, String email, String psswd) {

        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, psswd)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            FirebaseUser user = mAuth.getCurrentUser();
                            String email = user.getEmail();
                            String uid = user.getUid();

                            HashMap<Object,String> hashMap = new HashMap<>();
                            hashMap.put("email", email);
                            hashMap.put("uid", uid);
                            hashMap.put("name", "");
                            hashMap.put("image", "");
                            hashMap.put("cover", "");

                            //creacion de la base de datos de Firebase
                            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

                            //para guardar  la informacioon de user
                            DatabaseReference reference = firebaseDatabase.getReference("Users");

                            reference.child(uid).setValue(hashMap);


                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registrado...\n" + user.getEmail(),Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
                                    finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(RegisterActivity.this, "" +e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();  //ir a actividad previa
        return super.onSupportNavigateUp();
    }
}