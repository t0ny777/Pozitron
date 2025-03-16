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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FriendsRequestActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String SenderUserID;
    private FirebaseAuth mAuth;
    private DatabaseReference FriendsRef, AnotherFriendsRef;
    private String CURRENT_STATE, name, image, state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_request);

        mAuth = FirebaseAuth.getInstance();
        SenderUserID = mAuth.getCurrentUser().getUid();

        FriendsRef = FirebaseDatabase.getInstance().getReference().child("FriendsRequests");
        AnotherFriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");


        recyclerView = findViewById(R.id.requests_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("FriendsRequests").child(SenderUserID);
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
                        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference().child("Users").child(position1);
                        RootRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    name = dataSnapshot.child("name").getValue().toString();
                                    image = dataSnapshot.child("image").getValue().toString();



                                        holder.name.setText(name);
                                        Picasso.get().load(image).into(holder.imageView);


                                            holder.imageView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(FriendsRequestActivity.this, UserData.class);
                                                    intent.putExtra("uid", position1);
                                                    startActivity(intent);
                                        }
                                            });

                                    }



                                DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("FriendsRequests").child(SenderUserID).child(position1);
                                root.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {

                                            final String request_type = dataSnapshot.child("request_type").getValue().toString();
                                            if (request_type.equals("received")) {
                                                holder.name.setText(name);
                                                Picasso.get().load(image).into(holder.imageView);
                                                holder.profile.setVisibility(View.INVISIBLE);
                                                holder.delete.setVisibility(View.INVISIBLE);
                                                holder.send.setVisibility(View.INVISIBLE);
                                                holder.view_profile.setVisibility(View.INVISIBLE);
                                                holder.delete_once.setVisibility(View.INVISIBLE);
                                                holder.view_profile_huge_version.setVisibility(View.INVISIBLE);


                                                holder.decline.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CharSequence options[] = new CharSequence[]
                                                                {
                                                                        "Yes",
                                                                        "No"
                                                                };
                                                        final AlertDialog.Builder builder = new AlertDialog.Builder(FriendsRequestActivity.this);
                                                        builder.setTitle("Are you sure?");


                                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                if (which == 0) {

                                                                    DeclineFriendRequest();
                                                                }
                                                                if (which == 1) {
                                                                    dialog.cancel();
                                                                }
                                                            }

                                                            private void DeclineFriendRequest() {
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
                                                                                                        CURRENT_STATE = "not_friends";
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
                                            } else {


                                                holder.profile.setVisibility(View.INVISIBLE);
                                                holder.delete.setVisibility(View.INVISIBLE);
                                                holder.send.setVisibility(View.VISIBLE);
                                                holder.view_profile.setVisibility(View.VISIBLE);
                                                holder.accept.setVisibility(View.INVISIBLE);
                                                holder.decline.setVisibility(View.INVISIBLE);
                                                holder.delete_once.setVisibility(View.INVISIBLE);
                                                holder.view_profile_huge_version.setVisibility(View.INVISIBLE);
                                                holder.view_profile.setOnClickListener(new View.OnClickListener() {
                                                                                           @Override
                                                                                           public void onClick(View v) {
                                                                                               Intent intent = new Intent(FriendsRequestActivity.this, UserData.class);
                                                                                               intent.putExtra("uid", position1);
                                                                                               startActivity(intent);

                                                                                           }
                                                                                       });

                                                holder.send_req.setText("Cancel Request");
                                                holder.send.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        CancelFriendRequest();
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
                                                                                                CURRENT_STATE = "not_friends";

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

                                        AcceptFriendRequest();
                                    }


                                    private void AcceptFriendRequest() {

                                        final String saveCurrentDate;
                                        Calendar calendarDate = Calendar.getInstance();
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                                        saveCurrentDate = simpleDateFormat.format(calendarDate.getTime());

                                        AnotherFriendsRef.child(SenderUserID).child(position1).child("date").setValue(saveCurrentDate)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()) {

                                                            AnotherFriendsRef.child(position1).child(SenderUserID).child("date").setValue(saveCurrentDate)
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {

                                                                            if (task.isSuccessful()) {

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
                                                                                                                        CURRENT_STATE = "friends";
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
