package com.example.demo1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.demo1.DataClass.DonationDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class donatescreen extends AppCompatActivity {
    EditText Amount;
    Button donate;
    FirebaseApp userApp,ngoApp;
    String mNgoEmail,mUserEmail,mNgoName,mUserName;
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donatescreen);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("Donate");
        donate = findViewById(R.id.donate);
        Amount = findViewById(R.id.Amount);
        mNgoEmail = getIntent().getStringExtra("ngoEmail");
        mNgoName = getIntent().getStringExtra("ngoName");
        mUserEmail = getIntent().getStringExtra("userEmail");
        mUserName = getIntent().getStringExtra("userName");
        //mUserName = "Dummy";
        Database.getInstance().initialiseUserApp(getApplicationContext());
        Database.getInstance().initialiseNgoApp(getApplicationContext());
        userApp = FirebaseApp.getInstance("userApp");
        ngoApp = FirebaseApp.getInstance("ngoApp");
        //mUserEmail = FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail();
        init();
    }

    public void init(){
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Amount.getText().toString().isEmpty()){
                    Toast.makeText(donatescreen.this,"Please Enter Amount First",Toast.LENGTH_SHORT).show();
                    return;
                }

                String amount = Amount.getText().toString();
                for(int ij=0;ij<amount.length();ij++){
                    if(amount.charAt(ij) != '1' && amount.charAt(ij) != '2'&&amount.charAt(ij) != '3'&&amount.charAt(ij) != '4'&&amount.charAt(ij) != '5'&&amount.charAt(ij) != '6'
                    &&amount.charAt(ij) != '7'&&amount.charAt(ij) != '8'&&amount.charAt(ij) != '9'&&amount.charAt(ij) != '0'){
                        Toast.makeText(donatescreen.this,"Only Digits are allowed",Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                if(!isInternet()){
                    Toast.makeText(donatescreen.this,"You Are Offline",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(Database.getInstance().havePermissions(donatescreen.this)==false) {
                    Database.getInstance().requestPermission(donatescreen.this);
                    return;
                }

                final int[] cnt = {0};
                final String USER = mUserEmail.replaceAll("[^A-Za-z0-9]","-");
                final String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
                final DatabaseReference dR = FirebaseDatabase.getInstance(userApp).getReference("DonationHistory").child(USER);
                String key1 = dR.push().getKey();
                Date date = new Date();
                final DonationDetails donation = new DonationDetails(mNgoEmail,mNgoName,mUserEmail,mUserName,Amount.getText().toString(),date.toString());
                dR.child(key1).setValue(donation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cnt[0]++;
                        if(task.isSuccessful()){
                            if(cnt[0] == 2) {
                                Toast.makeText(donatescreen.this, "Successfully Donated", Toast.LENGTH_SHORT).show();
                            }

                        }

                        else{
                            Toast.makeText(donatescreen.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                DatabaseReference dR1 = FirebaseDatabase.getInstance(ngoApp).getReference("DonationHistory").child(NGO);
                String key2 = dR1.push().getKey();
                dR1.child(key2).setValue(donation).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cnt[0]++;
                        if(task.isSuccessful()){
                            if(cnt[0] == 2) {
                                createReceipt(donation);
                                Toast.makeText(donatescreen.this, "Successfully Donated", Toast.LENGTH_SHORT).show();
                            }
                        }

                        else{
                            createReceipt(donation);
                            Toast.makeText(donatescreen.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void createReceipt(DonationDetails donation) {
        PdfDocument document = new PdfDocument();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(900,900,1).create();
        String text = "We "+ donation.getNgoName() + " have received donation of Rupees "+
                donation.getAmount() + " at "+donation.getDateTime()+" from "+donation.getUserName();

        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint  paint = new Paint();
        canvas.drawText(text,80,50,paint);

        document.finishPage(page);

        String dirpath = Environment.getExternalStorageDirectory().getPath()+"/UserApp/";
        File file = new File(dirpath);
        if(!file.exists()){
            file.mkdirs();
        }

        String pdfpath = dirpath + "Receipts.pdf";
        File file1 = new File(pdfpath);
        int i=0;
        while(file1.exists()){
            i++;
           pdfpath = dirpath+"Receipts"+"("+i+")"+".pdf";
           file1 = new File(pdfpath);
        }

        try {
            document.writeTo(new FileOutputStream(file1));
        }catch (Exception e){
            Toast.makeText(this,"Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
        }

        document.close();
    }

    private boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }

}
