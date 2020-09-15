package com.example.demo1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class gallery extends AppCompatActivity {
    FirebaseApp ngoApp;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Database.getInstance().initialiseNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        setContentView(R.layout.imagegridview);
        String NGO;
        TextView tvText = findViewById(R.id.tvText);
        GridView gridView = findViewById(R.id.grid);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        final Intent intent = getIntent();
        String mNgoEmail = intent.getStringExtra("Email");

        assert mNgoEmail != null;
        NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");

        final ArrayList<String> array = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String url = Database.getInstance().ngoDetailsList.get(mNgoEmail).geturl();
        if(url.equals("Dummy") || url.equals("")) url = "https://firebasestorage.googleapis.com/v0/b/demo1-8b782.appspot.com/o/NgoDatabase%2Ficon%2Fgallery-187-902099.png?alt=media&token=b6ae18d6-fad0-48f7-aa8a-15517014cde2";
                array.add(url);
        //array.add
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("Gallery");
        dR.child(NGO).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //array.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String temp = ds.getValue(String.class);
                    //Toast.makeText(gallery.this,"temp : "+temp,Toast.LENGTH_SHORT).show();
                    Log.d("temp: ",temp);
                    array.add(temp);

                }
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(gallery.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
        gridView.setAdapter(new galleryAdapt(gallery.this, array));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent2 = new Intent(gallery.this,ImagePreview.class);
                intent2.putExtra("Url",array.get(position));
                startActivity(intent2);
            }
        });
    }
}
