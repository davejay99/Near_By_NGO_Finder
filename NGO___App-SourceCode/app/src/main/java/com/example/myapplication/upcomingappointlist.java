//package com.example.myapplication;
//
//import android.app.AlertDialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.os.Build;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.ContextMenu;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.ActionBar;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.Toolbar;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.demo1.DataClass.AppointmentDetails;
////import com.example.demo1.DataClass.DonationDetails;
////import com.example.demo1.DataClass.EventDetails;
////import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
//import com.example.myapplication.DataClass.AppointmentDetails;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//
//public class upcomingappointlist extends AppCompatActivity {
//
//    TextView tvMessage;
//    RecyclerView recyclerView;
//    FirebaseApp userApp,ngoApp;
//    Boolean sortUpcoming = false,sortPast=false;
//    Toolbar toolbar;
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.donationlist);
//        toolbar =  findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//        //toolbar.setTitle("Donation History");
//        tvMessage = findViewById(R.id.textView);
//        recyclerView = findViewById(R.id.recycle);
//        Database.getInstance().initialiseUserApp(getApplicationContext());
//        userApp=FirebaseApp.getInstance("userApp");
//        Database.getInstance().initializeNgoApp(getApplicationContext());
//        ngoApp = FirebaseApp.getInstance("ngoApp");
//        getAppointList();
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void getAppointList(){
//        if(isInternet() == false){
//            tvMessage.setText("You Are Offline");
//            tvMessage.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//            return;
//        }
//
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Updating...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{
//            progressDialog.show();
//        }catch (Exception e){e.getMessage();}
//
//
//        final ArrayList<AppointmentDetails> allEvents = new ArrayList<>();
//
//        //String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
//        String USER = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(USER);
//        //Date cD = java.time.LocalDate.now();
//        databaseReference.addValueEventListener(new ValueEventListener() {
//
//
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                allEvents.clear();
//
//                progressDialog.dismiss();
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    AppointmentDetails temp =  ds.getValue(AppointmentDetails.class);
////                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
////                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
////                    Date dateobj = new Date();
//////                    String dd = dateobj.toString();
////
////                    String currentD = df.format(dateobj);
////                    String  currentT = df1.format(dateobj);
////                    String dd = "",MM="",yy="",HH="",mm="",aDate = temp.getDate(),aTime = temp.getTime();
////                    String dd1="",MM1="",yy1="",HH1="",mm1="";
////                    //double ddD,MMD,yyD,HHD,mmD,dd1D,MM1D,yy1D,HH1D,mm1D,
////                    double ddD = Double.parseDouble(dd.concat(currentD.substring(0, 1)));
////                    double   MMD = Double.parseDouble(MM.concat(currentD.substring(3,4)));
////                    double yyD = Double.parseDouble(yy.concat(currentD.substring(6,9)));
////                    double HHD = Double.parseDouble(HH.concat(currentT.substring(0,1)));
////                    double mmD = Double.parseDouble(mm.concat(currentT.substring(3,4)));
////                    double  dd1D = Double.parseDouble(dd1.concat(aDate.substring(0,1)));
////                    double MM1D = Double.parseDouble(MM1.concat(aDate.substring(3,4)));
////                    double yy1D = Double.parseDouble(yy1.concat(aDate.substring(6,9)));
////                    double HH1D = Double.parseDouble(HH1.concat(aTime.substring(0,1)));
////                    double mm1D = Double.parseDouble(mm1.concat(aTime.substring(3,4)));
////
////                    {
////                        if(yy1D > yyD){
////                            allEvents.add(temp);
////                        }
////                        else if(yy1D==yyD){
////                            if(MM1D > MMD){
////                                allEvents.add(temp);
////                            }
////                            else if(MM1D==MMD){
////                                if(dd1D > ddD){
////                                    allEvents.add(temp);
////                                }
////                                else if(dd1D==ddD){
////                                    if(HH1D>HHD){
////                                        allEvents.add(temp);
////                                    }
////                                    else if(HH1D==HHD){
////                                        if(mm1D > mmD){
////                                            allEvents.add(temp);
////                                        }
////
////                                    }
////                                }
////                            }
////                        }
////                    }
////                    if(sortPast == true){
////                        if(yy1D < yyD){
////                            allEvents.add(temp);
////                        }
////                        else if(yy1D==yyD){
////                            if(MM1D < MMD){
////                                allEvents.add(temp);
////                            }
////                            else if(MM1D==MMD){
////                                if(dd1D < ddD){
////                                    allEvents.add(temp);
////                                }
////                                else if(dd1D==ddD){
////                                    if(HH1D<HHD){
////                                        allEvents.add(temp);
////                                    }
////                                    else if(HH1D==HHD){
////                                        if(mm1D < mmD){
////                                            allEvents.add(temp);
////                                        }
////
////                                        else if(mm1D == mmD){
////                                            allEvents.add(temp);
////                                        }
////
////                                    }
////                                }
////                            }
////                        }
////                    }
//
//
//
//                    Date d2 = new Date(); // Current date
//                    //	Date d2 = Calendar.getInstance().getTime();
//                    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                    String strDate = dateFormat.format(d2);
//
//                    SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                    long millis1=0;
//                    try{
//                        Date date1 = sdf1.parse(strDate);
//                        // Date date = new Date(2014,10,29,6,6);
//                        millis1 = date1.getTime();
//                    }catch(ParseException e){
//                        e.printStackTrace();
//                    }
//
//                    //String myDate = "2014-10-29 09:09";
//                    String myDate = temp.getDate();
//                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                    long millis=0;
//                    try{
//                        Date date = sdf.parse(myDate);
//                        // Date date = new Date(2014,10,29,6,6);
//                        millis = date.getTime();
//                    }catch(ParseException e){
//                        e.printStackTrace();
//                    }
//
//
//                    //long ms = d2.getTime();
////                    System.out.println(millis1);
////                    System.out.println(millis);
//                    if(millis1<millis) allEvents.add(temp);
//                    //allEvents.add(temp);
//                }
//
//                //Log.d("list",allEvents.toString());
//
//                setRecyclerView(allEvents);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                progressDialog.dismiss();
//                Toast.makeText(upcomingappointlist.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    public void setRecyclerView(ArrayList<AppointmentDetails> recyclerList){
//        if(recyclerList.size()==0){
//            tvMessage.setText("Nothing to show");
//            tvMessage.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//            return;
//        }
//
//        else{
//            tvMessage.setVisibility(View.GONE);
//            recyclerView.setVisibility(View.VISIBLE);
//            //if(sortUpcoming==true || sortPast == true) getSortedList(recyclerList);
//        }
//
//        recyclerView.setNestedScrollingEnabled(false);
//        recyclerView.setLayoutManager(new LinearLayoutManager(upcomingappointlist.this));
//        recyclerView.setAdapter(new upcomingappointlist.DataAdapter(upcomingappointlist.this,recyclerList));
//    }
//
////    private void getSortedList(ArrayList<AppointmentDetails> recyclerList) {
////        if(sortUpcoming){
////            //Collections.sort(recyclerList, new Comparator<AppointmentDetails>() {
////                //@Override
////               // public int compare(AppointmentDetails o1, AppointmentDetails o2) {
////                    SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
////                    try {
////                        java.util.Date d1 = sdformat.parse(o1.getDate());
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////                    try {
////                        java.util.Date d2 = sdformat.parse(o2.getDate());
////                    } catch (ParseException e) {
////                        e.printStackTrace();
////                    }
////                    if(d1.compare)
//////                    if(Double.parseDouble(o1.()) > Double.parseDouble(o2.getAmount())) return 1;
//////                    else return -1;
////                }
////            //});
////        }
//
////        if(sortPast){
////            Collections.sort(recyclerList, new Comparator<AppointmentDetails>() {
////                @Override
////                public int compare(AppointmentDetails o1, AppointmentDetails o2) {
////                    if(Date.parse(o1.getDateTime()) > Date.parse(o1.getDateTime())) return 1;
////                    else return -1;
////                }
////            });
////        }
////
////    }
//
//    public class DataAdapter extends RecyclerView.Adapter<upcomingappointlist.DataAdapter.ViewHolder>{
//        ArrayList<AppointmentDetails> eventDetailsList;
//        Context context;
//        public DataAdapter(Context context, ArrayList<AppointmentDetails> eventDetailsList) {
//            this.eventDetailsList=eventDetailsList;
//            this.context=context;
//        }
//
//        @NonNull
//        @Override
//        public upcomingappointlist.DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointlistrecycle,parent,false);
//            return new upcomingappointlist.DataAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull upcomingappointlist.DataAdapter.ViewHolder holder, int position) {
//            holder.setIsRecyclable(false);
//            AppointmentDetails event = eventDetailsList.get(position);
//            holder.tvName.setText("NGO : "+event.getUserName());
//            holder.tvEmail.setText("NGO Email : "+event.getUserEmail());
//            holder.tvAmount.setText("Date : "+event.getDate());
//            //holder.tvDate.setText("Date/Time: "+event.getTime());
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return eventDetailsList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
//            TextView tvName,tvEmail,tvAmount,tvDate;
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                tvEmail = itemView.findViewById(R.id.tvEmail);
//                tvName =  itemView.findViewById(R.id.tvName);
//                tvAmount = (TextView) itemView.findViewById(R.id.date);
//                tvDate = itemView.findViewById(R.id.time);
//
//            }
//
//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                MenuItem postpone,cancel;
//                postpone = menu.add(0,1,1,"Postpone Appointment");
//                cancel = menu.add(0,2,2,"Cancel Appointment");
//                postpone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        postponeA(eventDetailsList.get(getLayoutPosition()));
//                        return true;
//                    }
//                });
//
//                cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        cancelA(eventDetailsList.get(getLayoutPosition()),false);
//                        return true;
//                    }
//                });
//
//            }
//        }
//    }
//
//    private void postponeA(final AppointmentDetails appointmentDetails) {
//        LayoutInflater layoutInflater = getLayoutInflater();
//        View alertLayout = layoutInflater.inflate(R.layout.upcominglistdialog,null);
//        final EditText D = findViewById(R.id.etDate);
//        final EditText T = findViewById(R.id.etTime);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle("Set Date and Time");
//        builder.setView(alertLayout);
//        builder.setPositiveButton(" Postpone ", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//
//                if(D.getText().toString().isEmpty() || T.getText().toString().isEmpty()){
//                    Toast.makeText(upcomingappointlist.this,"All Fields Are Mandatory",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
//                Date dateobj = new Date();
////                    String dd = dateobj.toString();
//
//                String currentD = df.format(dateobj);
//                String  currentT = df1.format(dateobj);
//                String dd = "",MM="",yy="",HH="",mm="",aDate = appointmentDetails.getDate(),aTime = appointmentDetails.getTime();
//                String dd1="",MM1="",yy1="",HH1="",mm1="";
//                double ddD = Double.parseDouble(dd.concat(currentD.substring(0, 1)));
//                double   MMD = Double.parseDouble(MM.concat(currentD.substring(3,4)));
//                double yyD = Double.parseDouble(yy.concat(currentD.substring(6,9)));
//                double HHD = Double.parseDouble(HH.concat(currentT.substring(0,1)));
//                double mmD = Double.parseDouble(mm.concat(currentT.substring(3,4)));
//                double  dd1D = Double.parseDouble(dd1.concat(aDate.substring(0,1)));
//                double MM1D = Double.parseDouble(MM1.concat(aDate.substring(3,4)));
//                double yy1D = Double.parseDouble(yy1.concat(aDate.substring(6,9)));
//                double HH1D = Double.parseDouble(HH1.concat(aTime.substring(0,1)));
//                double mm1D = Double.parseDouble(mm1.concat(aTime.substring(3,4)));
//                Boolean done=false;
//                if(yy1D < yyD){
////                    allEvents.add(temp);
//                    done=true;
//                }
//                else if(yy1D==yyD){
//                    if(MM1D < MMD){
//                        // allEvents.add(temp);
//                        done=true;
//                    }
//                    else if(MM1D==MMD){
//                        if(dd1D < ddD){
//                            //allEvents.add(temp);
//                            done=true;
//                        }
//                        else if(dd1D==ddD){
//                            if(HH1D<HHD){
//                                //allEvents.add(temp);
//                                done=true;
//                            }
//                            else if(HH1D==HHD){
//                                if(mm1D < mmD){
//                                    //allEvents.add(temp);
//                                    done=true;
//                                }
//
//                                else if(mm1D == mmD){
//                                    //allEvents.add(temp);
//                                    done=true;
//                                }
//
//                            }
//                        }
//                    }
//                }
//
//                if(done==true){
//                    Toast.makeText(upcomingappointlist.this,"Date and Time should greater than current date and time",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                cancelA(appointmentDetails,true);
//                String ngoEmail = appointmentDetails.getNgoEmail();
//                String ngoName = appointmentDetails.getNgoName();
//                String userEmail = appointmentDetails.getUserEmail();
//                String userName = appointmentDetails.getUserName();
//                final String USER = userEmail.replaceAll("[^A-Za-z0-9]","-");
//                final String NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
//                DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
//                //final String key = dR.push().getKey();
//                final AppointmentDetails val = new AppointmentDetails(ngoEmail,ngoName,userEmail,userName,D.getText().toString(),T.getText().toString());
//                dR.child(USER).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if(task.isSuccessful()){
//                            DatabaseReference dR1 = FirebaseDatabase.getInstance(userApp).getReference("Appoint").child(USER);
//                            //String key1 = dR1.push().getKey();
//                            dR1.child(NGO).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @RequiresApi(api = Build.VERSION_CODES.O)
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task1) {
//                                    if(task1.isSuccessful()){
//                                        Toast.makeText(upcomingappointlist.this,"Appointed Successfully",Toast.LENGTH_SHORT).show();
//                                        getAppointList();
//                                    }
//
//                                    else{
//                                        Toast.makeText(upcomingappointlist.this,"Failed: "+task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//
//                        else{
//                            Toast.makeText(upcomingappointlist.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//            }
//        });
//
//        builder.create().show();
//    }
//
//    private void cancelA(AppointmentDetails appointmentDetails, final Boolean done) {
//        final String NGO = appointmentDetails.getNgoEmail().replaceAll("[^A-Za-z0-9]","-");
//        final String USER = appointmentDetails.getUserEmail().replaceAll("[^A-Za-z0-9]","-");
//        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
//        dR.child(USER).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    DatabaseReference dR1 = FirebaseDatabase.getInstance(userApp).getReference("Appoint").child(USER);
//                    dR1.child(NGO).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task1) {
//                            if(task1.isSuccessful()){
//                                if(done==false) {
//                                    Toast.makeText(upcomingappointlist.this, "Cancelled Successfully", Toast.LENGTH_SHORT).show();
//                                    getAppointList();
//                                }
//                            }
//                            else{
//
//                                Toast.makeText(upcomingappointlist.this,"Failed: "+task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(upcomingappointlist.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.appointlistmenu, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        if(item.getItemId() == R.id.update){
//            getAppointList();
//            Toast.makeText(upcomingappointlist.this,"Page Updated",Toast.LENGTH_SHORT).show();
//        }
//
////        if(item.getItemId() == R.id.upcoming){
////            sortUpcoming = true;
////            getAppointList();
////        }
////
////        if(item.getItemId() == R.id.past){
////            sortPast = true;
////            getAppointList();
////        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    public boolean isInternet() {
//        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
//                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
//            return true;
//        return false;
//    }
//
//
//}

package com.example.myapplication;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.myapplication.DataClass.AppointmentDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class upcomingappointlist extends AppCompatActivity {

    TextView tvMessage;
    RecyclerView recyclerView;
    FirebaseApp userApp,ngoApp;
    Boolean sortUpcoming = false,sortPast=false;
    Toolbar toolbar;
    EditText Dd;
    EditText month;
    EditText year;
    EditText hour;
    EditText minute;
    EditText purpose;
    ProgressBar progressBar;

    Button button;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appointlist);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("Donation History");
        tvMessage = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycle);
        Database.getInstance().initialiseUserApp(getApplicationContext());
        userApp=FirebaseApp.getInstance("userApp");
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        getAppointList();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getAppointList(){
        if(isInternet() == false){
//            tvMessage.setText("You Are Offline");
//            tvMessage.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
            Toast.makeText(this,"You are Offline",Toast.LENGTH_SHORT).show();
            return;
        }

//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Updating...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{
//            progressDialog.show();
//        }catch (Exception e){e.getMessage();}

    progressBar.setVisibility(View.VISIBLE);
        final ArrayList<AppointmentDetails> allEvents = new ArrayList<>();

        //String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
        String NGO = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
        //Date cD = java.time.LocalDate.now();
        databaseReference.addValueEventListener(new ValueEventListener() {


            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();
                progressBar.setVisibility(View.INVISIBLE);
                //progressDialog.dismiss();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    AppointmentDetails temp =  ds.getValue(AppointmentDetails.class);
//                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//                    SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
//                    Date dateobj = new Date();
//                    String dd = dateobj.toString();

//                    String currentD = df.format(dateobj);
//                    String  currentT = df1.format(dateobj);
//                    String dd = "",MM="",yy="",HH="",mm="",aDate = temp.getDate(),aTime = temp.getTime();
//                    String dd1="",MM1="",yy1="",HH1="",mm1="";
//                    //double ddD,MMD,yyD,HHD,mmD,dd1D,MM1D,yy1D,HH1D,mm1D,
//                    double ddD = Double.parseDouble(dd.concat(currentD.substring(0, 1)));
//                    double   MMD = Double.parseDouble(MM.concat(currentD.substring(3,4)));
//                    double yyD = Double.parseDouble(yy.concat(currentD.substring(6,9)));
//                    double HHD = Double.parseDouble(HH.concat(currentT.substring(0,1)));
//                    double mmD = Double.parseDouble(mm.concat(currentT.substring(3,4)));
//                    double  dd1D = Double.parseDouble(dd1.concat(aDate.substring(0,1)));
//                    double MM1D = Double.parseDouble(MM1.concat(aDate.substring(3,4)));
//                    double yy1D = Double.parseDouble(yy1.concat(aDate.substring(6,9)));
//                    double HH1D = Double.parseDouble(HH1.concat(aTime.substring(0,1)));
//                    double mm1D = Double.parseDouble(mm1.concat(aTime.substring(3,4)));

//                    {
//                        if(yy1D > yyD){
//                            allEvents.add(temp);
//                        }
//                        else if(yy1D==yyD){
//                            if(MM1D > MMD){
//                                allEvents.add(temp);
//                            }
//                            else if(MM1D==MMD){
//                                if(dd1D > ddD){
//                                    allEvents.add(temp);
//                                }
//                                else if(dd1D==ddD){
//                                    if(HH1D>HHD){
//                                        allEvents.add(temp);
//                                    }
//                                    else if(HH1D==HHD){
//                                        if(mm1D > mmD){
//                                            allEvents.add(temp);
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//                    }
//                    if(sortPast == true){
//                        if(yy1D < yyD){
//                            allEvents.add(temp);
//                        }
//                        else if(yy1D==yyD){
//                            if(MM1D < MMD){
//                                allEvents.add(temp);
//                            }
//                            else if(MM1D==MMD){
//                                if(dd1D < ddD){
//                                    allEvents.add(temp);
//                                }
//                                else if(dd1D==ddD){
//                                    if(HH1D<HHD){
//                                        allEvents.add(temp);
//                                    }
//                                    else if(HH1D==HHD){
//                                        if(mm1D < mmD){
//                                            allEvents.add(temp);
//                                        }
//
//                                        else if(mm1D == mmD){
//                                            allEvents.add(temp);
//                                        }
//
//                                    }
//                                }
//                            }
//                        }
//                    }

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

                    //String myDate = "2014-10-29 09:09";
                    String myDate = temp.getDate();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
                    long millis=0;
                    try{
                        Date date = sdf.parse(myDate);
                        // Date date = new Date(2014,10,29,6,6);
                        millis = date.getTime();
                    }catch(ParseException e){
                        e.printStackTrace();
                    }


                    //long ms = d2.getTime();
//                    System.out.println(millis1);
//                    System.out.println(millis);
                    if(millis1<millis) allEvents.add(temp);
                    //else System.out.println("1");
                    //allEvents.add(temp);
                }

                //Log.d("list",allEvents.toString());

                setRecyclerView(allEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(upcomingappointlist.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setRecyclerView(ArrayList<AppointmentDetails> recyclerList){
        if(recyclerList.size()==0){
            tvMessage.setText("Nothing to show");
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        else{
            tvMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            //if(sortUpcoming==true || sortPast == true) getSortedList(recyclerList);
        }

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(upcomingappointlist.this));
        recyclerView.setAdapter(new upcomingappointlist.DataAdapter(upcomingappointlist.this,recyclerList));
    }

//    private void getSortedList(ArrayList<AppointmentDetails> recyclerList) {
//        if(sortUpcoming){
//            //Collections.sort(recyclerList, new Comparator<AppointmentDetails>() {
//                //@Override
//               // public int compare(AppointmentDetails o1, AppointmentDetails o2) {
//                    SimpleDateFormat sdformat = new SimpleDateFormat("dd-MM-yyyy");
//                    try {
//                        java.util.Date d1 = sdformat.parse(o1.getDate());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    try {
//                        java.util.Date d2 = sdformat.parse(o2.getDate());
//                    } catch (ParseException e) {
//                        e.printStackTrace();
//                    }
//                    if(d1.compare)
////                    if(Double.parseDouble(o1.()) > Double.parseDouble(o2.getAmount())) return 1;
////                    else return -1;
//                }
//            //});
//        }

//        if(sortPast){
//            Collections.sort(recyclerList, new Comparator<AppointmentDetails>() {
//                @Override
//                public int compare(AppointmentDetails o1, AppointmentDetails o2) {
//                    if(Date.parse(o1.getDateTime()) > Date.parse(o1.getDateTime())) return 1;
//                    else return -1;
//                }
//            });
//        }
//
//    }

    public class DataAdapter extends RecyclerView.Adapter<upcomingappointlist.DataAdapter.ViewHolder>{
        ArrayList<AppointmentDetails> eventDetailsList;
        Context context;
        public DataAdapter(Context context, ArrayList<AppointmentDetails> eventDetailsList) {
            this.eventDetailsList=eventDetailsList;
            this.context=context;
        }

        @NonNull
        @Override
        public upcomingappointlist.DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointlistrecycle,parent,false);
            return new upcomingappointlist.DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull upcomingappointlist.DataAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            AppointmentDetails event = eventDetailsList.get(position);
            holder.tvName.setText("NGO : "+event.getNgoName());
            holder.tvEmail.setText("NGO Email : "+event.getNgoEmail());
            holder.tvAmount.setText("Date : "+event.getDate());
            holder.tvDate.setText("Purpose: "+event.getPurpose());

        }

        @Override
        public int getItemCount() {
            return eventDetailsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvName,tvEmail,tvAmount,tvDate;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvName =  itemView.findViewById(R.id.tvName);
                tvAmount = (TextView) itemView.findViewById(R.id.date1);
                 tvDate = itemView.findViewById(R.id.time);

                //itemView.setOnCreateContextMenuListener(this);

            }

//            @Override
//            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                MenuItem postpone,cancel;
//
//                postpone = menu.add(0,1,1,"Postpone Appointment");
//                cancel = menu.add(0,2,2,"Cancel Appointment");
//                postpone.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        postponeA(eventDetailsList.get(getLayoutPosition()));
//                        return true;
//                    }
//                });
//
//                cancel.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        cancelA(eventDetailsList.get(getLayoutPosition()),false);
//                        return true;
//                    }
//                });
//
//            }
        }
    }

//    private void postponeA(final AppointmentDetails appointmentDetails) {
//        LayoutInflater layoutInflater = getLayoutInflater();
//        View alertLayout = layoutInflater.inflate(R.layout.upcominglistdialog,null);
//
//
//        month = alertLayout.findViewById(R.id.monthcu);
//        year = alertLayout.findViewById(R.id.yearcu);
//        //button = alertLayout.findViewById(R.id.appoint);
//        hour = alertLayout.findViewById(R.id.hourcu);
//        minute = alertLayout.findViewById(R.id.minutecu);
//        Dd = alertLayout.findViewById(R.id.date1);
//        purpose = alertLayout.findViewById(R.id.purpose);
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setCancelable(true);
//        builder.setTitle("Set Meeting Details");
//        builder.setView(alertLayout);
//        builder.setPositiveButton(" Postpone ", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which)
//            {
//
//                if(Dd.getText().toString().isEmpty() || month.getText().toString().isEmpty() || year.getText().toString().isEmpty()
//                        || hour.getText().toString().isEmpty() || minute.getText().toString().isEmpty() || purpose.getText().toString().isEmpty()){
//                    Toast.makeText(upcomingappointlist.this,"All Fields Are Mandatory",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
//                SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
//                Date dateobj = new Date();
////                    String dd = dateobj.toString();
//
//                String currentD = df.format(dateobj);
//                String  currentT = df1.format(dateobj);
//                String dd = "",MM="",yy="",HH="",mm="",aDate = appointmentDetails.getDate(),aTime = appointmentDetails.getTime();
//                String dd1="",MM1="",yy1="",HH1="",mm1="";
//                double ddD = Double.parseDouble(dd.concat(currentD.substring(0, 1)));
//                double   MMD = Double.parseDouble(MM.concat(currentD.substring(3,4)));
//                double yyD = Double.parseDouble(yy.concat(currentD.substring(6,9)));
//                double HHD = Double.parseDouble(HH.concat(currentT.substring(0,1)));
//                double mmD = Double.parseDouble(mm.concat(currentT.substring(3,4)));
//                double  dd1D = Double.parseDouble(dd1.concat(aDate.substring(0,1)));
//                double MM1D = Double.parseDouble(MM1.concat(aDate.substring(3,4)));
//                double yy1D = Double.parseDouble(yy1.concat(aDate.substring(6,9)));
//                double HH1D = Double.parseDouble(HH1.concat(aTime.substring(0,1)));
//                double mm1D = Double.parseDouble(mm1.concat(aTime.substring(3,4)));
//                Boolean done=false;
//                if(yy1D < yyD){
////                    allEvents.add(temp);
//                    done=true;
//                }
//                else if(yy1D==yyD){
//                    if(MM1D < MMD){
//                       // allEvents.add(temp);
//                        done=true;
//                    }
//                    else if(MM1D==MMD){
//                        if(dd1D < ddD){
//                            //allEvents.add(temp);
//                            done=true;
//                        }
//                        else if(dd1D==ddD){
//                            if(HH1D<HHD){
//                                //allEvents.add(temp);
//                                done=true;
//                            }
//                            else if(HH1D==HHD){
//                                if(mm1D < mmD){
//                                    //allEvents.add(temp);
//                                    done=true;
//                                }
//
//                                else if(mm1D == mmD){
//                                    //allEvents.add(temp);
//                                    done=true;
//                                }
//
//                            }
//                        }
//                    }
//                }

//                if(done==true){
//                    Toast.makeText(upcomingappointlist.this,"Date and Time should greater than current date and time",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                String dd = Dd.getText().toString(),MM=month.getText().toString(),yy=year.getText().toString(),hh=hour.getText().toString()
//                        ,mm=minute.getText().toString();
//                String date = dd+"-"+MM+"-"+yy+" "+hh+":"+mm;
//
//                Date d2 = new Date(); // Current date
//                //	Date d2 = Calendar.getInstance().getTime();
//                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                String strDate = dateFormat.format(d2);
//
//                SimpleDateFormat sdf1 = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                long millis1=0;
//                try{
//                    Date date1 = sdf1.parse(strDate);
//                    // Date date = new Date(2014,10,29,6,6);
//                    millis1 = date1.getTime();
//                }catch(ParseException e){
//                    e.printStackTrace();
//                }
//
//                String myDate = date;
//                //String myDate = temp.getDate();
//                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm");
//                long millis=0;
//                try{
//                    Date date2 = sdf.parse(myDate);
//                    // Date date = new Date(2014,10,29,6,6);
//                    millis = date2.getTime();
//                }catch(ParseException e){
//                    e.printStackTrace();
//                }
//
//
//                //long ms = d2.getTime();
////                    System.out.println(millis1);
////                    System.out.println(millis);
//                if(millis1>=millis) {
//                    Toast.makeText(upcomingappointlist.this,"Appointment time should greater than Current time",Toast.LENGTH_SHORT).show();
//                    return;
//                }
//
//                cancelA(appointmentDetails,true);
//                final String key = appointmentDetails.getMeetingcode();
//                String purposes =  purpose.getText().toString();
//                String ngoEmail = appointmentDetails.getNgoEmail();
//                String ngoName = appointmentDetails.getNgoName();
//                String userEmail = appointmentDetails.getUserEmail();
//                String userName = appointmentDetails.getUserName();
//                final String USER = userEmail.replaceAll("[^A-Za-z0-9]","-");
//                final String NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
//                DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
//                //final String key = dR.push().getKey();
//                final AppointmentDetails val = new AppointmentDetails(ngoEmail,ngoName,userEmail,userName,date,key,purposes);
//                dR.child(key).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        if(task.isSuccessful()){
//                            DatabaseReference dR1 = FirebaseDatabase.getInstance(userApp).getReference("Appoint").child(USER);
//                            //String key1 = dR1.push().getKey();
//                            dR1.child(key).setValue(val).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                @RequiresApi(api = Build.VERSION_CODES.O)
//                                @Override
//                                public void onComplete(@NonNull Task<Void> task1) {
//                                    if(task1.isSuccessful()){
//                                        Toast.makeText(upcomingappointlist.this,"Appointed Successfully",Toast.LENGTH_SHORT).show();
//                                        getAppointList();
//                                    }
//
//                                    else{
//                                        Toast.makeText(upcomingappointlist.this,"Failed: "+task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });
//                        }
//
//                        else{
//                            Toast.makeText(upcomingappointlist.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
           // }
//        });
//
//        builder.create().show();
//    }

//    private void cancelA(AppointmentDetails appointmentDetails, final Boolean done) {
//        final String key = appointmentDetails.getMeetingcode();
//        final String NGO = appointmentDetails.getNgoEmail().replaceAll("[^A-Za-z0-9]","-");
//        final String USER = appointmentDetails.getUserEmail().replaceAll("[^A-Za-z0-9]","-");
//        DatabaseReference dR = FirebaseDatabase.getInstance(userApp).getReference("Appoint").child(USER);
//        dR.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @RequiresApi(api = Build.VERSION_CODES.O)
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    DatabaseReference dR1 = FirebaseDatabase.getInstance(ngoApp).getReference("Appoint").child(NGO);
//                    dR1.child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task1) {
//                            if(task1.isSuccessful()){
//                                if(done==false) {
//                                    Toast.makeText(upcomingappointlist.this, "Cancelled Successfully", Toast.LENGTH_SHORT).show();
//                                    getAppointList();
//                                }
//                            }
//                            else{
//
//                                Toast.makeText(upcomingappointlist.this,"Failed: "+task1.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                }
//                else{
//                    Toast.makeText(upcomingappointlist.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appointlistmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.update){
            getAppointList();
            Toast.makeText(upcomingappointlist.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }

//        if(item.getItemId() == R.id.upcoming){
//            sortUpcoming = true;
//            getAppointList();
//        }
//
//        if(item.getItemId() == R.id.past){
//            sortPast = true;
//            getAppointList();
//        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }


}

