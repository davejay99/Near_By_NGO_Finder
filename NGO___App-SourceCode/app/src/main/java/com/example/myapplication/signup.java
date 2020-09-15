package com.example.myapplication;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.myapplication.DataClass.NgoDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class signup extends AppCompatActivity {
    EditText name,address,phone,email,password,confirm;
    String Name,Ad,Phone,EM,Password,Confirm;
    Button button;
    ImageButton map;
    String latitude,longitude;
    Boolean isLocate = false;
    FirebaseApp ngoApp;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signupact);
        if(!havepermission()){
            requestpermission();
        }
        name = findViewById(R.id.ngoname);
        address = findViewById(R.id.address);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm = findViewById(R.id.confirm);
        button = findViewById(R.id.button);
        progressBar = findViewById(R.id.progressBar);
        map = findViewById(R.id.map);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        progressBar.setVisibility(View.INVISIBLE);
        init();
    }
    
    public void init(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignUp();
            }
        });
        
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location();
            }
        });
    }

    private void location() {

        Intent intent = new Intent(signup.this,locateonmap.class);
        startActivityForResult(intent,1);
    }

    private void requestpermission() {
        List<String> permissionL = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionL.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionL.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(permissionL.size()>0) {
            String[] permissionA = new String[permissionL.size()];
            for(int i=0;i<permissionL.size();i++)
            permissionA[i] = permissionL.get(i);
            ActivityCompat.requestPermissions(signup.this, permissionA, 101);
        }
    }

    private boolean havepermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            return false;
        }
        return true;
    }

    public void getDetails(){
        Name = name.getText().toString();
        Ad = address.getText().toString();
        Phone = phone.getText().toString();
        EM = email.getText().toString().replaceAll("\\s+$","");
        Password = password.getText().toString();
        Confirm = confirm.getText().toString();
    }
    private void SignUp() {
        getDetails();
        if(!isInternet()){
            Toast.makeText(signup.this,"You are Offline",Toast.LENGTH_SHORT).show();
            return;
        }

        if(isLocate == false){
            Toast.makeText(signup.this,"Provide Location On Map",Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.getText().toString().isEmpty() || address.getText().toString().isEmpty() || phone.getText().toString().isEmpty()
            || EM.isEmpty() || password.getText().toString().isEmpty() || confirm.getText().toString().isEmpty()){
            Toast.makeText(signup.this,"All Fields Are Important",Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.getText().toString().compareTo(confirm.getText().toString()) != 0){
            Toast.makeText(signup.this,"Both Password Must be same",Toast.LENGTH_SHORT).show();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        FirebaseAuth.getInstance(ngoApp).createUserWithEmailAndPassword(EM,Password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                upload();
                            }
                            else{
                                Toast.makeText(signup.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                });

    }

    private boolean isInternet() {
        ConnectivityManager cM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
            cM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        return false;
    }

    public void upload(){
        String url = "Dummy";
        NgoDetails ngo = new NgoDetails(Name,Ad,Phone,EM,latitude,longitude,url);
        String NGO = EM.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("NgoDetails").child(NGO);
        dR.setValue(ngo).addOnCompleteListener(new OnCompleteListener<Void>() {

            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    finish();
                }
                else{
                    Toast.makeText(signup.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode==1){
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                isLocate = true;
            }
        }
    }



}
