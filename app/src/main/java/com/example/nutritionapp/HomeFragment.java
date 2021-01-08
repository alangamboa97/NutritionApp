package com.example.nutritionapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    //upload - variables
    EditText editPDFName;
    Button  btn_upload;

    //Download - variables
    ListView PDFListView;
    List<uploadPDF> uploadPDFS;

    StorageReference storageReference;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Variables de upload
        editPDFName = view.findViewById(R.id.textPDFName);
        btn_upload = view.findViewById(R.id.btn_upload_pdf);

        //Variables dowload
        PDFListView = view.findViewById(R.id.listViewPDF);
        uploadPDFS = new ArrayList<>();//declaramos arraylist<>();


        //leer achivos PDF de storage y database
        ViewAllFiles();

        PDFListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                uploadPDF uploadPDF = uploadPDFS.get(position);
                Intent intent = new Intent();
                //intent.setType(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(uploadPDF.getUrl()),intent.ACTION_VIEW);
                //intent.setData(Uri.parse(uploadPDF.getUrl()));
                startActivity(intent);
            }
        });

        //subir achivos pdf
        Subir_PDF();

        return view;
    }


    /*
    * FUNCION PARA READ PDF Y DESCARGA
    * */

    private void ViewAllFiles(){
        databaseReference =FirebaseDatabase.getInstance().getReference("upload_pdf");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot postSnapshot: snapshot.getChildren()){
                    uploadPDF uploadPDF = postSnapshot.getValue(com.example.nutritionapp.uploadPDF.class);
                    uploadPDFS.add(uploadPDF);
                }
                String[] uploads = new String[uploadPDFS.size()];
                for(int i=0; i<uploads.length; i++){
                    uploads[i] = uploadPDFS.get(i).getName();

                }
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,uploads);
                PDFListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }



    /*
    *
    * FUNCIONES PARA EL UPLOAD DE ARCHIVOS PDF A LA BASE DE DATOS Y STORAGES
    * */
    private void Subir_PDF(){
        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("upload_pdf");

        //avento para subir archivo pdf
        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si el input es vacio marca error
                if(editPDFName.getText().toString().isEmpty()){
                    editPDFName.setError("Escriba un título para el PDF");
                    editPDFName.setFocusable(true);
                }
                else{
                    selectPDFFile();
                }
            }
        });
    }
    private void selectPDFFile() {
        //hacemos la peticion de seleccionar un archivo pdf
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"SELECCIONA ARCHIVO PDF"),12);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //si hay algun error en la peticion de seleccionar un PDF no hace el uploadFile
        if (requestCode == 12 && resultCode == RESULT_OK
                && data !=null && data.getData()!=null){
            uploadPDFFILE(data.getData());
        }
    }

    private void uploadPDFFILE(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setTitle("Subiendo...");
        progressDialog.show();

        //guardamos el pdf en el storage
        StorageReference reference= storageReference.child("upload_pdf/"+System.currentTimeMillis()+".pdf");
        reference.putFile(data)
                //funcion OnSucess
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //obtenemos la url del archivo
                        Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                        while(!uri.isComplete());
                        Uri url = uri.getResult();//obtenemos el url del archivo en el storage
                        //instancia de la Clase uploadPDF
                        uploadPDF uploadPDF = new uploadPDF(editPDFName.getText().toString(),url.toString());
                        //guardamos en la base de datos el nombre y la url del archivo dentro del storage
                        databaseReference.child(databaseReference.push().getKey()).setValue(uploadPDF);
                        //mostramos mensaje de éxito
                        Toast.makeText(getActivity(),"FILE UPLOADED", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }) //FUNCION ON PROGESS
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            //mostramos la carga del archivo
                            double progress =(100.0*snapshot.getBytesTransferred())/snapshot.getTotalByteCount();
                            progressDialog.setMessage("UPLOADED: "+(int)progress+"%");
                    }
                });
    }
}