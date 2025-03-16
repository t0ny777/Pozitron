package com.example.e_commerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class NavigationActivity extends AppCompatActivity {

    private CircleImageView circleImageView;
    private TextView name;
    private String CurrentUserID;
    private FirebaseAuth mAuth;
    private RelativeLayout three, four, five, six, seven, eight, nine;
    private RelativeLayout two;
    private RelativeLayout one;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();


        two = findViewById(R.id.two);
        three = findViewById(R.id.three);
        four = findViewById(R.id.four);
        five = findViewById(R.id.five);
        six = findViewById(R.id.six);
        seven = findViewById(R.id.seven);
        eight = findViewById(R.id.eight);
        nine = findViewById(R.id.nine);
        one = findViewById(R.id.navigation_profile);
        circleImageView = findViewById(R.id.navigation_image);
        name = findViewById(R.id.navigation_name);


        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, MyProfileActivity.class);
                startActivity(intent);
            }
        });
        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, MyChats.class);
                startActivity(intent);
            }
        });
        five.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, ProductList.class);
                startActivity(intent);
            }
        });
        six.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, SearchUsersActivity.class);
                startActivity(intent);
            }
        });
        seven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, OptionsActivity.class);
                startActivity(intent);
            }
        });
        eight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().destroy();
                Intent intent = new Intent(NavigationActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        nine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NavigationActivity.this, FriendsActivity.class);
                startActivity(intent);
                finish();
            }
        });



        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();


        DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    final String uname = dataSnapshot.child("name").getValue().toString();
                    final String uimage = dataSnapshot.child("image").getValue().toString();


                    name.setText(uname);
                    Picasso.get().load(uimage).into(circleImageView);

//                    updateUserStatus("online");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

//    DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
//
//    public void updateUserStatus(String state) {
//        String saveCurrentTime, saveCurrentDate;
//
//        Calendar calendarDate = Calendar.getInstance();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
//        saveCurrentDate = simpleDateFormat.format(calendarDate.getTime());
//
//        Calendar calendarTime = Calendar.getInstance();
//        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
//        saveCurrentTime = simpleTimeFormat.format(calendarTime.getTime());
//
//        Map map = new HashMap();
//        map.put("time", saveCurrentTime);
//        map.put("date", saveCurrentDate);
//        map.put("state", state);
//
//        UsersRef.child(CurrentUserID).updateChildren(map);
//    }
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//
//        updateUserStatus("offline");
    }

