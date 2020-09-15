package com.example.demo1;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo1.DataClass.NgoDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;



public class nearbyngo extends AppCompatActivity {
    TextView tvMessage;
    RecyclerView recyclerview;

    //static LatLng currentL = new LatLng(23.089590,72.559570);

      LatLng currentL =null;

    LocationListener locationListener;
    boolean sortByName=false;
    boolean sortByD=false;
    long distR=-1;
    FirebaseApp ngoApp;
    Toolbar toolbar;
    ProgressBar progressBar;
    //private List<ImageUpload> mUploads;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearby);

        ngoApp = FirebaseApp.getInstance("ngoApp");
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setTitle("NGOs Near You");
        //getSupportActionBar().setTitle("");
        //uploadDetails();
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        init();
    }
    private void uploadDetails() {
        String name = "Hi-Feed";
        String address="Himachal Pradesh";
        String phone_number="9998972743";
        String email="hifeed@gmail.com";
        String latitude="31.1048";
        String longitude="77.1734";
        String url = "Dummy";
        NgoDetails ngo = new NgoDetails(name,address,phone_number,email,latitude,longitude,url);
        String key = ngo.getEmail().replaceAll("[^A-Za-z0-9]", "-");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("NgoDatabase/NGOdetails");
        databaseReference.child(key).setValue(ngo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //progressDialog.dismiss();
                if(task.isSuccessful()) {
                    //finish();
                }
                //else
                    //Toast.makeText(SignUp.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private  void init(){
        tvMessage = findViewById(R.id.textView3);
        recyclerview = findViewById(R.id.recycle);
        Database.getInstance().requestPermission(this);
        FloatingActionButton map = findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nearbyngo.this,MapsActivity.class);
                startActivity(intent);
            }
        });

        updatePage();
    }

    private void updatePage(){
        if(isInternet() == false){
            Toast.makeText(nearbyngo.this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentL!=null) getAllNgoList();
        else getcurrentL();
    }

    private void getcurrentL(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
         && ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
           // Toast.makeText(this,"if",Toast.LENGTH_SHORT).show();
            getAllNgoList();
            return;
        }

        final LocationManager lM = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location lastL = lM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        //Log.d("lastL : ",lastL.toString());
        //Toast.makeText(this,"lastL",Toast.LENGTH_SHORT).show();
        if(lastL != null)  currentL = new LatLng(lastL.getLatitude(),lastL.getLongitude());

//        Context context = null;
//        String location_context = context.LOCATION_SERVICE;
//        assert context != null;
//        final LocationManager locationManager = (LocationManager) context.getSystemService(location_context);
//
//        List<String> providers = locationManager.getProviders(true);
//
//        for (String provider : providers) {
//            try {
//                Location location = locationManager.getLastKnownLocation(provider);
//                if (location != null) {
////                    lastL.longitude = location.getLongitude();
////                    lastL.latitude = location.getLatitude();
//                     currentL = new LatLng(location.getLatitude(), location.getLongitude());
//                }
//                locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
//            } catch (SecurityException e) {
//                e.printStackTrace();
//            }
//        }
        getAllNgoList();

        locationListener = new LocationListener(){
            @Override
          public void onLocationChanged(Location location){
              currentL = new LatLng(location.getLatitude(),location.getLongitude());
              lM.removeUpdates(locationListener);
              getAllNgoList();
          }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        lM.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
    }

    public  void getAllNgoList(){
        if(isInternet() == false){
            Toast.makeText(nearbyngo.this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        final Database database = Database.getInstance();

//        final ProgressDialog pd = new ProgressDialog(this);
//        pd.setMessage("Fetching Data");
//        pd.setCanceledOnTouchOutside(false);
//        try{pd.show();}
//        catch(Exception e){return ;}
        progressBar.setVisibility(View.VISIBLE);

        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("NgoDetails");
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    NgoDetails temp = ds.getValue(NgoDetails.class);
//                    String Email=temp.getEmail();
//                    DatabaseReference sR= FirebaseDatabase.getInstance().getReference("Ngodetails/uploads");
//                    Upload getimg = sR.;
                      Database.getInstance().addNgo(temp);
//                    ImageUpload upload = new ImageUpload(temp.getName(),temp.getLatitude(),temp.getLongitude(),imgurl);
//                    mUpload.add(upload);
                    Log.d("list45",Database.getInstance().ngoList.toString());
                }
                //pd.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                setRecycleView();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //pd.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(nearbyngo.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRecycleView(){
        ArrayList<NgoDetails> recyclerList = getFilteredList();
        recyclerList = getSortedList(recyclerList);

        if(recyclerList.size() == 0){
            tvMessage.setText("It seems there are no NGOs around you");
            tvMessage.setVisibility(View.VISIBLE);
            recyclerview.setVisibility(View.GONE);
        }
        else{
            tvMessage.setVisibility(View.GONE);
            recyclerview.setVisibility(View.VISIBLE);
        }
        //recyclerview.setLayoutManager(new LinearLayout(nearbyngo.this));
        recyclerview.setLayoutManager(new LinearLayoutManager(nearbyngo.this));
        recyclerview.setAdapter(new nearbyngo.DataAdapter(nearbyngo.this,recyclerList));
    }

    private ArrayList<NgoDetails> getFilteredList(){
        ArrayList<NgoDetails> allNgos = Database.getInstance().getNgoList();
        ArrayList<NgoDetails> recyclerList = new ArrayList<>();

        if(currentL!=null && distR!=-1){
            for(int i=0;i<allNgos.size();i++){
                NgoDetails temp =allNgos.get(i);
                LatLng latlng = new LatLng(Double.parseDouble(temp.getLatitude()),Double.parseDouble(temp.getLongitude()));
                double distance = (double) Math.round(getDistance(currentL,latlng));
                double diskm = distance/1000d;

                if(diskm <= distR) recyclerList.add(temp);
            }
        }
        else recyclerList = allNgos;
        return recyclerList;
    }

    public ArrayList<NgoDetails> getSortedList(ArrayList<NgoDetails> recyclerList){
        if(sortByD && currentL!=null){
            Collections.sort(recyclerList, new Comparator<NgoDetails>() {


                @Override
                public int compare(NgoDetails o1, NgoDetails o2) {
                    double dist1 = getDistance(currentL,new LatLng(Double.parseDouble(o1.getLatitude()),Double.parseDouble(o1.getLongitude())));
                    double dist2 = getDistance(currentL,new LatLng(Double.parseDouble(o2.getLatitude()),Double.parseDouble(o2.getLongitude())));
                    if(dist1 <= dist2) return -1;
                    else return 1;
                }


            });
        }

        else if(sortByName){
            Collections.sort(recyclerList, new Comparator<NgoDetails>() {
                @Override
                public int compare(NgoDetails o1, NgoDetails o2) {
                    String name1 = o1.getName().toLowerCase();
                    String name2 = o2.getName().toLowerCase();
                    if(name1.compareTo(name2) < 0) return -1;
                    else return 1;
                }
            });
        }
        return recyclerList;
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
        Context context;
        ArrayList<NgoDetails> ngoList;

        public DataAdapter(Context context,ArrayList<NgoDetails> ngoList){
            this.ngoList=ngoList;
            this.context=context;
        }
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ngo_recycle,parent,false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);

            NgoDetails ngo = ngoList.get(position);
            Log.d("URL: ", ngo.geturl());
            String url = ngo.geturl();
            if(url.equals("Dummy") || url.equals("")){
                url="https://firebasestorage.googleapis.com/v0/b/demo1-8b782.appspot.com/o/NgoDatabase%2Ficon%2Fgallery-187-902099.png?alt=media&token=b6ae18d6-fad0-48f7-aa8a-15517014cde2";
            }
            Picasso.with(context)
                    .load(url)
                    .placeholder(R.drawable.image)
                    .fit()
                    .centerCrop()
                    .into(holder.imageView);
            //StorageReference storageReference = FirebaseStorage.getInstance().getReference("NgoDatabase/uploads/sattvik-gmail-com.jpg");
//            GlideApp.with(context)
//                    .load(storageReference)
//                    .into(holder.imageView);
            holder.tvName.setText("NGO : "+ngo.getName());
            if(currentL != null){
                LatLng latLng = new LatLng(Double.parseDouble(ngo.getLatitude()) , Double.parseDouble(ngo.getLongitude()));
                double dist=Math.round(getDistance(currentL,latLng));
                double distkm = dist/1000d;

                holder.tvDistance.setText("Distance : "+Double.toString(distkm)+" km");
                holder.tvDistance.setVisibility(View.VISIBLE);
            }
            else holder.tvDistance.setVisibility(View.GONE);
        }

        @Override
        public int getItemCount() {

            return ngoList.size();
        }

        public class ViewHolder extends  RecyclerView.ViewHolder implements  View.OnClickListener{
            TextView tvName,tvDistance;
            ImageView imageView;
            public ViewHolder(View item){
                super(item);
                imageView = item.findViewById(R.id.image_view_upload);
                 tvName = item.findViewById(R.id.ngoname);
                tvDistance = itemView.findViewById(R.id.ngodist);
                item.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(nearbyngo.this,myngo.class);
                intent.putExtra("ngoEmail",ngoList.get(getLayoutPosition()).getEmail());
                //Toast.makeText(nearbyngo.this,"Clicked",Toast.LENGTH_LONG).show();
                startActivity(intent);
                return;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ngomenu,menu);
        return  super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==R.id.update){
            distR=-1;
            updatePage();
            Toast.makeText(nearbyngo.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId()==R.id.filter){
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.ngodialog,null);
            final EditText etRange = alertLayout.findViewById(R.id.etRange);
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Range(km) : ");
            alert.setView(alertLayout);
            alert.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String temp = etRange.getText().toString();
                    if(temp.isEmpty()) Toast.makeText(nearbyngo.this,"Range Field Cant'be Empty",Toast.LENGTH_SHORT).show();
                    else{
                        distR = Long.parseLong(temp);
                        setRecycleView();
                    }
                }
            });
            AlertDialog dialog = alert.create();
            dialog.show();
        }

        else{
            sortByName=false;
            sortByD=false;
            if(item.getItemId() == R.id.sortByName) sortByName =  true;
            else sortByD = true;
            setRecycleView();
        }
        return super.onOptionsItemSelected(item);
    }

    public double getDistance(LatLng d1,LatLng d2){
        Location start = new Location("start");
        start.setLatitude(d1.latitude);
        start.setLongitude(d1.longitude);

        Location end = new Location("end");
        end.setLatitude(d2.latitude);
        end.setLongitude(d2.longitude);

        return start.distanceTo(end);
    }

    public boolean isInternet(){
        ConnectivityManager cM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
        || cM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState()==NetworkInfo.State.CONNECTED)
            return  true;
        return false;
    }
}
