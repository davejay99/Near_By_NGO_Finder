package com.example.demo1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo1.DataClass.SubscriptionDetails;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class subact extends AppCompatActivity {
    TextView normal;
    RecyclerView recycle;
    FirebaseApp userApp;
    Toolbar toolbar;
    ProgressBar progressBar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subscribeact);
        Database.getInstance().initialiseUserApp(getApplicationContext());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Subscribed NGOs");
        userApp = FirebaseApp.getInstance("userApp");
        normal = findViewById(R.id.normal);
        recycle = findViewById(R.id.recycle);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        updatePage();
    }
    private void updatePage() {
        if(isInternet()==false) {
            Toast.makeText(this, "You Are Offline", Toast.LENGTH_SHORT).show();
            return;
        }
        init();
    }
    public void init(){
//        if(isInternet()==false) {
//            normal.setText("No Internet Connection");
//            normal.setVisibility(View.VISIBLE);
//            recycle.setVisibility(View.GONE);
//            return;
//        }
//        final ProgressDialog progressDialog = new ProgressDialog(this);
//        progressDialog.setTitle("Updating..");
//        progressDialog.setCanceledOnTouchOutside(false);
//        try {
//            progressDialog.show();
//        }catch (Exception e) {Toast.makeText(this,"Failed: "+e.getMessage(),Toast.LENGTH_SHORT).show();
//                                return;}
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<SubscriptionDetails> list3 = new ArrayList<>();
//        SubscriptionDetails gg = new SubscriptionDetails("rr","dd@gmail.com");
//        list3.add(gg);
        String user = FirebaseAuth.getInstance(userApp).getCurrentUser().getEmail().replaceAll("[^A-Za-z0-9]","-");
        DatabaseReference dR = FirebaseDatabase.getInstance(userApp).getReference("subscribed").child(user);
        dR.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list3.clear();

                for(DataSnapshot ngo1 : dataSnapshot.getChildren()){
                    //Toast.makeText(subact.this,"done",Toast.LENGTH_SHORT).show();
                    SubscriptionDetails temp=ngo1.getValue(SubscriptionDetails.class);
                    list3.add(temp);
                }
                //progressDialog.dismiss();
                progressBar.setVisibility(View.INVISIBLE);

                    //recycle.setLayoutManager(new LinearLayoutManager(subact.this));
//                    DataAdapter L = new DataAdapter(subact.this,list3);
//                    recycle.setAdapter(L);
//                    L.notifyDataSetChanged();

                setRecycleview(list3);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(subact.this,"Failed: "+databaseError.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void setRecycleview(ArrayList<SubscriptionDetails> list){
        if(list.size() == 0){
            normal.setVisibility(View.VISIBLE);
            recycle.setVisibility(View.GONE);
            normal.setText("You haven't subscribed any NGO");
        }

        else{
            normal.setVisibility(View.GONE);
            recycle.setVisibility(View.VISIBLE);
            Log.d("list",list.toString());
            list = filter(list);
            recycle.setNestedScrollingEnabled(false);
            recycle.setLayoutManager(new LinearLayoutManager(subact.this));
            recycle.setAdapter(new DataAdapter(subact.this,list));
        }


    }

    public ArrayList<SubscriptionDetails> filter(ArrayList<SubscriptionDetails> list){
        Collections.sort(list, new Comparator<SubscriptionDetails>() {
            @Override
            public int compare(SubscriptionDetails o1, SubscriptionDetails o2) {
                String temp1 = o1.getNgoName().toLowerCase();
                String temp2 = o2.getNgoName().toLowerCase();
                if(temp1.compareTo(temp2)<0) return -1;
                else return 1;
            }
        });
        return list;
    }

//    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder>{
//        Context context;
//        ArrayList<SubscriptionDetails> list;
//        public  DataAdapter(Context context, ArrayList<SubscriptionDetails> list){
//            this.context = context;
//            this.list=list;
//        }
//
//
//        @NonNull
//        @Override
//        public DataAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subscribeactrecycle,parent,false);
//            return new DataAdapter.ViewHolder(view);
//        }
//
//        @Override
//        public void onBindViewHolder(@NonNull DataAdapter.ViewHolder holder, int position) {
//
//                holder.setIsRecyclable(false);
//                holder.text1.setText(list.get(position).getNgoName());
//                holder.text2.setText(list.get(position).getNgoEmail());
//
//        }
//
//        @Override
//        public int getItemCount() {
//            return list.size();
//        }
//
//        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//            TextView text1,text2;
//            public ViewHolder(@NonNull View itemView) {
//                super(itemView);
//                text1 = findViewById(R.id.text1);
//                text2 = findViewById(R.id.text2);
//            }
//
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(subact.this,myngo.class);
//                intent.putExtra("ngoEmail",list.get(getLayoutPosition()).getNgoEmail());
//                startActivity(intent);
//            }
//        }
//    }


    public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {

        ArrayList<SubscriptionDetails> ngoList;
        Context context;

        public DataAdapter(Context context, ArrayList<SubscriptionDetails> ngoList) {
            this.ngoList = ngoList;
            this.context = context;
        }

        @Override
        public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.subscribeactrecycle,parent,false);
            return new DataAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
            holder.setIsRecyclable(false);
            //Toast.makeText(subact.this,ngoList.get(position).getNgoEmail(),Toast.LENGTH_LONG).show();
            String first = ngoList.get(position).getNgoName().toString();
            holder.tvName.setText(first);
            holder.tvDistance.setText(ngoList.get(position).getNgoEmail());
        }

        @Override
        public int getItemCount() {
            return ngoList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView tvName,tvDistance;

            public ViewHolder(View itemView) {
                super(itemView);

                tvName =  itemView.findViewById(R.id.tvName);
                tvDistance =  itemView.findViewById(R.id.tvDistance);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(subact.this,myngo.class);
                //Toast.makeText(subact.this,ngoList.get(getLayoutPosition()).getNgoEmail(),Toast.LENGTH_LONG).show();
                intent.putExtra("ngoEmail",ngoList.get(getLayoutPosition()).getNgoEmail());
                startActivity(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.submenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.update) {
            updatePage();
            Toast.makeText(subact.this,"Page Updated",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    public boolean isInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;
        return false;
    }
}
