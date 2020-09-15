package com.example.demo1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo1.DataClass.EventDetails;
import com.example.demo1.DataClass.SubscriptionDetails;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class userdashboard extends AppCompatActivity {
    Toolbar toolbar;
    TextView tvMessage;
    RecyclerView recyclerView;
    String category;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    boolean isCategory=false;
    FirebaseApp userApp,ngoApp;
    ProgressBar progressBar;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.udashboard);
        toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Home");
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init(){
        Database database = Database.getInstance();
        database.initialiseUserApp(getApplicationContext());
        database.initialiseNgoApp(getApplicationContext());
        isCategory = false;
        userApp = FirebaseApp.getInstance("userApp");
        ngoApp = FirebaseApp.getInstance("ngoApp");

        tvMessage = (TextView) findViewById(R.id.tvMessage);
        recyclerView = (RecyclerView) findViewById(R.id.recycle);
        tvMessage.setVisibility(View.INVISIBLE);
        toolbar =  findViewById(R.id.toolbar6);
        progressBar = findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        setNavigationDrawer();

        updatePage();
    }

    public void setNavigationDrawer(){
        drawerLayout = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(userdashboard.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
       drawerLayout.addDrawerListener(toggle);
       toggle.syncState();
       navigationView = findViewById(R.id.navigate);
       navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener(){

           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               int id = item.getItemId();

               if(id==R.id.navNearByNgo) {
                   if(!Database.getInstance().havePermissions(userdashboard.this)) {
                       Database.getInstance().requestPermission(userdashboard.this);
                       return false;
                   }
                   Intent intent = new Intent(userdashboard.this,nearbyngo.class);
                   startActivity(intent);

               } else if(id==R.id.navDonationHistory) {
            Intent intent=new Intent(userdashboard.this,DonationList.class);
            startActivity(intent);
               } else if(id==R.id.navSubscribedNgo) {
            Intent intent = new Intent(userdashboard.this, subact.class);
            startActivity(intent);
               }
               else if(id == R.id.appointlist){
                    appoint();

               }

               else if(id==R.id.navSignOut) {
                   signOut();
               }

               drawerLayout.closeDrawer(GravityCompat.START);
               return false;
           }
       });

    }

    private void appoint() {
        if(isInternet() ==false){
            Toast.makeText(this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Choose One");

        builder.setPositiveButton(" Upcoming ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                Intent intent = new Intent(userdashboard.this, upcomingappointlist.class);
                startActivity(intent);


            }
        });
        builder.setNegativeButton(" Past ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(userdashboard.this, appointlist.class);
                startActivity(intent);
            }
        });
        builder.create().show();

//        Intent intent = new Intent(userdashboard.this, appointlist.class);
//        startActivity(intent);
    }

    private void updatePage(){
        if(isInternet() ==false){
            Toast.makeText(this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }

        getsubscribedngos();
    }

    private void getsubscribedngos(){
        final ArrayList<SubscriptionDetails> ngodetails = new ArrayList<>();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Fetching Subscribed NGO Details .....");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try { progressDialog.show(); }
//        catch(Exception e) { return; }

        progressBar.setVisibility(View.VISIBLE);

        String user = FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(userApp).getReference("subscribed").child(user);
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ngodetails.clear();
                for(DataSnapshot ngoSnapshot : dataSnapshot.getChildren()) {
                    SubscriptionDetails temp = ngoSnapshot.getValue(SubscriptionDetails.class);
                    ngodetails.add(temp);
                }
                //progressDialog.dismiss();

                if(isInternet()) getngoevents(ngodetails,progressBar);
                else{
//                    tvMessage.setVisibility(View.VISIBLE);
//                    recyclerView.setVisibility(View.GONE);
//                    tvMessage.setText("You Are Offline");
                        Toast.makeText(userdashboard.this,"You Are Offline",Toast.LENGTH_SHORT).show();
                        return;
                     }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //progressDialog.dismiss();

                Toast.makeText(userdashboard.this,"Failed: "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getngoevents(final ArrayList<SubscriptionDetails> ngodetails, final ProgressBar progressBar){
        if(ngodetails.size()==0){
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvMessage.setText("Nothing to Show!");
            progressBar.setVisibility(View.INVISIBLE);

            return;
        }

      final ArrayList<EventDetails> eventdetails = new ArrayList<>();
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Fetching Subscribed NGO Events .....");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try { progressDialog.show(); }
//        catch(Exception e) { return; }

        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("EventDetails");
        final int[] c = {0};
        for(int i=0;i<ngodetails.size();i++){
            String key = ngodetails.get(i).getNgoEmail().replaceAll("[^A-Za-z0-9]", "-");
            DatabaseReference Dr = dR.child(key);
            Dr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot eD: dataSnapshot.getChildren()){
                        EventDetails temp = (EventDetails) eD.getValue(EventDetails.class);
                        if(isCategory==true){
                            if(temp.getCategory().equalsIgnoreCase(category))
                                eventdetails.add(temp);
                        }
                        else
                           eventdetails.add(temp);
                    }
                    c[0]++;
                    if(c[0] ==ngodetails.size()){
                        //progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                        setRecyclerView(eventdetails);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    c[0]++;
                    if(c[0] ==ngodetails.size()){
                        //progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(userdashboard.this, "Failed : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void setRecyclerView(ArrayList<EventDetails> recyclerlist){
        if(recyclerlist.size()==0) {
            recyclerView.setVisibility(View.GONE);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("No Events from your subscribed NGOs");
            return;
        }
        tvMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(userdashboard.this));
        recyclerView.setAdapter(new DataAdapter(recyclerlist,userdashboard.this));
    }

    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
        ArrayList<EventDetails> eventList;
        Context context;

        public DataAdapter(ArrayList<EventDetails> eventList, Context context) {
            this.eventList = eventList;
            this.context = context;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_recycler_view,parent,false);

            return new DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
                holder.setIsRecyclable(false);

                EventDetails event = eventList.get(position);
                holder.tvCategory.setText("Category: "+event.getCategory());
            holder.tvCategory.setText("Category : " + event.getCategory());
            holder.tvName.setText("Event Name : "+event.getName());
            holder.tvOrganisedBy.setText("Organised By : "+event.getOrganisedBy());
            holder.tvDescription.setText("Description : "+event.getDescription());
            holder.tvLocation.setText("Location : "+event.getLocation());
            holder.tvStartDate.setText("Start Date : "+event.getStart_date());
            holder.tvStartTime.setText("Start Time : "+event.getStart_time());
            holder.tvEndDate.setText("End Date : "+event.getEnd_date());
            holder.tvEndTime.setText("End Time : "+event.getEnd_time());
        }

        @Override
        public int getItemCount() {
            return eventList.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            TextView tvName, tvCategory, tvOrganisedBy, tvDescription, tvLocation, tvStartDate, tvStartTime, tvEndDate, tvEndTime;
            public ViewHolder(View itemView){
                super(itemView);
                tvCategory = itemView.findViewById(R.id.tvCategory);
                tvName = (TextView) itemView.findViewById(R.id.tvName);
                tvOrganisedBy = (TextView) itemView.findViewById(R.id.tvOrganiseBy);
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
                MenuItem share;
                share = menu.add(0,1,1,"Share Event");
                share.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        shareEvent(eventList.get(getLayoutPosition()));
                        return true;
                    }
                });
            }
        }
    }

    private void shareEvent(EventDetails event){
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, "NGO Event");

            String sAux = "Name : "+event.getName();
            sAux = sAux + "\n\nDescription : "+event.getDescription();
            sAux = sAux + "\n\nLocation : "+event.getLocation();
            sAux = sAux + "\n\nStart Date : "+event.getStart_date();
            sAux = sAux + "\nStart Time : "+event.getStart_time();
            sAux = sAux + "\n\nEnd Date : "+event.getEnd_date();
            sAux = sAux + "\nEnd Time : "+event.getEnd_time();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose One"));
        } catch(Exception e) {}
    }


    private void signOut(){
        FirebaseAuth.getInstance(userApp).signOut();
        startActivity(new Intent(userdashboard.this,signinact.class));
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.useroption,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.updatePage) {
            isCategory = false;
            updatePage();
            Toast.makeText(userdashboard.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }
        if (item.getItemId() == R.id.filter) {
            LayoutInflater inflater = getLayoutInflater();
            View alertLayout = inflater.inflate(R.layout.custom, null);
            final EditText etCategory = alertLayout.findViewById(R.id.CC);

            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(this);
            alert.setTitle("Category");
            alert.setView(alertLayout);
            alert.setPositiveButton("Apply", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String temp = etCategory.getText().toString();
                    if (temp.isEmpty())
                        Toast.makeText(userdashboard.this, "Please provide category", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            category = temp;
                            isCategory = true;
                            updatePage();
                        } catch (Exception e) {
                        }
                    }
                }
            });
            android.app.AlertDialog dialog = alert.create();
            dialog.show();

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START);
        else
        super.onBackPressed();
    }

    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;

        return false;
    }


}
