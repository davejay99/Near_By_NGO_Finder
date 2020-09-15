package com.example.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.DataClass.EventDetails;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ngodashboardact extends AppCompatActivity {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    NavigationView navigationview;
    FirebaseApp ngoApp;
    FloatingActionButton fButton;
    String ngoEmail;
    RecyclerView recyclerView;
    TextView tvMessage;
    Boolean isCategory = false;
    String category;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ngodashboardact);

        toolbar = findViewById(R.id.toolbar);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        setSupportActionBar(toolbar);
//        toolbar.setTitle("Home");
        recyclerView = findViewById(R.id.recycle);
        tvMessage = findViewById(R.id.tvMessage);
        ngoEmail = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail();
        fButton = findViewById(R.id.floatingActionButton);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        fButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(havepermission() == false){
                    requestpermission();
                    return;
                }
                Intent intent = new Intent(ngodashboardact.this,galleryact.class);
                startActivity(intent);
            }
        });
        init();


    }

    private void init() {

        setNavigationDrawer();
        getngoevents();
    }

    private void getngoevents(){

        if(isInternet() ==false){
            Toast.makeText(this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        final ArrayList<EventDetails> eventdetails = new ArrayList<>();
        //final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Fetching Subscribed NGO Events .....");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try { progressDialog.show(); }
//        catch(Exception e) { return; }
        progressBar.setVisibility(View.VISIBLE);
        String NGO = ngoEmail.replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("EventDetails").child(NGO);
        //final int[] c = {0};
        //for(int i=0;i<ngodetails.size();i++){
//            String key = ngodetails.get(i).getNgoEmail().replaceAll("[^A-Za-z0-9]", "-");
//            DatabaseReference Dr = dR.child(key);
            dR.addValueEventListener(new ValueEventListener() {
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
//                    c[0]++;
//                    if(c[0] ==ngodetails.size()){
//                        progressDialog.dismiss();
                        progressBar.setVisibility(View.INVISIBLE);
                        setRecyclerView(eventdetails);
                    //}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
//                    c[0]++;
//                    if(c[0] ==ngodetails.size()){
//                        progressDialog.dismiss();
                    progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(ngodashboardact.this, "Failed : "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    //}
                }
            });
        }


    public void setRecyclerView(ArrayList<EventDetails> recyclerlist){
        if(recyclerlist.size()==0) {
            recyclerView.setVisibility(View.GONE);
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText("NGO do not have any Event");
            return;
        }
        tvMessage.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setLayoutManager(new LinearLayoutManager(ngodashboardact.this));
        recyclerView.setAdapter(new DataAdapter(recyclerlist,ngodashboardact.this));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.ngodashboardoption,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.updatePage) {
            isCategory = false;
            getngoevents();
            Toast.makeText(ngodashboardact.this,"Page Updated",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(ngodashboardact.this, "Please provide category", Toast.LENGTH_SHORT).show();
                    else {
                        try {
                            category = temp;
                            isCategory = true;
                            getngoevents();
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

    private void setNavigationDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(ngodashboardact.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close){
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

        navigationview = findViewById(R.id.nav_view);
        navigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.create){
                    createevent();
                }

                if(item.getItemId() == R.id.donate){
                    donate();
                }

                if(item.getItemId() == R.id.subscribe){
                    getSubscribedUsers();
                }

                if(item.getItemId() == R.id.signout){
                    signOut();
                }

                if(item.getItemId() == R.id.appointlist){
                    appoint();

                }

                if(item.getItemId() == R.id.edit){
                    editLogo();
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
        builder.setCancelable(false);
        builder.setTitle("Choose One");

        builder.setPositiveButton(" Upcoming ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

                Intent intent = new Intent(ngodashboardact.this, upcomingappointlist.class);
                startActivity(intent);


            }
        });
        builder.setNegativeButton(" Past ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(ngodashboardact.this, appointlist.class);
                startActivity(intent);
            }
        });
        builder.create().show();

//        Intent intent = new Intent(userdashboard.this, appointlist.class);
//        startActivity(intent);
    }

    private void editLogo() {
        if(havepermission() == false){
            requestpermission();
            return;
        }
        Intent intent = new Intent(ngodashboardact.this,imageact.class);
        startActivity(intent);
    }

    private void requestpermission() {
        List<String> permissionL = new ArrayList<>();
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionL.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }

        if(permissionL.size()>0) {
            String[] permissionA = new String[permissionL.size()];
            for(int i=0;i<permissionL.size();i++)
                permissionA[i] = permissionL.get(i);
            ActivityCompat.requestPermissions(ngodashboardact.this, permissionA, 101);
        }
    }

    private boolean havepermission() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            return false;
        }
        return true;
    }

    private void donate() {
        Intent intent = new Intent(ngodashboardact.this,DonationList.class);
        startActivity(intent);
    }

    private void signOut() {
        FirebaseAuth.getInstance(ngoApp).signOut();
        Intent intent = new Intent(ngodashboardact.this,signin.class);
        finish();
        startActivity(intent);

    }

    private void getSubscribedUsers() {
        Intent intent = new Intent(ngodashboardact.this,subscribedusersact.class);
        startActivity(intent);
    }

    private void createevent() {
        Intent intent = new Intent(ngodashboardact.this,createeventact.class);
        startActivity(intent);
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
