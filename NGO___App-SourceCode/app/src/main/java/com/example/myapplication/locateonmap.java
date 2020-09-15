package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class locateonmap extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Button go,confirm;
    EditText search;
    LatLng selected = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locateonmap);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

//        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        getDeviceLocation();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions options = new MarkerOptions()
                                            .position(latLng)
                                            .title("Selected Location");
                mMap.addMarker(options);
                selected = latLng;
                confirm.setVisibility(View.VISIBLE);
            }
        });
    }

    public void init(){
        search = findViewById(R.id.search);
        go = findViewById(R.id.go);
        confirm = findViewById(R.id.confirm);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locateaddress(search.getText().toString());
            }
        });
        confirm.setVisibility(View.GONE);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectlocation();
            }
        });
    }

    public void selectlocation(){

        if(selected == null){
            Toast.makeText(locateonmap.this,"Please select a location",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(locateonmap.this,signup.class);
        intent.putExtra("latitude",Double.toString(selected.latitude));
        intent.putExtra("longitude",Double.toString(selected.longitude));
        setResult(1,intent);
        finish();
    }
    public void locateaddress(String address)  {
        if(address.isEmpty()){
            Toast.makeText(locateonmap.this,"Search Field is Empty",Toast.LENGTH_SHORT).show();
            return;
        }
        Geocoder geocoder = new Geocoder(locateonmap.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(address,1);
        }catch(IOException e){
            getDeviceLocation();
        }
        LatLng latLng = null;
        for(int i=0;i<list.size();i++){
            Address a = list.get(i);
            if(a.hasLatitude() && a.hasLongitude()){
                latLng = new LatLng(a.getLatitude(),a.getLongitude());
                break;
            }
        }
        
        if(latLng == null){
            Toast.makeText(locateonmap.this,"Can't Locate given location",Toast.LENGTH_SHORT).show();
        }
        else{
            String string = search.getText().toString();
            //mMap.addMarker(new MarkerOptions().position(latLng).title(string));
            moveCamera(latLng,30.0f);
        }

    }

    public void getDeviceLocation(){
        FusedLocationProviderClient P = LocationServices.getFusedLocationProviderClient(this);
        Task location = P.getLastLocation();
        location.addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if(task.isSuccessful()){
                    Location address = (Location) task.getResult();
                    LatLng latLng = new LatLng(address.getLatitude(),address.getLongitude());
                    moveCamera(latLng,14.0f);
                }

                else{
                    Toast.makeText(locateonmap.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void moveCamera(LatLng latLng,float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
    }
}
