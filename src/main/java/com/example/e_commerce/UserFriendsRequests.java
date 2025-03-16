package com.example.e_commerce;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class UserFriendsRequests extends AppCompatActivity {

    private TextView send_request_txt;
    private String CurrentUserID, CURRENT_STATE, SenderUserID;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private CardView send_request, decline_request;
    private DatabaseReference FriendsRef, AnotherFriendsRef, NotificationsRef;
    private String saveCurrentDate, saveCurrentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends_requests);

        mAuth = FirebaseAuth.getInstance();
        SenderUserID = mAuth.getCurrentUser().getUid();
        CurrentUserID = getIntent().getStringExtra("uid");
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");
        AnotherFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        NotificationsRef = FirebaseDatabase.getInstance().getReference().child("Friend Notifications");

        send_request = findViewById(R.id.send_friend_request);
        decline_request = findViewById(R.id.decline_friend_request);
        send_request_txt = findViewById(R.id.send_request_txt);

        CURRENT_STATE = "not_friends";

        decline_request.setVisibility(View.INVISIBLE);
        decline_request.setEnabled(false);

        FriendsRef.child(SenderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(CurrentUserID)) {
                    final String request_type = dataSnapshot.child(CurrentUserID).child("request_type").getValue().toString();
                    if (request_type.equals("sent")) {
                        CURRENT_STATE = "request_sent";
                        send_request_txt.setText("Cancel Friend Request");
                        decline_request.setVisibility(View.INVISIBLE);
                        decline_request.setEnabled(false);
                    } else if (request_type.equals("received")) {

                        CURRENT_STATE = "request_received";
                        send_request_txt.setText("Accept Friend Request");
                        decline_request.setVisibility(View.VISIBLE);
                        decline_request.setEnabled(true);

                        decline_request.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CancelFriendRequest();
                            }
                        });

                    }
                } else {
                    AnotherFriendsRef.child(SenderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(CurrentUserID)) {
                                CURRENT_STATE = "friends";
                                send_request_txt.setText("Unfriend This Person");
                                decline_request.setVisibility(View.INVISIBLE);
                                decline_request.setEnabled(false);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }


                if (!SenderUserID.equals(CurrentUserID)) {

                    send_request.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            send_request.setEnabled(false);
                            if (CURRENT_STATE.equals("not_friends")) {
                                SendFriendRequest();
                            }
                            if (CURRENT_STATE.equals("request_sent")) {
                                CancelFriendRequest();
                            }
                            if (CURRENT_STATE.equals("request_received")) {
                                AcceptFriendRequest();
                            }
                            if (CURRENT_STATE.equals("friends")) {
                                UnFriendExistingFriend();
                            }
                        }
                    });

                } else {

                    send_request.setVisibility(View.INVISIBLE);
                    decline_request.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

            private void UnFriendExistingFriend() {

                AnotherFriendsRef.child(SenderUserID).child(CurrentUserID).removeValue()
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {

                                    AnotherFriendsRef.child(CurrentUserID).child(SenderUserID).removeValue()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        send_request.setEnabled(true);
                                                        CURRENT_STATE = "not_friends";
                                                        send_request_txt.setText("Send Friend Request");
                                                        decline_request.setVisibility(View.INVISIBLE);
                                                        decline_request.setEnabled(false);

                                                    }
                                                }
                                            });
                                }
                            }
                        });

            }
        });
    }

    private void AcceptFriendRequest() {

        final String saveCurrentDate;
        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = simpleDateFormat.format(calendarDate.getTime());

        AnotherFriendsRef.child(SenderUserID).child(CurrentUserID).child("date").setValue(saveCurrentDate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            AnotherFriendsRef.child(CurrentUserID).child(SenderUserID).child("date").setValue(saveCurrentDate)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()) {

                                                FriendsRef.child(SenderUserID).child(CurrentUserID).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    FriendsRef.child(CurrentUserID).child(SenderUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        send_request.setEnabled(true);
                                                                                        CURRENT_STATE = "friends";
                                                                                        send_request_txt.setText("Unfriend This Person");
                                                                                        decline_request.setVisibility(View.INVISIBLE);
                                                                                        decline_request.setEnabled(false);

                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });

                                            }

                                        }
                                    });

                        }

                    }
                });


    }

    private void CancelFriendRequest() {

        FriendsRef.child(SenderUserID).child(CurrentUserID).removeValue()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            FriendsRef.child(CurrentUserID).child(SenderUserID).removeValue()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                send_request.setEnabled(true);
                                                CURRENT_STATE = "not_friends";
                                                send_request_txt.setText("Send Friend Request");
                                                decline_request.setVisibility(View.INVISIBLE);
                                                decline_request.setEnabled(false);

                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    private void SendFriendRequest() {

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());


        FriendsRef.child(SenderUserID).child(CurrentUserID).child("request_type").setValue("sent")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            FriendsRef.child(CurrentUserID).child(SenderUserID).child("request_type").setValue("received")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                send_request.setEnabled(true);
                                                CURRENT_STATE = "request_sent";
                                                send_request_txt.setText("Cancel Friend Request");
                                                decline_request.setVisibility(View.INVISIBLE);
                                                decline_request.setEnabled(false);

                                                HashMap<String, String>Friend_Notification = new HashMap<>();
                                                Friend_Notification.put("from", CurrentUserID);
                                                Friend_Notification.put("type", "request");
                                                Friend_Notification.put("date", saveCurrentDate + ", " + saveCurrentTime);
                                                NotificationsRef.child(SenderUserID).push()
                                                .setValue(Friend_Notification);



                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(UserFriendsRequests.this, "Error!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}
