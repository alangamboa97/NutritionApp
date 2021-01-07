package com.example.nutritionapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

public class PostActivity extends AppCompatActivity {

    private ImageButton mselectImage;
    private EditText mSemana;
    private EditText mcircBrazo;
    private EditText mcircCintura;
    private EditText mcircCadera;
    private EditText mcircPantorrilla;
    private EditText mcircMuslo;

    private StorageReference mStorage;
    Uri mImageURI = null;
    private ProgressDialog mProgress;
    private DatabaseReference mDataBase;
   



    private static final int GALLERY_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorage = FirebaseStorage.getInstance().getReference();
        mDataBase = FirebaseDatabase.getInstance().getReference().child("Posts");


        mselectImage = (ImageButton) findViewById(R.id.imageButton);

        mSemana = (EditText) findViewById(R.id.semanaEditText);
        mcircBrazo = (EditText) findViewById(R.id.circBrazoEditText);
        mcircCintura = (EditText) findViewById(R.id.circCinturaEditText);
        mcircCadera= (EditText) findViewById(R.id.circCaderaEditText);
        mcircPantorrilla = (EditText) findViewById(R.id.circPantorrillaEditText);
        mcircMuslo = (EditText) findViewById(R.id.circMusloEditText);

        mProgress = new ProgressDialog(this);

        mselectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleyIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleyIntent.setType("image/*");
                startActivityForResult(galleyIntent, GALLERY_REQUEST);
            }
        });


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPosting();
            }
        });
    }

    private void startPosting() {
        mProgress.setMessage("Espera...");
        mProgress.show();
        String semana_val = mSemana.getText().toString().trim();
        String circBrazo_val = mcircBrazo.getText().toString().trim();
        String circCintura_val = mcircCintura.getText().toString().trim();
        String circCadera_val = mcircCadera.getText().toString().trim();
        String circPantorrilla_val = mcircPantorrilla.getText().toString().trim();
        String circMuslo_val = mcircMuslo.getText().toString().trim();


        if(!TextUtils.isEmpty(semana_val) && !TextUtils.isEmpty(circBrazo_val) &&
        !TextUtils.isEmpty(circCintura_val) && !TextUtils.isEmpty(circCadera_val) &&
        !TextUtils.isEmpty(circPantorrilla_val) && !TextUtils.isEmpty(circMuslo_val)
            && mImageURI !=null){

            StorageReference filepath = mStorage.child("Post_Image").child(mImageURI.getLastPathSegment());
            filepath.putFile(mImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Task<Uri> downloadUrl = taskSnapshot.getStorage().getDownloadUrl();

                    DatabaseReference newPost = mDataBase.push();
                    newPost.child("semana").setValue(semana_val);
                    newPost.child("circBrazo").setValue(circBrazo_val);
                    newPost.child("circCintura").setValue(circCintura_val);
                    newPost.child("circCadera").setValue(circCadera_val);
                    newPost.child("circPantorilla").setValue(circPantorrilla_val);
                    newPost.child("circMuslo").setValue(circMuslo_val);
                    newPost.child("image").setValue(downloadUrl.toString());
                    newPost.child("uid").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());

                    mProgress.dismiss();
                    startActivity(new Intent(PostActivity.this, ProfileFragment.class));

                }
            });

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK){
            mImageURI = data.getData();
            mselectImage.setImageURI(mImageURI);

        }
    }
}