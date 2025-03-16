package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.e_commerce.Model.Messages;
import com.example.e_commerce.Model.Products;
import com.example.e_commerce.ViewHolder.ChatsViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyChats extends AppCompatActivity {


    private FirebaseAuth mAuth;
    private String CurrentUserID;


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private String pname,product_image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_chats);



        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();




        recyclerView = findViewById(R.id.chats_recycler_view);
        layoutManager = new LinearLayoutManager(MyChats.this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Messages").child(CurrentUserID);
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference, Products.class)
                        .build();
        FirebaseRecyclerAdapter<Products, ChatsViewHolder> adapter = new FirebaseRecyclerAdapter<Products, ChatsViewHolder>(options) {

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chats_items_layout, viewGroup, false);
                ChatsViewHolder holder = new ChatsViewHolder(view);
                return holder;
            }


            @Override
            protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull final Products model) {

                final String Details = getRef(position).getKey();

                DatabaseReference d = FirebaseDatabase.getInstance().getReference().child("Products").child(Details);
                d.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists())
                        {

                            pname = dataSnapshot.child("pname").getValue().toString();
                            product_image = dataSnapshot.child("image").getValue().toString();
                            final String date = dataSnapshot.child("date").getValue().toString();


                            holder.ProductName.setText(pname);
                            holder.date.setText("Posted: " + date);
                            Picasso.get().load(product_image).into(holder.chat_image);


                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MyChats.this, ChatActivityTwo.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);


                                }
                            });
                        }
                                else
                                {

                                    reference.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            reference.child(Details).removeValue();
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        };

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }


}








