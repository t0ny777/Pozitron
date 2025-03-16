package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.e_commerce.Model.Products;
import com.example.e_commerce.ViewHolder.product_view_holder;
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

public class ShopActivity2 extends AppCompatActivity {

    private String categories;
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private String uid;
    private String SenderUserID;
    private FirebaseAuth mAuth;
    Boolean LikeChecker = false;
    int countLikes;
    private DatabaseReference Likes;
    int number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop2);

        categories = getIntent().getStringExtra("category");
        Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mAuth = FirebaseAuth.getInstance();
        SenderUserID = mAuth.getCurrentUser().getUid();

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference1.orderByChild("category").startAt(categories).endAt(categories + "\uf8ff"), Products.class)
                        .build();

        FirebaseRecyclerAdapter<Products, product_view_holder> adapter =
                new FirebaseRecyclerAdapter<Products, product_view_holder>(options) {
                    @NonNull
                    @Override
                    public product_view_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_items_layout, viewGroup, false);
                        product_view_holder holder = new product_view_holder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final product_view_holder holder, int position, @NonNull final Products model) {

                        final String Details = getRef(position).getKey();
                        holder.txtProductName.setText(model.getPname());
                        holder.date.setText(model.getDate() + ", " + model.getTime());
                        holder.txtUserName.setText(model.getUser_name());
                        Picasso.get().load(model.getUser_image()).into(holder.circleImageView);
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        holder.setLikeButtonStatus(Details);

                        if (model.getUid().equals(SenderUserID)) {
                            holder.cart.setVisibility(View.INVISIBLE);
                            holder.txt_cart.setVisibility(View.INVISIBLE);
                        }

                        holder.txt_cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String saveCurrentTime, saveCurrentDate;

                                Calendar callForDate = Calendar.getInstance();
                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                saveCurrentTime = CurrentTime.format(callForDate.getTime());

                                if (model.getCategory().equals("Solar_Panel Installation") || model.getCategory().equals("Wind_Turbine Installation")) {
                                    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                    final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("phone", model.getPhone());
                                    cartMap.put("pname", model.getPname());
                                    cartMap.put("price", model.getPrice());
                                    cartMap.put("address", model.getPaddress());
                                    cartMap.put("date", saveCurrentDate);
                                    cartMap.put("time", saveCurrentTime);
                                    cartMap.put("image", model.getImage());


                                    cartListRef.child("User View").child(SenderUserID).child("Services").child(Details)
                                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ShopActivity2.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                    final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("phone", model.getPhone());
                                    cartMap.put("pname", model.getPname());
                                    cartMap.put("price", model.getPrice());
                                    cartMap.put("address", model.getPaddress());
                                    cartMap.put("date", saveCurrentDate);
                                    cartMap.put("time", saveCurrentTime);
                                    cartMap.put("image", model.getImage());


                                    cartListRef.child("User View").child(SenderUserID).child("Products").child(Details)
                                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ShopActivity2.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                        }

                                    });
                                }
                            }
                        });


                        holder.cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String saveCurrentTime, saveCurrentDate;

                                Calendar callForDate = Calendar.getInstance();
                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                saveCurrentTime = CurrentTime.format(callForDate.getTime());

                                if (model.getCategory().equals("Solar_Panel Installation") || model.getCategory().equals("Wind_Turbine Installation")) {
                                    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                    final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("phone", model.getPhone());
                                    cartMap.put("pname", model.getPname());
                                    cartMap.put("price", model.getPrice());
                                    cartMap.put("address", model.getPaddress());
                                    cartMap.put("date", saveCurrentDate);
                                    cartMap.put("time", saveCurrentTime);
                                    cartMap.put("image", model.getImage());


                                    cartListRef.child("User View").child(SenderUserID).child("Services").child(Details)
                                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ShopActivity2.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                    final HashMap<String, Object> cartMap = new HashMap<>();
                                    cartMap.put("phone", model.getPhone());
                                    cartMap.put("pname", model.getPname());
                                    cartMap.put("price", model.getPrice());
                                    cartMap.put("address", model.getPaddress());
                                    cartMap.put("date", saveCurrentDate);
                                    cartMap.put("time", saveCurrentTime);
                                    cartMap.put("image", model.getImage());


                                    cartListRef.child("User View").child(SenderUserID).child("Products").child(Details)
                                            .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ShopActivity2.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                        }


                                    });
                                }
                            }
                        });


                        holder.like.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

                                                               LikeChecker = true;
                                                               Likes.addValueEventListener(new ValueEventListener() {
                                                                   @Override
                                                                   public void onDataChange(DataSnapshot dataSnapshot) {

                                                                       if (LikeChecker.equals(true)) {

                                                                           if (dataSnapshot.child(Details).hasChild(SenderUserID)) {
                                                                               Likes.child(Details).child(SenderUserID).removeValue();
                                                                               LikeChecker = false;
                                                                               holder.display_num_of_likes.setText((Integer.toString(countLikes) ));
                                                                               countLikes = (int) dataSnapshot.child(Details).getChildrenCount();


                                                                           } else {
                                                                               Likes.child(Details).child(SenderUserID).setValue(true);
                                                                               LikeChecker = false;
                                                                               holder.display_num_of_likes.setText(Integer.toString(countLikes));
                                                                               countLikes = (int) dataSnapshot.child(Details).getChildrenCount();
                                                                           }

                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(DatabaseError databaseError) {

                                                                   }
                                                               });
                                                           }
                                                       });

                        holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
                                databaseReference.child(Details).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            uid = dataSnapshot.child("uid").getValue().toString();
                                        } else {

                                            Intent intent = new Intent(ShopActivity2.this, UserData.class);
                                            startActivity(intent);

                                        }
                                        Intent intent = new Intent(ShopActivity2.this, UserData.class);
                                        intent.putExtra("uid", uid);
                                        startActivity(intent);


                                    }


                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {


                                    }
                                });


                            }
                        });
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (model.getCategory().equals("Solar Panel")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarPanelDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Combiner Box")) {

                                    Intent intent = new Intent(ShopActivity2.this, CombinerBoxDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Solar Charge Controller")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarChargeControllerDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Battery")) {
                                    Intent intent = new Intent(ShopActivity2.this, ProductDetailsActivity.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Inverter")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarInverterDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("DC and AC Disconnects")) {
                                    Intent intent = new Intent(ShopActivity2.this, DCandACdetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Wires")) {
                                    Intent intent = new Intent(ShopActivity2.this, BrakesDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Generator")) {
                                    Intent intent = new Intent(ShopActivity2.this, GeneratorDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Electrical Motor")) {
                                    Intent intent = new Intent(ShopActivity2.this, ElectricalMotorDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Wind Turbine Controller")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarChargeControllerDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Gear Box")) {
                                    Intent intent = new Intent(ShopActivity2.this, GearBoxDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Power Converter")) {
                                    Intent intent = new Intent(ShopActivity2.this, PowerConverterDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Other Details")) {
                                    Intent intent = new Intent(ShopActivity2.this, BrakesDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Solar_Panel Installation")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarPanelInstallationDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);
                                } else if (model.getCategory().equals("Wind_Turbine Installation")) {
                                    Intent intent = new Intent(ShopActivity2.this, SolarPanelInstallationDetails.class);
                                    intent.putExtra("p_uid", Details);
                                    startActivity(intent);

                                }

                            }
                        });
                    }
                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();

    }
}
