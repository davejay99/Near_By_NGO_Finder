package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class signin extends AppCompatActivity {
    EditText editText,editText2;
    Button button;
    TextView textView,textView2;
    FirebaseApp ngoApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signinact);
        editText = findViewById(R.id.editText);
        editText2 = findViewById(R.id.editText2);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);
        Database.getInstance().initializeNgoApp(getApplicationContext());
        ngoApp = FirebaseApp.getInstance("ngoApp");
        if(FirebaseAuth.getInstance(ngoApp).getCurrentUser() != null){
            Intent intent = new Intent(signin.this,ngodashboardact.class);

            startActivity(intent);
            finish();
        }
        init();
    }
    public void init(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternet()){
                    Toast.makeText(signin.this,"You Are Offline",Toast.LENGTH_SHORT).show();
                    return;
                }
                SignIn();
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(signin.this,signup.class);
                startActivity(intent);
            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isInternet()){
                    Toast.makeText(signin.this,"You Are Offline",Toast.LENGTH_SHORT).show();
                    return;
                }
                forgotPassword();
            }
        });
    }

    public void SignIn(){
       final String email =  editText.getText().toString().replaceAll("\\s+$","");
       String password = editText2.getText().toString();
       if(email.isEmpty() || password.isEmpty()){
           Toast.makeText(this,"All fields are Mandatory",Toast.LENGTH_SHORT).show();
           return;
       }
       FirebaseAuth.getInstance(ngoApp).signInWithEmailAndPassword(email,password)
               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){
                           Intent intent = new Intent(signin.this,ngodashboardact.class);
                           finish();
                           startActivity(intent);

                       }
                       else{
                           Toast.makeText(signin.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();;
                       }
                   }
               });
    }

    public void forgotPassword(){
        final String email =  editText.getText().toString();
        if(email.isEmpty()){
            Toast.makeText(this,"Provide Email First",Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseAuth.getInstance(ngoApp).sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(signin.this,"Password reset link has been sent. Check your MailBox",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            Toast.makeText(signin.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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
