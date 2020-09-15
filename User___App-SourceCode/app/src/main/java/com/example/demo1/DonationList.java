package com.example.demo1;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo1.DataClass.DonationDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DonationList extends AppCompatActivity {
    TextView tvMessage;
    RecyclerView recyclerView;
    FirebaseApp userApp;
    Boolean sortAmount = false,sortDate=false;
    Toolbar toolbar;
    ProgressBar progressBar ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.donationlist);
        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //toolbar.setTitle("Donation History");
        tvMessage = findViewById(R.id.textView);
        recyclerView = findViewById(R.id.recycle);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        tvMessage.setVisibility(View.INVISIBLE);
        Database.getInstance().initialiseUserApp(getApplicationContext());
        userApp=FirebaseApp.getInstance("userApp");
        getDonationList();
    }

    public void getDonationList(){
        if(isInternet() == false){
//            tvMessage.setText("You Are Offline");
//            tvMessage.setVisibility(View.VISIBLE);
//            recyclerView.setVisibility(View.GONE);
//            return;

                Toast.makeText(this,"You Are Offline",Toast.LENGTH_SHORT).show();
                return;

        }

//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setMessage("Updating...");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try{
//            progressDialog.show();
//        }catch (Exception e){e.getMessage();}


        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<DonationDetails> allEvents = new ArrayList<>();

        //String NGO = mNgoEmail.replaceAll("[^A-Za-z0-9]","-");
        String USER = FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance(userApp).getReference("DonationHistory").child(USER);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allEvents.clear();

                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    DonationDetails temp =  ds.getValue(DonationDetails.class);
                    allEvents.add(temp);
                }

                //Log.d("list",allEvents.toString());

                setRecyclerView(allEvents);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(DonationList.this,"Failed : "+databaseError.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setRecyclerView(ArrayList<DonationDetails> recyclerList){
        if(recyclerList.size()==0){
            tvMessage.setText("Nothing to show");
            tvMessage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            return;
        }

        else{
            tvMessage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if(sortAmount==true || sortDate == true) getSortedList(recyclerList);
        }

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(DonationList.this));
        recyclerView.setAdapter(new DonationList.DataAdapter(DonationList.this,recyclerList));
    }

    private void getSortedList(ArrayList<DonationDetails> recyclerList) {
        if(sortAmount){
            Collections.sort(recyclerList, new Comparator<DonationDetails>() {
                @Override
                public int compare(DonationDetails o1, DonationDetails o2) {
                    if(Double.parseDouble(o1.getAmount()) > Double.parseDouble(o2.getAmount())) return 1;
                    else return -1;
                }
            });
        }

        if(sortDate){
            Collections.sort(recyclerList, new Comparator<DonationDetails>() {
                @Override
                public int compare(DonationDetails o1, DonationDetails o2) {
                    if(Date.parse(o1.getDateTime()) > Date.parse(o1.getDateTime())) return 1;
                    else return -1;
                }
            });
        }

    }

    public class DataAdapter extends RecyclerView.Adapter<DonationList.DataAdapter.ViewHolder>{
        ArrayList<DonationDetails> eventDetailsList;
        Context context;
        public DataAdapter(Context context, ArrayList<DonationDetails> eventDetailsList) {
            this.eventDetailsList=eventDetailsList;
            this.context=context;
        }

        @NonNull
        @Override
        public DonationList.DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donationrecycle,parent,false);
            return new DonationList.DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DonationList.DataAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            DonationDetails event = eventDetailsList.get(position);
            holder.tvName.setText("NGO : "+event.getNgoName());
            holder.tvEmail.setText("NGO Email : "+event.getNgoEmail());
            holder.tvAmount.setText("Amount : "+event.getAmount());
            holder.tvDate.setText("Date/Time: "+event.getDateTime());

        }

        @Override
        public int getItemCount() {
            return eventDetailsList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView tvName,tvEmail,tvAmount,tvDate;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvEmail = itemView.findViewById(R.id.tvEmail);
                tvName =  itemView.findViewById(R.id.tvName);
                tvAmount = (TextView) itemView.findViewById(R.id.amount);
                tvDate = itemView.findViewById(R.id.date1);

            }


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.donationmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.update){
            getDonationList();
            Toast.makeText(DonationList.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }

        if(item.getItemId() == R.id.sortByAmount){
            sortAmount = true;
            getDonationList();
        }

        if(item.getItemId() == R.id.sortByDate){
            sortDate = true;
            getDonationList();
        }
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
