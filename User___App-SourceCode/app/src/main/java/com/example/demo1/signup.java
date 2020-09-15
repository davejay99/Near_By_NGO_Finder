package com.example.demo1;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demo1.DataClass.UserDetails;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class signup extends AppCompatActivity {
    EditText etName,etAddress,etPhoneNumber,etEmail,etPassword,etConfirmPassword;
    Button btnSignUp;
    static String name,address,phone_number,email,password,confirm_password;
    FirebaseApp userApp;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        init();
    }

    private void init() {

        userApp = FirebaseApp.getInstance("userApp");

        etName = (EditText) findViewById(R.id.etName);
        etAddress = (EditText) findViewById(R.id.etAddress);
        etPhoneNumber = (EditText) findViewById(R.id.etPhoneNumber);
        etEmail = (EditText) findViewById(R.id.etEmail);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etConfirmPassword = (EditText) findViewById(R.id.etConfirmPassword);

        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    private void signUp(){
        name = etName.getText().toString();
        address = etAddress.getText().toString();
        phone_number = etPhoneNumber.getText().toString();
        email = etEmail.getText().toString().replaceAll("\\s+$", "");
        password = etPassword.getText().toString();
        confirm_password = etConfirmPassword.getText().toString();

        if(name.isEmpty() || address.isEmpty() || phone_number.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
            Toast.makeText(this, "All Details Are Mandatory", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.compareTo(confirm_password) != 0){
            Toast.makeText(this, "Both Passwords Must Be Same", Toast.LENGTH_SHORT).show();
            return;
        }

        if (phone_number.length() != 10) {
            Toast.makeText(this, "Phone Number should be 10 digits long", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog = new ProgressDialog(signup.this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Authenticating ...");
        try { progressDialog.show(); }
        catch(Exception e) { return; }

        FirebaseAuth.getInstance(userApp).createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(signup.this,new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()) uploadetails();
                        else{
                            progressDialog.dismiss();
                            Toast.makeText(signup.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadetails(){
        UserDetails user = new UserDetails(name,address,phone_number,email);
        String key = user.getEmail().replaceAll("[^A-Za-z0-9]","-");

        DatabaseReference databaseReference = FirebaseDatabase.getInstance(userApp).getReference("UserDetails");
        databaseReference.child(key).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    finish();
                }
                else
                    Toast.makeText(signup.this,"Failed: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}
