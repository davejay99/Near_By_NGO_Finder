package com.example.demo1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo1.DataClass.EventDetails;
import com.example.demo1.DataClass.NgoDetails;
import com.example.demo1.DataClass.SubscriptionDetails;
import com.example.demo1.DataClass.UserDetails;
import com.example.demo1.Database;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;

public class myngo extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseApp ngoApp,userApp;
    TextView tvMessage;
    RecyclerView recyclerView;
    TextView tvName,tvAddress,tvEmail,tvPhoneNumber;
    ProgressBar progressBar;
    static String name,address,phone_number,email,latitude,longitude,category,location,startDate,startTime,endDate,endTime,ngo,description,NGO;
    private static String mNgoEmail=null;
    private  NgoDetails mNgo=null;
    private static  UserDetails mUser=null;
    DatabaseReference myRef;
    Database database = Database.getInstance();
    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngo);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mNgoEmail=getIntent().getStringExtra("ngoEmail");
        //getSupportActionBar().setTitle("NGO Details");
        FloatingActionButton gallery = findViewById(R.id.gallery);
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(myngo.this,gallery.class);
                intent.putExtra("Email",mNgoEmail);
                startActivity(intent);
            }
        });


        tvMessage = findViewById(R.id.tvMessage);
        recyclerView = findViewById(R.id.recyclerView);



        //assert mNgoEmail != null;
       // Log.d("NGO23 : ",mNgoEmail.toString());
        NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
        database.initialiseNgoApp(getApplicationContext());
        database.initialiseUserApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        userApp = FirebaseApp.getInstance("userApp");
        //uploadDetails();
        //mNgoEmail = "sattvik@gmail.com";
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        updatePage();

    }

    private void uploadDetails() {
        name="Sristi";
        address="Ahmedabad";
        phone_number="9662207604";
        email="sristi@gmail.com";
        latitude="23.022505";
        longitude="72.571365";

//        name="Food Festival";
//        category="Halth";
//        ngo="Sattvik";
//        description="Enjoy Traditional Food";
//        location="Ahmedabad";
//        startDate="21/12/2020";
//        startTime="9:00";
//        endDate="25/12/2020";
//        endTime="17:00";
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("NgoDetails");

        //EventDetails event = new EventDetails(name,category,ngo,description,location,startDate,startTime,endDate,endTime);
String url = "https://firebasestorage.googleapis.com/v0/b/demo1-8b782.appspot.com/o/NgoDatabase%2Fuploads%2Fsristi-gmail-com.jpg?alt=media&token=bf1d5497-a202-411b-9d24-9c8770b71a7b";
        NgoDetails ngo = new NgoDetails(name,address,phone_number,email,latitude,longitude,url);
        String key = "sristi-gmail-com";

        //DatabaseReference databaseReference = FirebaseDatabase.getInstance(userApp).getReference(getString(R.string.NgoDetails));
        databaseReference.child(key).setValue(ngo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    //finish();
                }
                else
                    Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePage() {
        if(isInternet()==false){
            Toast.makeText(this,"You Are Offline",Toast.LENGTH_LONG).show();
            return;
        }
        getDetails(mNgoEmail);
    }

    private void getDetails(String ngoEmail){
        //Toast.makeText(myngo.this,"gett",Toast.LENGTH_LONG).show();
        Database database = Database.getInstance();
        mNgo = Database.getInstance().getNgoDetails(ngoEmail);
        //Log.d("mNgo",mNgo.toString());
        mUser = database.getUser();

        if(mNgo==null){
            //Toast.makeText(myngo.this,"null",Toast.LENGTH_LONG).show();
            getNgoDetails(ngoEmail);
            return;
        }

        if(mUser==null){
            getUserDetails(FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail());
            return;
        }
        init();
    }

    private void getUserDetails(String userEmail){
        //final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Fetching Data");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{progressDialog.show();}
//        catch(Exception e) {return;}
        progressBar.setVisibility(View.VISIBLE);

        userEmail = userEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(userApp).getReference(getString(R.string.UserDetails)).child(userEmail);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //progressDialog.dismiss();;
                progressBar.setVisibility(View.INVISIBLE);
                mUser = dataSnapshot.getValue(UserDetails.class);
                Database.getInstance().setUser(mUser);
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(myngo.this,"Falied : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getNgoDetails(String ngoEmail){
        //final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Fetching Data.....");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{progressDialog.show();}
//        catch(Exception e) {return;}
        progressBar.setVisibility(View.VISIBLE);

        ngoEmail = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("NgoDetails").child(ngoEmail);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //progressDialog.dismiss();;
                progressBar.setVisibility(View.INVISIBLE);
                mNgo = dataSnapshot.getValue(NgoDetails.class);
                Database.getInstance().addNgo(mNgo);
                //Toast.makeText(myngo.this,"Hello",Toast.LENGTH_LONG).show();
                init();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(myngo.this,"Falied : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private void init(){
        //Toast.makeText(myngo.this,"init",Toast.LENGTH_LONG).show();
        tvName = findViewById(R.id.tvName);
        tvName.setText("Name : "+mNgo.getName());

        tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setText("Address : "+mNgo.getAddress());

        tvEmail = (TextView) findViewById(R.id.tvEmail);
        tvEmail.setText("Email : "+mNgo.getEmail());

        tvPhoneNumber = (TextView) findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText("Phone Number : "+mNgo.getPhone_number());

        updateAllEvents();
    }

    public void updateAllEvents(){
        if(isInternet() == false){
            tvMessage.setText("You Are Offline");
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Updating...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{
//            progressDialog.show();
//        }catch (Exception e){e.getMessage();}
        progressBar.setVisibility(View.VISIBLE);


        final ArrayList<EventDetails> allEvents = new ArrayList<>();

        //String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("EventDetails").child(NGO);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();

                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    EventDetails temp =  ds.getValue(EventDetails.class);
                    allEvents.add(temp);
                }

                Log.d("list",allEvents.toString());

                setRecyclerView(allEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(myngo.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setRecyclerView(ArrayList<EventDetails> recyclerList){
        if(recyclerList.size()==0){
            tvMessage.setText("No Events");
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        else{
            tvMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(myngo.this));
        recyclerView.setAdapter(new DataAdapter(myngo.this,recyclerList));
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
        ArrayList<EventDetails> eventDetailsList;
        Context context;
        public DataAdapter(Context context, ArrayList<EventDetails> eventDetailsList) {
            this.eventDetailsList=eventDetailsList;
            this.context=context;
        }

        @NonNull
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_recycler_view,parent,false);
            return new DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            EventDetails event = eventDetailsList.get(position);
            holder.tvCategory.setText("Category : "+event.getCategory());
            holder.tvName.setText("Event Name : "+event.getName());
            holder.tvOrganiseBy.setVisibility(View.GONE);
            holder.tvDescription.setText("Description : "+event.getDescription());
            holder.tvLocation.setText("Location : "+event.getLocation());
            holder.tvStartDate.setText("Start Date : "+event.getStart_date());
            holder.tvStartTime.setText("Start Time : "+event.getStart_time());
            holder.tvEndDate.setText("End Date : "+event.getEnd_date());
            holder.tvEndTime.setText("End Time : "+event.getEnd_time());
        }

        @Override
        public int getItemCount() {
            return eventDetailsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements  View.OnCreateContextMenuListener{
            TextView tvCategory,tvName,tvOrganiseBy,tvDescription,tvLocation,tvStartDate
                    ,tvStartTime,tvEndDate,tvEndTime;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvName =  itemView.findViewById(R.id.tvName);
                tvOrganiseBy = (TextView) itemView.findViewById(R.id.tvOrganiseBy);
                tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
                tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
                tvStartDate = (TextView) itemView.findViewById(R.id.tvStartDate);
                tvStartTime = (TextView) itemView.findViewById(R.id.tvStartTime);
                tvEndDate = (TextView) itemView.findViewById(R.id.tvEndDate);
                tvEndTime = (TextView) itemView.findViewById(R.id.tvEndTime);

                itemView.setOnCreateContextMenuListener(this);
            }

            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                MenuItem share = menu.add(0,1,1,"Share Event");
                share .setOnMenuItemClickListener(onMenuClicked);
            }

            public MenuItem.OnMenuItemClickListener onMenuClicked = new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getItemId()==1) shareEvent(eventDetailsList.get(getLayoutPosition()));
                    return false;
                }
            };
        }
    }

    private void shareEvent(EventDetails event){
        try{
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT,"Event Details");
            String temp1= (String) tvName.getText();
            String temp = "NGO : "+temp1;
            temp = temp+"\n\n\t\t\t\t\tEvent : "+event.getName();
            temp = temp+"\n\nDescription : "+event.getDescription();
            temp = temp+"\nLocation : "+event.getLocation();
            temp = temp + "\nStart Time : "+event.getStart_time();
            temp = temp + "\nEnd Date : "+event.getEnd_date();
            temp = temp + "\nEnd Time : "+event.getEnd_time();
            i.putExtra(Intent.EXTRA_TEXT,temp);
            startActivity(Intent.createChooser(i,"Share With"));
        } catch (Exception e){}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ngo_dashboard_option_menu,menu);
//        final MenuItem item = menu.findItem(R.id.subscribe);
////        item.setVisible(false);
////        Toast.makeText(myngo.this,"Done",Toast.LENGTH_LONG).show();
//        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("UserDatabase/subscribed/Aman");
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                for(DataSnapshot ds : dataSnapshot.getChildren()){
//                    String temp =  ds.getValue(String.class);
//                    if(temp.equals("sattvik-gmail-com")) {
//                        item.setVisible(false);
//                        Toast.makeText(myngo.this,"Done",Toast.LENGTH_LONG).show();
//                        break;
//                    }
//                }
//
//                //Log.d("list",allEvents.toString());
//
//                //setRecyclerView(allEvents);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                //progressDialog.dismiss();
//                Toast.makeText(myngo.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
//            }
//        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item){
        if(item.getItemId() == R.id.updatePage)  {
            updatePage();
            Toast.makeText(myngo.this, "Page Updated", Toast.LENGTH_SHORT).show();
        }
        else if(item.getItemId()==R.id.share) shareNgo();

        else if(item.getItemId()==R.id.subscribe) {
            subscribe();
        }

        else if(item.getItemId()==R.id.unsubscribe) {
            unsubscribe();
        }

        else if(item.getItemId()==R.id.donate) donate();

        else if(item.getItemId() == R.id.appoint) appoint();

        return super.onOptionsItemSelected(item);
    }

    private void appoint() {
        if(mNgo == null){
            getNgoDetails(mNgoEmail);
            return;
        }
        if(mUser==null){
            getUserDetails(FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail());
            return;
        }
        Intent intent = new Intent(myngo.this,appointngoact.class);
        intent.putExtra("ngoEmail",mNgo.getEmail());
        intent.putExtra("ngoName",mNgo.getName());
        intent.putExtra("userEmail",mUser.getEmail());
        intent.putExtra("userName",mUser.getName());
        startActivity(intent);
    }


    private void shareNgo(){
        if(mNgo==null) getNgoDetails(mNgoEmail);

        try{
            Intent i=new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT,"NGO Event");
            String temp = "NGO : "+mNgo.getName();
            temp = temp + "\nAddress : "+mNgo.getAddress();
            temp = temp + "\nEmail : "+mNgo.getEmail();
            temp = temp + "\nPhone Number : "+mNgo.getPhone_number();
            i.putExtra(Intent.EXTRA_TEXT,temp);
            startActivity(Intent.createChooser(i,"Share With"));
        }catch(Exception e){}
    }

   private void subscribe(){
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance(ngoApp).getReference("subscribed").child(mNgoEmail);
//       databaseReference.child("sarthak").setValue("sub").addOnCompleteListener(new OnCompleteListener<Void>() {
//           @Override
//           public void onComplete(@NonNull Task<Void> task) {
//               if(task.isSuccessful()) {
//                   Toast.makeText(myngo.this,"Subscribed Successfully",Toast.LENGTH_LONG).show();
//               }
//               else
//                   Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//           }
//       });
       if(mNgo == null){
           getNgoDetails(mNgoEmail);
           return;
       }
       if(mUser==null){
           getUserDetails(FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail());
           return;
       }
       String USER_EMAIL = mUser.getEmail();
       String USER_NAME = mUser.getName();
       String USER = USER_EMAIL.replaceAll("[^A-Za-z0-9]", "-");

       String NGO_EMAIL = mNgo.getEmail();
       String NGO_NAME = mNgo.getName();
       String NGO = NGO_EMAIL.replaceAll("[^A-Za-z0-9]","-");
       SubscriptionDetails detail = new SubscriptionDetails(NGO_EMAIL,NGO_NAME,USER_EMAIL,USER_NAME);

       DatabaseReference databaseReference1 = FirebaseDatabase.getInstance(userApp).getReference().child("subscribed").child(USER);
       databaseReference1.child(NGO).setValue(detail).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()) {
                   Toast.makeText(myngo.this,"Subscribed Successfully",Toast.LENGTH_SHORT).show();
               }
               else
                   Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
           }
       });

        databaseReference1 = FirebaseDatabase.getInstance(ngoApp).getReference().child("subscribed").child(NGO);
       databaseReference1.child(USER).setValue(detail).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()) {
                   //Toast.makeText(myngo.this,"Subscribed Successfully",Toast.LENGTH_LONG).show();
               }
//               else
//                   Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
   }

   private void unsubscribe(){
       if(mNgo == null){
           getNgoDetails(mNgoEmail);
           return;
       }
       if(mUser==null){
           getUserDetails(FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail());
           return;
       }
       String USER_EMAIL = mUser.getEmail();
       String USER_NAME = mUser.getName();
       String USER = USER_EMAIL.replaceAll("[^A-Za-z0-9]", "-");

       String NGO_EMAIL = mNgo.getEmail();
       String NGO_NAME = mNgo.getName();
       String NGO = NGO_EMAIL.replaceAll("[^A-Za-z0-9]","-");
       //String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
       DatabaseReference databaseReference1 = FirebaseDatabase.getInstance(userApp).getReference("subscribed").child(USER);
       databaseReference1.child(NGO).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()) {
                   Toast.makeText(myngo.this,"Unsubscribed Successfully",Toast.LENGTH_SHORT).show();
               }
               else
                   Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
           }
       });

        databaseReference1 = FirebaseDatabase.getInstance(ngoApp).getReference("subscribed/"+NGO);
       databaseReference1.child(USER).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()) {
                   //Toast.makeText(myngo.this,"Unsubscribed Successfully",Toast.LENGTH_LONG).show();
               }
               //else
                   //Toast.makeText(myngo.this, "Failed : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
   }

   private void donate(){
       if(mNgo == null){
           getNgoDetails(mNgoEmail);
           return;
       }
       if(mUser==null){
           getUserDetails(FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail());
           return;
       }
        Intent intent = new Intent(myngo.this,donatescreen.class);
        intent.putExtra("ngoEmail",mNgo.getEmail());
        intent.putExtra("ngoName",mNgo.getName());
        intent.putExtra("userEmail",mUser.getEmail());
        intent.putExtra("userName",mUser.getName());
        startActivity(intent);
   }
    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }
}
