package com.example.myapplication;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.DataClass.SubscribedDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class subscribedusersact extends AppCompatActivity {
    FirebaseApp ngoApp;
    RecyclerView recycle;
    TextView textView;
    Toolbar toolbar;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribedusers);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        recycle = findViewById(R.id.recycle);
        textView = findViewById(R.id.textView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        textView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
        setSupportActionBar(toolbar);
        init();
    }

    public void init(){
        if(!isInternet()){
            textView.setVisibility(View.VISIBLE);
            //Toast.makeText(subscribedusersact.this,"You Are Offline",Toast.LENGTH_SHORT).show();
            return;
        }
        
        getSubscribedUsers();
    }

    private void getSubscribedUsers() {
        final ArrayList<SubscribedDetails> SubList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        String NGO = FirebaseAuth.getInstance(ngoApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(ngoApp).getReference("subscribed").child(NGO);
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                SubList.clear();
                for(DataSnapshot user : dataSnapshot.getChildren()){
                    SubscribedDetails temp = user.getValue(SubscribedDetails.class);
                    SubList.add(temp);
                }
                progressBar.setVisibility(View.INVISIBLE);
                setRecycleView(SubList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setRecycleView(ArrayList<SubscribedDetails> SubList) {
        if(SubList.size() == 0){
            textView.setVisibility(View.VISIBLE);
            textView.setText("Empty");
            recycle.setVisibility(View.INVISIBLE);
        }

        else{
            textView.setVisibility(View.INVISIBLE);
            recycle.setVisibility(View.VISIBLE);
        }

        recycle.setNestedScrollingEnabled(false);
        recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycle.setAdapter(new DataAdapter(subscribedusersact.this,SubList));
    }


    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
        Context context;
        ArrayList<SubscribedDetails> SubList;

        public DataAdapter(Context context, ArrayList<SubscribedDetails> subList) {
            this.context = context;
            this.SubList = subList;
        }

        @NonNull
        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribedrecycle,parent,false);
            return new DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            holder.name.setText(SubList.get(position).getUserName());
            holder.email.setText(SubList.get(position).getUserEmail());
        }

        @Override
        public int getItemCount() {
            return SubList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView name,email;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.name);
                email = itemView.findViewById(R.id.email);
            }
        }
    }

//    public void setRecyclerView(ArrayList<SubscribedDetails> recyclerList) {
//
//        if(recyclerList.size()==0) {
//            textView.setText("No One Subscribed Your NGO !!!");
//            textView.setVisibility(View.VISIBLE);
//            recycle.setVisibility(View.GONE);
//        } else {
//            textView.setVisibility(View.GONE);
//            recycle.setVisibility(View.VISIBLE);
//        }
//
//        recycle.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//        recycle.setAdapter(new DataAdapter(subscribedusersact.this, recyclerList));
//    }
//
//    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
//
//        ArrayList<SubscribedDetails> ngoList;
//        Context context;
//
//        public DataAdapter(Context context, ArrayList<SubscribedDetails> ngoList) {
//            this.ngoList = ngoList;
//            this.context = context;
//        }
//
//        @Override
//        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribedrecycle, parent, false);
//            return new DataAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
//            holder.setIsRecyclable(false);
//            holder.tvName.setText(ngoList.get(position).getUserName());
//        }
//
//        @Override
//        public int getItemCount() {
//            return ngoList.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder {
//
//            TextView tvName;
//            public ViewHolder(View itemView) {
//                super(itemView);
//                tvName =  itemView.findViewById(R.id.name);
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.subscribedmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.update){
            init();
            Toast.makeText(subscribedusersact.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isInternet() {
        ConnectivityManager cM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cM.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                cM.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        return false;
    }
}
