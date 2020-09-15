package com.example.demo1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

public class signinact extends AppCompatActivity {
    EditText etEmail,etPassword;
    Button btnSignIn;
    TextView tvSignUp,tvForgotPassword;
    FirebaseApp userApp;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        Database database = Database.getInstance();
        database.initialiseUserApp(getApplicationContext());
        database.initialiseNgoApp(getApplicationContext());

        userApp = FirebaseApp.getInstance("userApp");
        if(FirebaseAuth.getInstance(userApp).getCurrentUser() != null){
            userDashboard();
        }
        init();

    }

    public void init(){
        etEmail = (EditText) findViewById(R.id.email);
        etPassword = (EditText) findViewById(R.id.password);

        btnSignIn = (Button) findViewById(R.id.signin);
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        tvSignUp = (TextView) findViewById(R.id.tvSignUp);
        tvSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignUp();
            }
        });

        tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
        tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgotPassword();
            }
        });
    }

    private void signIn(){
        String email = etEmail.getText().toString().replaceAll("\\s+$","");
        String password = etPassword.getText().toString();

        if(email.isEmpty() || password.isEmpty()){
            Toast.makeText(this,"All Fields Are Mandatory",Toast.LENGTH_SHORT).show();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(signinact.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Authenticating");
        try {
            progressDialog.show();
        }catch (Exception e) {return;}

        FirebaseAuth.getInstance(userApp).signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(signinact.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if(task.isSuccessful()) userDashboard();
                        else Toast.makeText(signinact.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                    }
                });
    }

    public void forgotPassword(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        final String userEmail = etEmail.getText().toString();

        if(userEmail.isEmpty()) Toast.makeText(this,"EmailID not Found",Toast.LENGTH_SHORT).show();
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);
            builder.setTitle("Send password reset link?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog.setMessage("Sending Mail..");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();

                    FirebaseAuth.getInstance(userApp).sendPasswordResetEmail(userEmail).addOnCompleteListener(signinact.this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful())
                                Toast.makeText(signinact.this,"Password reset link is successfully sent",Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(signinact.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            });
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            builder.create().show();
        }
    }

    private  void startSignUp(){
        Intent intent = new Intent(signinact.this,signup.class);
        startActivity(intent);
    }

    private void userDashboard(){
        Intent intent = new Intent(signinact.this,userdashboard.class);
        finish();
        startActivity(intent);
    }
}
