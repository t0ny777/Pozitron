package com.example.e_commerce;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class RegisterActivity extends AppCompatActivity
{
    private CardView CreateAccountButton;
    private EditText UserEmail, UserPassword;


    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog loadingBar;
    private FirebaseUser firebaseUser;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();
        firebaseUser = mAuth.getCurrentUser();
        UserEmail = findViewById(R.id.user_email);
        UserPassword = findViewById(R.id.user_password);



        InitializeFields();


        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                CreateNewAccount();
            }
        });
    }




    private void CreateNewAccount()
    {
        final String email = UserEmail.getText().toString();
        final String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email))
        {
            Toast.makeText(this, "Please enter email...", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please enter password...", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Creating New Account");
            loadingBar.setMessage("Please wait, while we wre creating new account for you...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if (task.isSuccessful())
                            {
                                String User_Unique_ID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                String currentUserID = mAuth.getCurrentUser().getUid();
                                String itd = mAuth.getCurrentUser().getEmail();
                                RootRef.child("Users").child(currentUserID).setValue(User_Unique_ID);
                                RootRef.child("Users").child(currentUserID).setValue(email);
                                RootRef.child("Users").child(currentUserID).setValue(password);


                                RootRef.child("Users").child(currentUserID).child("uid")
                                        .setValue(User_Unique_ID);
                                RootRef.child("Users").child(currentUserID).child("email")
                                        .setValue(email);
                                RootRef.child("Users").child(currentUserID).child("password")
                                        .setValue(password);


                                SendUserToMainActivity();
                                Toast.makeText(RegisterActivity.this, "Account Created Successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().toString();
                                Toast.makeText(RegisterActivity.this, "Error : " + message, Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                        }
                    });
        }
    }




    private void InitializeFields()
    {
        CreateAccountButton = findViewById(R.id.create_account);
        UserEmail = (EditText) findViewById(R.id.user_email);
        UserPassword = (EditText) findViewById(R.id.user_password);


        loadingBar = new ProgressDialog(this);

    }


    private void SendUserToMainActivity()
    {

        Intent Intent = new Intent(RegisterActivity.this, PopUpWindow.class);
        Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(Intent);
        finish();
    }

}