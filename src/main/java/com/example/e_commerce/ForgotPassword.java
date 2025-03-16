package com.example.e_commerce;

import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText editText;
    private ImageView cardView;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        mAuth = FirebaseAuth.getInstance();

        editText = findViewById(R.id.edittext);
        cardView = findViewById(R.id.cardview);

        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_email = editText.getText().toString();
                if (TextUtils.isEmpty(user_email))
                {
                    Toast.makeText(ForgotPassword.this, "Please, enter your email", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mAuth.sendPasswordResetEmail(user_email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful())
                            {
                                Toast.makeText(ForgotPassword.this, "Password Link was sent to your Email Account. Please, check it.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPassword.this, LoginActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
