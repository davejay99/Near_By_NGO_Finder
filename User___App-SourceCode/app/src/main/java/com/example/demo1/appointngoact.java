package com.example.demo1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo1.DataClass.AppointmentDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class appointngoact extends AppCompatActivity {
    EditText date,month,year,hour,minute,purpose;
    Button button;
    String ngoEmail,userEmail,ngoName,userName;
    FirebaseApp userApp,ngoApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointngo);
        date = findViewById(R.id.date1);
        month = findViewById(R.id.monthcu);
        year = findViewById(R.id.yearcu);
        button = findViewById(R.id.appoint);
        hour = findViewById(R.id.hourcu);
        minute = findViewById(R.id.minutecu);
        Database.getInstance().initialiseUserApp(getApplicationContext());
        userApp = FirebaseApp.getInstance("userApp");
        Database.getInstance().initialiseNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        ngoEmail = getIntent().getStringExtra("ngoEmail");
        userEmail = getIntent().getStringExtra("userEmail");
        ngoName = getIntent().getStringExtra("ngoName");
        userName = getIntent().getStringExtra("userName");
        purpose = findViewById(R.id.purpose);
        init();
    }

    private void init() {


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appoint();
            }
        });
    }

    private void appoint() {
        if(isInternet() == false){
            Toast.makeText(appointngoact.this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        if(date.getText().toString().isEmpty() || month.getText().toString().isEmpty() || year.getText().toString().isEmpty()
        || hour.getText().toString().isEmpty() || minute.getText().toString().isEmpty() || purpose.getText().toString().isEmpty()){
            Toast.makeText(appointngoact.this,"All Fields Are Mandatory",Toast.LENGTH_SHORT).show();
            return;
        }
        String dd = date.getText().toString(),MM=month.getText().toString(),yy=year.getText().toString(),hh=hour.getText().toString()
                ,mm=minute.getText().toString();
        String date = dd+"-"+MM+"-"+yy+" "+hh+":"+mm;
        Date d2 = new Date(); // Current date
        //	Date d2 = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        String strDate = dateFormat.format(d2);

        SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        long millis1=0;
        try{
            Date date1 = sdf1.parse(strDate);
            // Date date = new Date(2014,10,29,6,6);
            millis1 = date1.getTime();
        }catch(ParseException e){
            e.printStackTrace();
        }

        String myDate = date;
        //String myDate = temp.getDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
        long millis=0;
        try{
            Date date2 = sdf.parse(myDate);
            // Date date = new Date(2014,10,29,6,6);
            millis = date2.getTime();
        }catch(ParseException e){
            e.printStackTrace();
        }


        //long ms = d2.getTime();
//                    System.out.println(millis1);
//                    System.out.println(millis);
        if(millis1>=millis) {
            Toast.makeText(appointngoact.this,"Appointment time should greater than Current time",Toast.LENGTH_SHORT).show();
            return;
        }
        final String USER = userEmail.replaceAll("[^A-Za-z0-9]","-");
        final String NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(userApp).getReference("Appoint").child(USER);
        final String key = dR.push().getKey();
        final AppointmentDetails val = new AppointmentDetails(ngoEmail,ngoName,userEmail,userName,date,key,purpose.getText().toString());
        dR.child(key).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if(task.isSuccessful()){
                    DatabaseReference dR1 = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
                    //String key1 = dR1.push().getKey();
                    dR1.child(key).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task1) {
                            if(task1.isSuccessful()){
                                Toast.makeText(appointngoact.this,"Appointed Successfully",Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            else{
                                Toast.makeText(appointngoact.this,"Failed: "+task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(appointngoact.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }
}
