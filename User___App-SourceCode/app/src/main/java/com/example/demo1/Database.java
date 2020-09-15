package com.example.demo1;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;


import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.demo1.DataClass.NgoDetails;
import com.example.demo1.DataClass.UserDetails;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Database {

    public Database() {}

    public static Database instance;

    public static Database getInstance() {
        if(instance==null)
            instance = new Database();
        return instance;
    }

//--------------------------------------------------------------------------------------------------//

    public UserDetails user;

    public UserDetails getUser() {
        return user;
    }

    public void setUser(UserDetails user) {
        this.user = user;
    }

//--------------------------------------------------------------------------------------------------//

    public ArrayList<NgoDetails> ngoList = new ArrayList<>();
    public HashMap<String,NgoDetails> ngoDetailsList = new HashMap<>();

    public ArrayList<NgoDetails> getNgoList() {
        return ngoList;
    }

    public void addNgo(NgoDetails ngo) {
        if(ngoDetailsList.containsKey(ngo.getEmail())) {
            ngoList.remove(ngoDetailsList.get(ngo.getEmail()));
            ngoDetailsList.remove(ngo.getEmail());
            ngoList.add(ngo);
            ngoDetailsList.put(ngo.getEmail(), ngo);
        } else {
            ngoList.add(ngo);
            ngoDetailsList.put(ngo.getEmail(), ngo);
        }
    }

    public NgoDetails getNgoDetails(String ngoEmail) {
        if(ngoDetailsList.containsKey(ngoEmail)) {
            Log.d("Trace : ","Yes");
            return ngoDetailsList.get(ngoEmail);
        }
        else {
            Log.d("Trace : ","No");
            return null;
        }
    }

//--------------------------------------------------------------------------------------------------//

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

    public void initialiseNgoApp(Context context) {

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyD01iF2cZ_6pHvEFYqCORrewTh6Apxi02s")
                .setApplicationId("com.example.myapplication")
                .setDatabaseUrl("https://ngoapp-36b13.firebaseio.com/")
                .build();

        boolean hasBeenInitialized=false;
        List<FirebaseApp> firebaseApps = FirebaseApp.getApps(context);
        for(FirebaseApp app : firebaseApps){
            if(app.getName().equals("ngoApp")){
                hasBeenInitialized=true;
            }
        }

        if(hasBeenInitialized==false)
            FirebaseApp.initializeApp(context, options, "ngoApp");
    }

    public boolean havePermissions(Activity caller) {

        int permissionCheck = ContextCompat.checkSelfPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck== PackageManager.PERMISSION_GRANTED)
        {
            permissionCheck = ContextCompat.checkSelfPermission(caller, Manifest.permission.ACCESS_COARSE_LOCATION);

            if (permissionCheck== PackageManager.PERMISSION_GRANTED)
            {
                permissionCheck = ContextCompat.checkSelfPermission(caller,Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if(permissionCheck==PackageManager.PERMISSION_GRANTED)
                    return true;
            }
        }

        return false;
    }

    public void requestPermission(Activity caller) {

        List<String> permissionList = new ArrayList<>();

        if  (ContextCompat.checkSelfPermission(caller, Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if  (ContextCompat.checkSelfPermission(caller, Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        if  (ContextCompat.checkSelfPermission(caller, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED)
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionList.size()>0)
        {
            String [] permissionArray = new String[permissionList.size()];

            for (int i=0;i<permissionList.size();i++)
                permissionArray[i] = permissionList.get(i);

            ActivityCompat.requestPermissions(caller, permissionArray,99);
        }
    }
}