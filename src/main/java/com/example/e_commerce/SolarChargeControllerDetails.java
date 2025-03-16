package com.example.e_commerce;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import de.hdodenhof.circleimageview.CircleImageView;

public class SolarChargeControllerDetails extends AppCompatActivity {



    private TextView product_description, compatibility, special_features, delete_txt, edit_txt;
    private TextView product_price;
    private TextView product_address;
    private TextView product_phone_num;
    private TextView Seller_Nick;
    private RelativeLayout product_message, product_details_cart;
    private String productID = "";
    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private CircleImageView circleImageView;
    private String Details;
    private String image;
    int number = 0;
    Boolean LikeChecker = false;
    int countLikes;
    private DatabaseReference Likes;
    private FirebaseAuth mAuth;
    private String CurrentUserID, DatabaseUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_charge_controller_details);


        productID = getIntent().getStringExtra("p_uid");

        Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        product_description = (TextView) findViewById(R.id.solar_charge_product_details_description);
        product_price = (TextView) findViewById(R.id.solar_charge_product_details_price);
        product_address = (TextView) findViewById(R.id.solar_charge_product_details_address);
        product_phone_num = (TextView) findViewById(R.id.solar_charge_call_me_baby);
        compatibility = (TextView) findViewById(R.id.solar_charge_product_details_type);
        special_features = (TextView) findViewById(R.id.solar_charge_product_details_power);
        product_message = findViewById(R.id.solar_charge_product_message);
        circleImageView = findViewById(R.id.solar_charge_watch_seller_image);
        delete_txt = findViewById(R.id.delete_txt_solar_charge);
        edit_txt = findViewById(R.id.edit_txt_solar_charge);

        Seller_Nick = findViewById(R.id.solar_charge_seller_nick);

        product_details_cart = findViewById(R.id.solar_charge_product_details_cart);


        product_phone_num.setVisibility(View.VISIBLE);


        recyclerView = findViewById(R.id.rec_solar_charge);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("location").startAt(productID).endAt(productID), Products.class)
                        .build();

        final FirebaseRecyclerAdapter<Products, product_view_holder> adapter =
                new FirebaseRecyclerAdapter<Products, product_view_holder>(options) {
                    @NonNull
                    @Override
                    public product_view_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_items_layout2, viewGroup, false);
                        product_view_holder holder = new product_view_holder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull final product_view_holder holder, int position, @NonNull final Products model) {

                        Details = getRef(position).getKey();


                        holder.txtProductName.setText(model.getPname());
                        holder.date.setText(model.getDate() + ", " + model.getTime());
                        holder.txtUserName.setText(model.getUser_name());
                        Picasso.get().load(model.getUser_image()).into(holder.circleImageView);
                        Picasso.get().load(model.getImage()).into(holder.imageView);
                        holder.setLikeButtonStatus(Details);

                        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
                        databaseReference.child(productID).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                final Products products = dataSnapshot.getValue(Products.class);
                                if (dataSnapshot.exists()) {

                                    product_description.setText(products.getDescription());
                                    product_price.setText("$" + products.getPrice());
                                    product_address.setText(products.getPaddress());
                                    product_phone_num.setText(products.getPhone());
                                    compatibility.setText(products.getCompatible_with());
                                    special_features.setText(products.getSpec_features());
                                    delete_txt.setText("Add to Cart");
                                    edit_txt.setText("Type a Message");
                                    Picasso.get().load(products.getUser_image()).into(circleImageView);
                                    Seller_Nick.setText(products.getUser_name());


                                    DatabaseUserID = (String) dataSnapshot.child("uid").getValue();

                                    circleImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (CurrentUserID.equals(DatabaseUserID)) {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, MyProfileActivity.class);
                                                startActivity(intent);

                                            } else {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, UserData.class);
                                                intent.putExtra("uid", DatabaseUserID);
                                                startActivity(intent);
                                            }
                                        }
                                    });


                                    holder.circleImageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (CurrentUserID.equals(DatabaseUserID)) {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, MyProfileActivity.class);
                                                startActivity(intent);

                                            } else {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, UserData.class);
                                                intent.putExtra("uid", DatabaseUserID);
                                                startActivity(intent);
                                            }
                                        }
                                    });



                                    holder.imageView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(holder.imageView.getContext(), ImageViewerInDetails.class);
                                            intent.putExtra("image", products.getImage());
                                            intent.putExtra("p_uid", productID);
                                            intent.putExtra("visit_user", DatabaseUserID);
                                            startActivity(intent);

                                        }
                                    });

                                    product_message.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(SolarChargeControllerDetails.this, ChatActivity.class);
                                            intent.putExtra("visit_user", DatabaseUserID);
                                            intent.putExtra("p_uid", Details);
                                            startActivity(intent);
                                        }
                                    });
                                    product_phone_num.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent intent = new Intent(Intent.ACTION_CALL);
                                            String number = product_phone_num.getText().toString().replaceAll("-", "");
                                            intent.setData(Uri.parse("tel:" + number));
                                            if (ActivityCompat.checkSelfPermission(SolarChargeControllerDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            startActivity(intent);
                                        }
                                    });

                                    Seller_Nick.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            if (CurrentUserID.equals(DatabaseUserID)) {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, MyProfileActivity.class);
                                                startActivity(intent);

                                            } else {

                                                Intent intent = new Intent(SolarChargeControllerDetails.this, UserData.class);
                                                intent.putExtra("uid", DatabaseUserID);
                                                startActivity(intent);

                                            }


                                        }
                                    });


                                    if (CurrentUserID.equals(DatabaseUserID)) {
                                        product_message.setVisibility(View.VISIBLE);
                                        product_phone_num.setVisibility(View.INVISIBLE);
                                        holder.cart.setVisibility(View.INVISIBLE);
                                        holder.txt_cart.setVisibility(View.INVISIBLE);
                                        delete_txt.setText("Delete Product");
                                        edit_txt.setText("Edit Product");


                                        product_message.setOnClickListener(new View.OnClickListener() {

                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(SolarChargeControllerDetails.this, EditSolarChargeController.class);
                                                intent.putExtra("p_uid", productID);
                                                startActivity(intent);
                                            }
                                        });
                                        final DatabaseReference productDel = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);
                                        product_details_cart.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Yes",
                                                                "No"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(SolarChargeControllerDetails.this);
                                                builder.setTitle("Delete it?");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        if (which == 0) {
                                                            productDel.removeValue();
                                                            Intent intent = new Intent(SolarChargeControllerDetails.this, ShopActivity.class);
                                                            startActivity(intent);
                                                            Toast.makeText(SolarChargeControllerDetails.this, "Ad has been deleted successfully.", Toast.LENGTH_SHORT).show();

                                                        }
                                                        if (which == 1) {
                                                            dialog.cancel();
                                                        }
                                                    }

                                                });
                                                builder.show();
                                            }
                                        });


                                    } else {

                                        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Products").child(productID);

                                        UsersRef.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.exists()) {
                                                    if (dataSnapshot.child("image").exists()) {

                                                        image = dataSnapshot.child("image").getValue().toString();

                                                    }
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


                                                        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                        final HashMap<String, Object> cartMap = new HashMap<>();

                                                        cartMap.put("pname", model.getPname());
                                                        cartMap.put("price", model.getPrice());
                                                        cartMap.put("address", product_address.getText());
                                                        cartMap.put("date", saveCurrentDate);
                                                        cartMap.put("time", saveCurrentTime);
                                                        cartMap.put("image", image);


                                                        cartListRef.child("User View").child(CurrentUserID).child("Products").child(productID)
                                                                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SolarChargeControllerDetails.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
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


                                                        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                        final HashMap<String, Object> cartMap = new HashMap<>();

                                                        cartMap.put("pname", model.getPname());
                                                        cartMap.put("price", model.getPrice());
                                                        cartMap.put("address", product_address.getText());
                                                        cartMap.put("date", saveCurrentDate);
                                                        cartMap.put("time", saveCurrentTime);
                                                        cartMap.put("image", image);


                                                        cartListRef.child("User View").child(CurrentUserID).child("Products").child(productID)
                                                                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SolarChargeControllerDetails.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                });


                                                product_details_cart.setOnClickListener(new View.OnClickListener() {

                                                    @Override
                                                    public void onClick(View v) {
                                                        String saveCurrentTime, saveCurrentDate;

                                                        Calendar callForDate = Calendar.getInstance();
                                                        SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                                        saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                                        SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                                        saveCurrentTime = CurrentTime.format(callForDate.getTime());


                                                        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                        final HashMap<String, Object> cartMap = new HashMap<>();

                                                        cartMap.put("pname", model.getPname());
                                                        cartMap.put("price", model.getPrice());
                                                        cartMap.put("address", product_address.getText());
                                                        cartMap.put("date", saveCurrentDate);
                                                        cartMap.put("time", saveCurrentTime);
                                                        cartMap.put("image", image);


                                                        cartListRef.child("User View").child(CurrentUserID).child("Products").child(productID)
                                                                .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                Toast.makeText(SolarChargeControllerDetails.this, "Product was added successfully", Toast.LENGTH_SHORT).show();
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
                                }


                                holder.like.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        LikeChecker = true;
                                        Likes.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                if (LikeChecker.equals(true)) {


                                                    if (dataSnapshot.child(productID).hasChild(CurrentUserID)) {

                                                        Likes.child(productID).child(CurrentUserID).removeValue();
                                                        LikeChecker = false;
                                                        holder.like.setLiked(false);
                                                        holder.display_num_of_likes.setText((Integer.toString(countLikes)));
                                                        countLikes = (int) dataSnapshot.child(productID).getChildrenCount();


                                                    } else {

                                                        Likes.child(productID).child(CurrentUserID).setValue(true);
                                                        LikeChecker = false;
                                                        holder.like.setLiked(true);
                                                        holder.display_num_of_likes.setText(Integer.toString(countLikes));
                                                        countLikes = (int) dataSnapshot.child(productID).getChildrenCount();
                                                    }


                                                }
                                            }


                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

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

        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }
}

