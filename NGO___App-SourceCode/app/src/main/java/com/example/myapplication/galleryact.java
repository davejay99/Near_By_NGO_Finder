package com.example.myapplication;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class galleryact extends AppCompatActivity {
    private int Pimage = 1;
    private Button mButtonChooseImage;
    private Button mButtonUpload;
    private Button mButtonNear;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    private Uri mImageUri;
    private FirebaseStorage mStorage;
    StorageReference mStorageReference,mStorageRef;
    private DatabaseReference mDatabaseRef;
    private StorageTask mUploadTask;
    private String ngoEmail;
    FirebaseApp ngoApp,userApp;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_choose);
        mButtonChooseImage = findViewById(R.id.button_choose_image);
        mButtonUpload = findViewById(R.id.button_upload);
//        mButtonNear = findViewById(R.id.nearby);
        //mEditTextFileName = findViewById(R.id.edit_text_file_name);
        mImageView = findViewById(R.id.image_view);
        mProgressBar = findViewById(R.id.progress_bar);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        Database.getInstance().initialiseUserApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        userApp = FirebaseApp.getInstance("userApp");
        ngoEmail = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        //Toast.makeText(galleryact.this,"ngo: "+ngoEmail,Toast.LENGTH_LONG).show();
        mStorageRef = FirebaseStorage.getInstance().getReference("Gallery").child(ngoEmail);
        // mStorageRef=FirebaseStorage.getInstance(ngoApp).getReference("NgoDetails").child(ngoEmail);
//        mStorage = FirebaseStorage.getInstance(ngoApp);
//        mStorageReference = mStorage.getReference();//.child(ngoEmail);
           mDatabaseRef= FirebaseDatabase.getInstance(ngoApp).getReference("Gallery").child(ngoEmail);


//        mButtonNear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(imageact.this,nearbyngo.class);
//                startActivity(intent);
//            }
//        });
        mButtonChooseImage.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        mButtonUpload.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(mUploadTask!=null && mUploadTask.isInProgress()){
                    Toast.makeText(galleryact.this,"Upload in Progress",Toast.LENGTH_SHORT).show();
                }

                else uploadFile();
            }
        });
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,Pimage);
    }
    @Override
    protected void onActivityResult(int reqcode,int resultcode,Intent data) {
        super.onActivityResult(reqcode, resultcode, data);
        if (reqcode == Pimage && resultcode == RESULT_OK && data != null
                && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile(){
        if(mImageUri != null){
            final StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()+"."+getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0);
                                }
                            }, 1000);



                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String key = mDatabaseRef.push().getKey();
                                    mDatabaseRef.child(key).setValue(uri.toString());
                                    Toast.makeText(galleryact.this,"Upload Successful",Toast.LENGTH_LONG).show();
                                    finish();
                                    //Do what you want with the url
                                }
                            });
//                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
//                                    taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());
//                            mDatabaseRef.child(ngoEmail).child("url").setValue(upload.getmImageUrl());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(galleryact.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * (taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount()));
                            mProgressBar.setProgress((int) progress);
                        }
                    });
        }

        else Toast.makeText(galleryact.this,"No File Selected",Toast.LENGTH_SHORT).show();
    }
}
