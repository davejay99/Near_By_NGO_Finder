package com.example.myapplication;

import android.content.Context;

import com.example.myapplication.DataClass.NgoDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class Database {

    public Database() {
    }

    public static Database temp=null;
    NgoDetails mNGO=null;

    public static Database getInstance(){
        if(temp==null) {
            temp = new Database();
        }
        return temp;
    }
    public void initialiseUserApp(Context context) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyC0HvvskFEsMuYg2Cq9UEBw32VQ_FlHmPw")
                .setApplicationId("com.example.demo1")
                .setDatabaseUrl("https://demo1-8b782.firebaseio.com/")
                .build();

        boolean hasBeenInitialized=false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(context);
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("userApp")){
                hasBeenInitialized=true;
            }
        }

        if(hasBeenInitialized==false)
            FirebaseApp.initializeApp(context, options, "userApp");
    }
    public void initializeNgoApp(Context context) {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyD01iF2cZ_6pHvEFYqCORrewTh6Apxi02s")
                .setApplicationId("com.example.myapplication")
                .setDatabaseUrl("https://ngoapp-36b13.firebaseio.com/")
                .build();
        Boolean initialized = false;
        List<FirebaseApp> firebaseApps= FirebaseApp.getApps(context);
        for(FirebaseApp temp : firebaseApps){
            if(temp.getName() == "ngoApp") {
                initialized = true;
                break;
            }
        }
        if(initialized == false) FirebaseApp.initializeApp(context, options, "ngoApp");
    }

    public void setNgoName(NgoDetails ngo){
        this.mNGO = ngo;
    }

    public NgoDetails getNgoName(){
        return mNGO;
    }
}
