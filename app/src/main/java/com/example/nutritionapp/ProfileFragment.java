package com.example.nutritionapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.view.KeyCharacterMap.load;

public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ImageView avatarIv, coverIv;
    TextView nameTv, emailTv;
    FloatingActionButton fab;
    ProgressDialog pd;

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_CAMERA_REQUEST_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_REQUEST_CODE = 400;

    String cameraPermissions[];
    String storagePermissions[];

    public ProfileFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");

        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE};


        avatarIv = view.findViewById(R.id.avatarIv);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        emailTv = view.findViewById(R.id.emailTv);
        fab = view.findViewById(R.id.fab);
        pd = new ProgressDialog(getActivity());

            Query query= databaseReference.orderByChild("email").equalTo(user.getEmail());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds: snapshot.getChildren()){
                        String name = ""+ds.child("name").getValue();
                        String email = ""+ds.child("email").getValue();
                        String image = ""+ds.child("image").getValue();
                        String cover = ""+ds.child("cover").getValue();

                        nameTv.setText(name);
                        emailTv.setText(email);

                        try{
                            Picasso.get().load(image).into(avatarIv);
                        }catch (Exception e){
                            Picasso.get().load(R.drawable.ic_default_img_white).into(avatarIv);
                        }
                        try{
                            Picasso.get().load(cover).into(coverIv);
                        }catch (Exception e){
                        }

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            fab.setOnClickListener((v)->{showEditProfileDialog(); });

            return view;
        }

            private boolean checkStoragePermission(){
                boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        ==(PackageManager.PERMISSION_GRANTED);
                return result;
            }

                private void requestStoragePermission(){
                    ActivityCompat.requestPermissions(getActivity(), storagePermissions, STORAGE_REQUEST_CODE);
                }

                private boolean checkCameraPermission(){
                    boolean result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            ==(PackageManager.PERMISSION_GRANTED);
                    return result;
                }

                private void requestCameraPermission(){
                    ActivityCompat.requestPermissions(getActivity(), cameraPermissions, CAMERA_REQUEST_CODE);
                }

                private void showEditProfileDialog() {

                    String options[] = {"Editar Foto de Perfil", "Editar Foto de Portada", "Editar Nombre"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Choose Action");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){
                                pd.setMessage("Actualizando Foto de Perfil ");
                                showImageDialog();
                            }else if (which == 1){
                                pd.setMessage("Actualizando Foto de Portada ");

                            }
                            else if (which==2){
                                pd.setMessage("Actualizando Nombre ");

                            }

                        }
                    });
                    builder.create().show();
                }

                private void showImageDialog() {
                    String options[] = {"Cámara", "Galería"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Escoger desde");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which==0){

                            }else if (which == 1){

                            }

                        }
                    });
                    builder.create().show();

                }
            }