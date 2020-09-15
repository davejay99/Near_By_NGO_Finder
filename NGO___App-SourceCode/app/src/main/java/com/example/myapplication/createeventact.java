package com.example.myapplication;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myapplication.DataClass.EventDetails;
import com.example.myapplication.DataClass.NgoDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class createeventact extends AppCompatActivity {
    EditText name,category,description,startdate,starttime,enddate,endtime,location;
    Button button;
    FirebaseApp ngoApp;
    String ngoEmail,NGO;
    ProgressBar progressbar;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createevent);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        ngoEmail = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail();
        init();
    }

    public void init(){
        name = findViewById(R.id.name);
        category = findViewById(R.id.category);
        description = findViewById(R.id.description);
        location = findViewById(R.id.location);
        startdate = findViewById(R.id.startdate);
        starttime = findViewById(R.id.starttime);
        enddate = findViewById(R.id.enddate);
        endtime = findViewById(R.id.endtime);
        button = findViewById(R.id.button);
        progressbar = findViewById(R.id.progressBar);
        progressbar.setVisibility(View.INVISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                upload();
            }
        });
    }

    public void upload(){
        if(!isInternet()){
            Toast.makeText(createeventact.this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }

        if(name.getText().toString().isEmpty() || category.getText().toString().isEmpty() || description.getText().toString().isEmpty() || location.getText().toString().isEmpty()
            || startdate.getText().toString().isEmpty() || starttime.getText().toString().isEmpty() || enddate.getText().toString().isEmpty() || endtime.getText().toString().isEmpty()){
            Toast.makeText(createeventact.this,"All fields are mandatory",Toast.LENGTH_SHORT).show();
            return;
        }

        if(Database.getInstance().getNgoName() == null){
            getNgoDetails();
            return;
        }
        //Log.d("NGO_NAME: ",NGO_NAME);
        EventDetails event = new EventDetails(name.getText().toString(),category.getText().toString(),Database.getInstance().getNgoName().getName(),description.getText().toString(),location.getText().toString(),startdate.getText().toString(),starttime.getText().toString(),enddate.getText().toString(),endtime.getText().toString());
        String NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");

        progressbar.setVisibility(View.VISIBLE);
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("EventDetails").child(NGO);
        String key = name.getText().toString();
        dR.child(key).setValue(event).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(createeventact.this,"Event Successfully Created",Toast.LENGTH_SHORT).show();
                    finish();
                }

                else{
                    progressbar.setVisibility(View.INVISIBLE);
                    Toast.makeText(createeventact.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getNgoDetails(){
        NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("NgoDetails");
        dR.child(NGO).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                NgoDetails temp = dataSnapshot.getValue(NgoDetails.class);
                Log.d("name: ",temp.toString());
                Database.getInstance().setNgoName(temp);
                //Database.getInstance().setNgoName(temp.getName().toString());
                Toast.makeText(createeventact.this,"Try Again",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
}
