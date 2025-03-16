package com.example.e_commerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.ViewHolder.FriendsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class SearchUsersActivity extends AppCompatActivity {

    private EditText editText;
    private ImageView imageView, green_icon;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private DatabaseReference databaseReference;
    private String SearchInput;
    private DatabaseReference RootRef;
    private FirebaseAuth mAuth;
    private String CurrentUserID, state;
    private DatabaseReference FriendsRef;
    private String CURRENT_STATE;
    private DatabaseReference AnotherFriendsRef, data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_users);

        data = FirebaseDatabase.getInstance().getReference().child("Users");

        AnotherFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");

        RootRef = FirebaseDatabase.getInstance().getReference();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users");

        editText = findViewById(R.id.user_search_edit_text);
        imageView = findViewById(R.id.user_search_btn);
        recyclerView = findViewById(R.id.users1);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        CURRENT_STATE = "not_friends";



        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchInput = editText.getText().toString();

                onStart();
            }
        });
    }

    DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

    public void updateUserStatus(String state) {
        String saveCurrentTime, saveCurrentDate;

        Calendar calendarDate = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = simpleDateFormat.format(calendarDate.getTime());

        Calendar calendarTime = Calendar.getInstance();
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = simpleTimeFormat.format(calendarTime.getTime());

        Map map = new HashMap();
        map.put("time", saveCurrentTime);
        map.put("date", saveCurrentDate);
        map.put("state", state);

        UsersRef.child(CurrentUserID).updateChildren(map);


    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users");
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(reference.orderByChild("name").startAt(SearchInput).endAt(SearchInput + "\uf8ff"), Users.class)
                        .build();
        final FirebaseRecyclerAdapter<Users, FriendsViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<Users, FriendsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, int position, @NonNull final Users model) {

                        final String Details = getRef(position).getKey();


                        data.child(Details).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {

                                    if (dataSnapshot.hasChild("state"))

                                    {
                                        state = dataSnapshot.child("state").getValue().toString();

                                        if (state.equals("online"))
                                        {

                                        }
                                        else
                                        {

                                        }
                                    }

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        FriendsRef.child(CurrentUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(Details)) {
                                    final String request_type = dataSnapshot.child(Details).child("request_type").getValue().toString();
                                    if (request_type.equals("sent")) {
                                        CURRENT_STATE = "request_sent";
                                        holder.send_req.setText("Cancel Request");

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        holder.name.setText(model.getName());
                        holder.accept.setVisibility(View.INVISIBLE);
                        holder.decline.setVisibility(View.INVISIBLE);
                        holder.profile.setVisibility(View.INVISIBLE);
                        holder.delete.setVisibility(View.INVISIBLE);
                        holder.view_profile_huge_version.setVisibility(View.INVISIBLE);
                        holder.delete_once.setVisibility(View.INVISIBLE);
                        Picasso.get().load(model.getImage()).into(holder.imageView);



                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(SearchUsersActivity.this, UserData.class);
                                intent.putExtra("uid", Details);
                                startActivity(intent);
                            }
                        });


                        holder.send.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.send.setEnabled(false);
                                if (CURRENT_STATE.equals("not_friends")) {
                                    SendFriendRequest();


                                } else if (CURRENT_STATE.equals("request_sent")) {
                                    CancelFriendRequest();

                                }
                            }


                            private void CancelFriendRequest() {
                                FriendsRef.child(CurrentUserID).child(Details).removeValue()
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    FriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        holder.send.setEnabled(true);
                                                                        CURRENT_STATE = "not_friends";
                                                                        holder.send_req.setText("Send Request");


                                                                    }
                                                                }
                                                            });
                                                }
                                            }
                                        });

                            }


                            private void SendFriendRequest() {

                                FriendsRef.child(CurrentUserID).child(Details).child("request_type").setValue("sent")
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {

                                                    FriendsRef.child(Details).child(CurrentUserID).child("request_type").setValue("received")
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        holder.send.setEnabled(true);
                                                                        CURRENT_STATE = "request_sent";
                                                                        holder.send_req.setText("Cancel Request");

                                                                    }
                                                                }
                                                            });
                                                } else {

                                                }

                                            }
                                        });
                            }
                        });
                        DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Friends").child(CurrentUserID);
                        dr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(Details)) {
                                    holder.view_profile_huge_version.setVisibility(View.INVISIBLE);
                                    holder.view_profile.setVisibility(View.INVISIBLE);
                                    holder.send.setVisibility(View.INVISIBLE);
                                    holder.accept.setVisibility(View.INVISIBLE);
                                    holder.decline.setVisibility(View.INVISIBLE);
                                    holder.delete.setVisibility(View.INVISIBLE);
                                    holder.view_profile.setVisibility(View.VISIBLE);
                                    holder.profile.setVisibility(View.INVISIBLE);
                                    holder.delete_once.setVisibility(View.VISIBLE);

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                        final DatabaseReference dbr = FirebaseDatabase.getInstance().getReference().child("FriendsRequests").child(CurrentUserID);
                        dbr.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (dataSnapshot.hasChild(Details)) {
                                    final String zero = dataSnapshot.child(Details).child("request_type").getValue().toString();
                                    if (zero.equals("received")) {
                                        holder.view_profile_huge_version.setVisibility(View.INVISIBLE);
                                        holder.view_profile.setVisibility(View.INVISIBLE);
                                        holder.send.setVisibility(View.INVISIBLE);
                                        holder.accept.setVisibility(View.VISIBLE);
                                        holder.decline.setVisibility(View.VISIBLE);
                                        holder.delete.setVisibility(View.INVISIBLE);
                                        holder.view_profile.setVisibility(View.INVISIBLE);
                                        holder.profile.setVisibility(View.INVISIBLE);
                                        holder.delete_once.setVisibility(View.INVISIBLE);

                                        holder.decline.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                FriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    FriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        CURRENT_STATE = "not_friends";

                                                                                        holder.send.setVisibility(View.VISIBLE);
                                                                                        holder.decline.setVisibility(View.INVISIBLE);
                                                                                        holder.view_profile.setVisibility(View.VISIBLE);

                                                                                        holder.view_profile.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                Intent intent = new Intent(SearchUsersActivity.this, UserData.class);
                                                                                                intent.putExtra("uid", Details);
                                                                                                startActivity(intent);
                                                                                            }
                                                                                        });

                                                                                        holder.send.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {

                                                                                                holder.send.setEnabled(false);
                                                                                                if (CURRENT_STATE.equals("not_friends")) {
                                                                                                    SendRequestTo();

                                                                                                } else if (CURRENT_STATE.equals("request_sent")) {
                                                                                                    CancelFriendRequestTo();
                                                                                                }
                                                                                            }

                                                                                            private void CancelFriendRequestTo() {

                                                                                                FriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {

                                                                                                                    FriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                @Override
                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                        holder.send.setEnabled(true);
                                                                                                                                        CURRENT_STATE = "not_friends";
                                                                                                                                        holder.send_req.setText("Send Request");

                                                                                                                                    }
                                                                                                                                }
                                                                                                                            });
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }

                                            private void SendRequestTo() {

                                                FriendsRef.child(CurrentUserID).child(Details).child("request_type").setValue("sent")
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {

                                                                    FriendsRef.child(Details).child(CurrentUserID).child("request_type").setValue("received")
                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        holder.send.setEnabled(true);
                                                                                        CURRENT_STATE = "request_sent";
                                                                                        holder.send_req.setText("Cancel Request");

                                                                                    }


                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        holder.accept.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final String saveCurrentDate;
                                Calendar calendarDate = Calendar.getInstance();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                saveCurrentDate = simpleDateFormat.format(calendarDate.getTime());

                                AnotherFriendsRef.child(CurrentUserID).child(Details).child("date").setValue(saveCurrentDate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {

                                                    AnotherFriendsRef.child(Details).child(CurrentUserID).child("date").setValue(saveCurrentDate)
                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {

                                                                    if (task.isSuccessful()) {

                                                                        FriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                        if (task.isSuccessful()) {

                                                                                            FriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                CURRENT_STATE = "friends";

                                                                                                                holder.delete_once.setVisibility(View.VISIBLE);
                                                                                                                holder.delete_once.setOnClickListener(new View.OnClickListener() {
                                                                                                                    @Override
                                                                                                                    public void onClick(View v) {
                                                                                                                        CharSequence options[] = new CharSequence[]
                                                                                                                                {
                                                                                                                                        "Yes",
                                                                                                                                        "No"
                                                                                                                                };
                                                                                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(SearchUsersActivity.this);
                                                                                                                        builder.setTitle("Are you sure?");


                                                                                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                                                                            @Override
                                                                                                                            public void onClick(DialogInterface dialog, int which) {
                                                                                                                                if (which == 0) {

                                                                                                                                    UnFriendExistingFriend();
                                                                                                                                    holder.delete_once.setVisibility(View.INVISIBLE);

                                                                                                                                    holder.send.setVisibility(View.VISIBLE);
                                                                                                                                }
                                                                                                                                if (which == 1) {
                                                                                                                                    dialog.cancel();
                                                                                                                                }
                                                                                                                            }

                                                                                                                            private void UnFriendExistingFriend() {

                                                                                                                                final DatabaseReference AnotherFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

                                                                                                                                AnotherFriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                            @Override
                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                if (task.isSuccessful()) {

                                                                                                                                                    AnotherFriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                @Override
                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                    if (task.isSuccessful()) {

                                                                                                                                                                        CURRENT_STATE = "not_friends";

                                                                                                                                                                        holder.send.setOnClickListener(new View.OnClickListener() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onClick(View v) {
                                                                                                                                                                                holder.send.setEnabled(false);
                                                                                                                                                                                if (CURRENT_STATE.equals("not_friends")) {
                                                                                                                                                                                    SendRequest();

                                                                                                                                                                                } else if (CURRENT_STATE.equals("request_sent")) {
                                                                                                                                                                                    CancelFriendRequest();
                                                                                                                                                                                }
                                                                                                                                                                            }

                                                                                                                                                                            private void CancelFriendRequest() {

                                                                                                                                                                                FriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                if (task.isSuccessful()) {

                                                                                                                                                                                                    FriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                                                @Override
                                                                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                                                                        holder.send.setEnabled(true);
                                                                                                                                                                                                                        CURRENT_STATE = "not_friends";
                                                                                                                                                                                                                        holder.send_req.setText("Send Request");

                                                                                                                                                                                                                    }
                                                                                                                                                                                                                }
                                                                                                                                                                                                            });

                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        });

                                                                                                                                                                            }


                                                                                                                                                                            private void SendRequest() {
                                                                                                                                                                                FriendsRef.child(CurrentUserID).child(Details).child("request_type").setValue("sent")
                                                                                                                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                if (task.isSuccessful()) {

                                                                                                                                                                                                    FriendsRef.child(Details).child(CurrentUserID).child("request_type").setValue("received")
                                                                                                                                                                                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                                                @Override
                                                                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                                                                        holder.send.setEnabled(true);
                                                                                                                                                                                                                        CURRENT_STATE = "request_sent";
                                                                                                                                                                                                                        holder.send_req.setText("Cancel Request");
                                                                                                                                                                                                                    }
                                                                                                                                                                                                                }
                                                                                                                                                                                                            });

                                                                                                                                                                                                }
                                                                                                                                                                                            }


                                                                                                                                                                                        });

                                                                                                                                                                            }
                                                                                                                                                                        });

                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            });

                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        });

                                                                                                                            }
                                                                                                                        });
                                                                                                                        builder.show();
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

                                                } else {

                                                }
                                            }
                                        });
                            }
                        });


                        holder.delete_once.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CharSequence options[] = new CharSequence[]
                                        {
                                                "Yes",
                                                "No"
                                        };
                                final AlertDialog.Builder builder = new AlertDialog.Builder(SearchUsersActivity.this);
                                builder.setTitle("Are you sure?");


                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if (which == 0) {

                                            UnFriendExistingFriend();
                                        }
                                        if (which == 1) {
                                            dialog.cancel();
                                        }
                                    }

                                    private void UnFriendExistingFriend() {

                                        final DatabaseReference AnotherFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");

                                        AnotherFriendsRef.child(CurrentUserID).child(Details).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            AnotherFriendsRef.child(Details).child(CurrentUserID).removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {

                                                                                CURRENT_STATE = "not_friends";


                                                                            }
                                                                        }
                                                                    });

                                                            holder.view_profile.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    Intent intent = new Intent(SearchUsersActivity.this, UserData.class);
                                                                    intent.putExtra("uid", Details);
                                                                    startActivity(intent);
                                                                }
                                                            });
                                                        }
                                                    }

                                                });
                                    }
                                });
                                builder.show();
                            }
                        });





                        if (Details.equals(CurrentUserID)) {

                            holder.view_profile_huge_version.setVisibility(View.VISIBLE);
                            holder.view_profile.setVisibility(View.INVISIBLE);
                            holder.send.setVisibility(View.INVISIBLE);
                            holder.accept.setVisibility(View.INVISIBLE);
                            holder.decline.setVisibility(View.INVISIBLE);
                            holder.delete.setVisibility(View.INVISIBLE);
                            holder.view_profile.setVisibility(View.INVISIBLE);
                            holder.profile.setVisibility(View.INVISIBLE);
                            holder.delete_once.setVisibility(View.INVISIBLE);

                            holder.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchUsersActivity.this, MyProfileActivity.class);
                                    startActivity(intent);
                                }
                            });

                            holder.view_profile_huge_version.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchUsersActivity.this, MyProfileActivity.class);
                                    intent.putExtra("uid", Details);
                                    startActivity(intent);

                                }
                            });
                        } else {
                            holder.view_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(SearchUsersActivity.this, UserData.class);
                                    intent.putExtra("uid", Details);
                                    startActivity(intent);

                                }
                            });
                        }

                    }





                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends, viewGroup, false);
                        FriendsViewHolder holder = new FriendsViewHolder(view);
                        return holder;

                    }
};
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
        updateUserStatus("online");

    }


                            }

