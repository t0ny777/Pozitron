package com.example.e_commerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.e_commerce.Model.Friends;
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

public class UserFriendsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseAuth mAuth;
    private String SenderUserID;
    private DatabaseReference users;
    private String CURRENT_STATE, CurrentUserID;
    private ImageView friends_req;
    private DatabaseReference FriendsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_friends);

        FriendsRef = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");

        mAuth = FirebaseAuth.getInstance();
        SenderUserID = mAuth.getCurrentUser().getUid();
        friends_req = findViewById(R.id.friends_req);

        CurrentUserID = getIntent().getStringExtra("uid");

        users = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = findViewById(R.id.user_friends_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        CURRENT_STATE = "not_friends";

        friends_req.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserFriendsActivity.this, UserFriendsRequests.class);
                intent.putExtra("uid", CurrentUserID);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(CurrentUserID);
        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(reference, Friends.class)
                        .build();

        final FirebaseRecyclerAdapter<Friends, FriendsViewHolder> adapter =
                new FirebaseRecyclerAdapter<Friends, FriendsViewHolder>(options) {
                    @NonNull
                    @Override
                    public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.friends, viewGroup, false);
                        FriendsViewHolder holder = new FriendsViewHolder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final FriendsViewHolder holder, final int position, @NonNull Friends model) {

                        final String position1 = getRef(position).getKey();
                        users.child(position1).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String name = dataSnapshot.child("name").getValue().toString();
                                String image = dataSnapshot.child("image").getValue().toString();
                                String state = dataSnapshot.child("state").getValue().toString();

                                holder.accept.setVisibility(View.INVISIBLE);
                                holder.decline.setVisibility(View.INVISIBLE);
                                holder.send.setVisibility(View.VISIBLE);
                                holder.view_profile.setVisibility(View.VISIBLE);
                                holder.delete_once.setVisibility(View.INVISIBLE);
                                holder.view_profile_huge_version.setVisibility(View.INVISIBLE);
                                holder.profile.setVisibility(View.INVISIBLE);
//                                holder.green.setVisibility(View.INVISIBLE);
                                holder.delete.setVisibility(View.INVISIBLE);

                                holder.name.setText(name);
                                Picasso.get().load(image).into(holder.imageView);

                                FriendsRef.child(SenderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(position1)) {
                                            final String request_type = dataSnapshot.child(position1).child("request_type").getValue().toString();
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

                                if (position1.equals(SenderUserID))
                                {
                                    holder.send.setVisibility(View.INVISIBLE);
                                    holder.view_profile.setVisibility(View.INVISIBLE);
                                    holder.view_profile_huge_version.setVisibility(View.VISIBLE);
                                    holder.view_profile_huge_version.setOnClickListener(new View.OnClickListener() {
                                                                                            @Override
                                                                                            public void onClick(View v) {
                                                                                                Intent intent = new Intent(UserFriendsActivity.this, MyProfileActivity.class);
                                                                                                intent.putExtra("uid", position1);
                                                                                                startActivity(intent);
                                                                                            }
                                                                                        });
                                }

                                DatabaseReference dr = FirebaseDatabase.getInstance().getReference().child("Friends").child(SenderUserID);
                                dr.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.hasChild(position1))
                                        {
                                            holder.send.setVisibility(View.INVISIBLE);
                                            holder.view_profile.setVisibility(View.INVISIBLE);
                                            holder.view_profile_huge_version.setVisibility(View.VISIBLE);
                                            holder.view_profile_huge_version.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(UserFriendsActivity.this, UserData.class);
                                                    intent.putExtra("uid", position1);
                                                    startActivity(intent);
                                                }
                                            });
                                        }

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

//                                if (state.equals("online"))
//                                {
//                                    holder.green.setVisibility(View.VISIBLE);
//                                }
//                                else
//                                {
//
//                                }

                                holder.view_profile.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(UserFriendsActivity.this, UserData.class);
                                        intent.putExtra("uid", position1);
                                        startActivity(intent);
                                    }
                                });
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(UserFriendsActivity.this, UserData.class);
                                        intent.putExtra("uid", position1);
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
                                        FriendsRef.child(SenderUserID).child(position1).removeValue()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            FriendsRef.child(position1).child(SenderUserID).removeValue()
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

                                        FriendsRef.child(SenderUserID).child(position1).child("request_type").setValue("sent")
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {

                                                            FriendsRef.child(position1).child(SenderUserID).child("request_type").setValue("received")
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

                            }



                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                                };
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recycler_view_divider));
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
