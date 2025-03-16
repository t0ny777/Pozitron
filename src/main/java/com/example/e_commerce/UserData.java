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
import android.widget.ImageView;
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

public class UserData extends AppCompatActivity {


    private String CurrentUserID, uid;
    RecyclerView.LayoutManager layoutManager;
    private ImageView green_icon, next;
    int countLikes = 0;
    int countFriends = 0;
    int countPosts = 0;
    private DatabaseReference Likes, FriendsRef, ProductsRef;
    int number = 0;
    private RelativeLayout l1, l2, l3;
    private TextView FriendsOne, PostsOne, friends_btn, posts_btn;
    private TextView PostsTen, PostsHundred, PostsThousand, PostsTenThousand;
    private TextView FriendsTen, FriendsHundred, FriendsThousand, FriendsTenThousand;
    private RecyclerView products;
    private TextView zero_posts;
    private FirebaseAuth mAuth;
    private TextView fullNameEditText, addressEditText;
    private CircleImageView profileImageView;
    Boolean LikeChecker = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_data);


        Likes = FirebaseDatabase.getInstance().getReference().child("Likes");
        zero_posts = findViewById(R.id.zero_posts_f);

        uid = getIntent().getStringExtra("uid");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();


        profileImageView = (CircleImageView) findViewById(R.id.user_data_profile_image);
        fullNameEditText = findViewById(R.id.user_data_user_name);
        addressEditText = findViewById(R.id.user_data_profile_status);
//        FriendsOne = findViewById(R.id.num_of_friends_1);
        FriendsTen = findViewById(R.id.num_of_friends_2_f);
//        FriendsHundred = findViewById(R.id.num_of_friends_3);
//        FriendsThousand = findViewById(R.id.num_of_friends_4);
//        FriendsTenThousand = findViewById(R.id.num_of_friends_5);

//        FriendsTen.setVisibility(View.INVISIBLE);
//        FriendsHundred.setVisibility(View.INVISIBLE);
//        FriendsThousand.setVisibility(View.INVISIBLE);
//        FriendsTenThousand.setVisibility(View.INVISIBLE);


//        PostsOne = findViewById(R.id.num_of_posts_1);
        PostsTen = findViewById(R.id.num_of_posts_2_f);
//        PostsHundred = findViewById(R.id.num_of_posts_3);
//        PostsThousand = findViewById(R.id.num_of_posts_4);
//        PostsTenThousand = findViewById(R.id.num_of_posts_5);

//        PostsTen.setVisibility(View.INVISIBLE);
//        PostsHundred.setVisibility(View.INVISIBLE);
//        PostsThousand.setVisibility(View.INVISIBLE);
//        PostsTenThousand.setVisibility(View.INVISIBLE);


        friends_btn = findViewById(R.id.friends_btn_f);
        posts_btn = findViewById(R.id.posts_btn_f);
        FriendsRef = FirebaseDatabase.getInstance().getReference().child("Friends");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");


        userInfoDisplay(profileImageView, fullNameEditText, addressEditText);
        l1 = findViewById(R.id.li_1);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserData.this, UserFriendsActivity.class);
                intent.putExtra("uid", uid);
                startActivity(intent);
            }
        });
//
//        l3.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MyProfileActivity.this, PostsActivity.class);
//                startActivity(intent);
//            }
//        });

        products = findViewById(R.id.my_own_ads1_f);
        layoutManager = new LinearLayoutManager(UserData.this);
        products.setLayoutManager(layoutManager);

        DisplayMyOwnPosts();


    }

    private void DisplayMyOwnPosts() {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("uid").startAt(uid).endAt(uid + "\uf8ff"), Products.class)
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

                        holder.cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Details);
                                UsersRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            final String category = dataSnapshot.child("category").getValue().toString();

                                            if (category.equals("Wind_Turbine Installation") || category.equals("Solar_Panel Installation")) {

                                                final String sname = dataSnapshot.child("pname").getValue().toString();
                                                final String sprice = dataSnapshot.child("wage_salary").getValue().toString();
                                                final String user__name = dataSnapshot.child("user_name").getValue().toString();
                                                final String user__image = dataSnapshot.child("user_image").getValue().toString();
                                                final String sphone = dataSnapshot.child("phone").getValue().toString();
                                                final String saddress = dataSnapshot.child("paddress").getValue().toString();
                                                final String schedule = dataSnapshot.child("schedule").getValue().toString();
                                                final String experience = dataSnapshot.child("experience").getValue().toString();
                                                final String education = dataSnapshot.child("education").getValue().toString();
                                                final String sdescription = dataSnapshot.child("description").getValue().toString();
                                                final String image_ = dataSnapshot.child("image").getValue().toString();

                                                final String company_name = dataSnapshot.child("company_name").getValue().toString();

                                                String saveCurrentTime, saveCurrentDate;

                                                Calendar callForDate = Calendar.getInstance();
                                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                                saveCurrentTime = CurrentTime.format(callForDate.getTime());


                                                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                final HashMap<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("phone", sphone);
                                                cartMap.put("pname", sname);
                                                cartMap.put("paddress", saddress);
                                                cartMap.put("wage_salary", sprice);
                                                cartMap.put("company_name", company_name);
                                                cartMap.put("date", saveCurrentDate);
                                                cartMap.put("time", saveCurrentTime);
                                                cartMap.put("user_image", user__image);
                                                cartMap.put("user_name", user__name);
                                                cartMap.put("schedule", schedule);
                                                cartMap.put("experience", experience);
                                                cartMap.put("education", education);
                                                cartMap.put("description", sdescription);
                                                cartMap.put("image", image_);


                                                cartListRef.child("User View").child(CurrentUserID).child("Services").child(Details)
                                                        .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(UserData.this, "Service was added successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {

                                                final String pname = dataSnapshot.child("pname").getValue().toString();
                                                final String price = dataSnapshot.child("price").getValue().toString();
                                                final String address = dataSnapshot.child("paddress").getValue().toString();
                                                final String image = dataSnapshot.child("image").getValue().toString();
                                                final String phone = dataSnapshot.child("phone").getValue().toString();
                                                final String category1 = dataSnapshot.child("category").getValue().toString();
                                                final String user_name = dataSnapshot.child("user_name").getValue().toString();
                                                final String user_image = dataSnapshot.child("user_image").getValue().toString();

                                                String saveCurrentTime, saveCurrentDate;

                                                Calendar callForDate = Calendar.getInstance();
                                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                                saveCurrentTime = CurrentTime.format(callForDate.getTime());


                                                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                final HashMap<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("category", category1);
                                                cartMap.put("phone", phone);
                                                cartMap.put("pname", pname);
                                                cartMap.put("price", price);
                                                cartMap.put("address", address);
                                                cartMap.put("date", saveCurrentDate);
                                                cartMap.put("time", saveCurrentTime);
                                                cartMap.put("image", image);
                                                cartMap.put("user_name", user_name);
                                                cartMap.put("user_image", user_image);


                                                cartListRef.child("User View").child(CurrentUserID).child("Products").child(Details)
                                                        .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(UserData.this, "Product was added successfully", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        });

                        holder.txt_cart.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Products").child(Details);
                                UsersRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (dataSnapshot.exists()) {
                                            final String category = dataSnapshot.child("category").getValue().toString();

                                            if (category.equals("Wind_Turbine Installation") || category.equals("Solar_Panel Installation")) {

                                                final String sname = dataSnapshot.child("pname").getValue().toString();
                                                final String sprice = dataSnapshot.child("wage_salary").getValue().toString();
                                                final String user__name = dataSnapshot.child("user_name").getValue().toString();
                                                final String user__image = dataSnapshot.child("user_image").getValue().toString();
                                                final String sphone = dataSnapshot.child("phone").getValue().toString();
                                                final String saddress = dataSnapshot.child("paddress").getValue().toString();
                                                final String schedule = dataSnapshot.child("schedule").getValue().toString();
                                                final String experience = dataSnapshot.child("experience").getValue().toString();
                                                final String education = dataSnapshot.child("education").getValue().toString();
                                                final String sdescription = dataSnapshot.child("description").getValue().toString();
                                                final String image_ = dataSnapshot.child("image").getValue().toString();

                                                final String company_name = dataSnapshot.child("company_name").getValue().toString();

                                                String saveCurrentTime, saveCurrentDate;

                                                Calendar callForDate = Calendar.getInstance();
                                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                                saveCurrentTime = CurrentTime.format(callForDate.getTime());


                                                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                final HashMap<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("phone", sphone);
                                                cartMap.put("pname", sname);
                                                cartMap.put("paddress", saddress);
                                                cartMap.put("wage_salary", sprice);
                                                cartMap.put("company_name", company_name);
                                                cartMap.put("date", saveCurrentDate);
                                                cartMap.put("time", saveCurrentTime);
                                                cartMap.put("user_image", user__image);
                                                cartMap.put("user_name", user__name);
                                                cartMap.put("schedule", schedule);
                                                cartMap.put("experience", experience);
                                                cartMap.put("education", education);
                                                cartMap.put("description", sdescription);
                                                cartMap.put("image", image_);


                                                cartListRef.child("User View").child(CurrentUserID).child("Services").child(Details)
                                                        .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(UserData.this, "Service was added successfully", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            } else {

                                                final String pname = dataSnapshot.child("pname").getValue().toString();
                                                final String price = dataSnapshot.child("price").getValue().toString();
                                                final String address = dataSnapshot.child("paddress").getValue().toString();
                                                final String image = dataSnapshot.child("image").getValue().toString();
                                                final String phone = dataSnapshot.child("phone").getValue().toString();
                                                final String category1 = dataSnapshot.child("category").getValue().toString();
                                                final String user_name = dataSnapshot.child("user_name").getValue().toString();
                                                final String user_image = dataSnapshot.child("user_image").getValue().toString();

                                                String saveCurrentTime, saveCurrentDate;

                                                Calendar callForDate = Calendar.getInstance();
                                                SimpleDateFormat CurrentDate = new SimpleDateFormat("MM dd, yyyy");
                                                saveCurrentDate = CurrentDate.format(callForDate.getTime());

                                                SimpleDateFormat CurrentTime = new SimpleDateFormat("HH:mm:ss a");
                                                saveCurrentTime = CurrentTime.format(callForDate.getTime());


                                                final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");

                                                final HashMap<String, Object> cartMap = new HashMap<>();
                                                cartMap.put("category", category1);
                                                cartMap.put("phone", phone);
                                                cartMap.put("pname", pname);
                                                cartMap.put("price", price);
                                                cartMap.put("address", address);
                                                cartMap.put("date", saveCurrentDate);
                                                cartMap.put("time", saveCurrentTime);
                                                cartMap.put("image", image);
                                                cartMap.put("user_name", user_name);
                                                cartMap.put("user_image", user_image);


                                                cartListRef.child("User View").child(CurrentUserID).child("Products").child(Details)
                                                        .updateChildren(cartMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Toast.makeText(UserData.this, "Product was added successfully", Toast.LENGTH_SHORT).show();

                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
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


                                                            if (dataSnapshot.child(Details).hasChild(CurrentUserID)) {
                                                                Likes.child(Details).child(CurrentUserID).removeValue();
                                                                LikeChecker = false;
                                                                holder.like.setLiked(false);
                                                                holder.display_num_of_likes.setText((Integer.toString(countLikes)));
                                                                countLikes = (int) dataSnapshot.child(Details).getChildrenCount();


                                                            } else {

                                                                Likes.child(Details).child(CurrentUserID).setValue(true);
                                                                LikeChecker = false;
                                                                holder.like.setLiked(true);
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

                                        if (model.getUid().equals(CurrentUserID)) {
                                            holder.cart.setVisibility(View.INVISIBLE);
                                            holder.txt_cart.setVisibility(View.INVISIBLE);
                                        }


                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (model.getCategory().equals("Solar Panel")) {
                                                    Intent intent = new Intent(UserData.this, SolarPanelDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Combiner Box")) {

                                                    Intent intent = new Intent(UserData.this, CombinerBoxDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Solar Charge Controller")) {
                                                    Intent intent = new Intent(UserData.this, SolarChargeControllerDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Battery")) {
                                                    Intent intent = new Intent(UserData.this, ProductDetailsActivity.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Inverter")) {
                                                    Intent intent = new Intent(UserData.this, SolarInverterDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("DC and AC Disconnects")) {
                                                    Intent intent = new Intent(UserData.this, DCandACdetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Wires")) {
                                                    Intent intent = new Intent(UserData.this, BrakesDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Generator")) {
                                                    Intent intent = new Intent(UserData.this, GeneratorDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Electrical Motor")) {
                                                    Intent intent = new Intent(UserData.this, ElectricalMotorDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Wind Turbine Controller")) {
                                                    Intent intent = new Intent(UserData.this, SolarChargeControllerDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Gear Box")) {
                                                    Intent intent = new Intent(UserData.this, GearBoxDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Power Converter")) {
                                                    Intent intent = new Intent(UserData.this, PowerConverterDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Other Details")) {
                                                    Intent intent = new Intent(UserData.this, BrakesDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Solar_Panel Installation")) {
                                                    Intent intent = new Intent(UserData.this, SolarPanelInstallationDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                } else if (model.getCategory().equals("Wind_Turbine Installation")) {
                                                    Intent intent = new Intent(UserData.this, SolarPanelInstallationDetails.class);
                                                    intent.putExtra("visit_user", CurrentUserID);
                                                    intent.putExtra("p_uid", Details);
                                                    startActivity(intent);
                                                }


                                            }

                                        });

                                    }

                                };
        products.setAdapter(adapter);
        adapter.startListening();
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final TextView fullNameEditText, final TextView addressEditText) {
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("name").exists()) {



                        String name = dataSnapshot.child("name").getValue().toString();
                        String address = dataSnapshot.child("address").getValue().toString();
                        if (dataSnapshot.hasChild("image")) {
                            final String image = dataSnapshot.child("image").getValue().toString();
                            Picasso.get().load(image).into(profileImageView);

                            profileImageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(UserData.this, ImageViewerActivity.class);
                                    intent.putExtra("image", image);
                                    startActivity(intent);
                                }
                            });
                        } else {

                        }


                        fullNameEditText.setText(name);
                        addressEditText.setText(address);
//                        DisplayMyOwnAds();


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        ProductsRef.orderByChild("uid").startAt(uid).endAt(uid + "\uf8ff").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {

                    zero_posts.setVisibility(View.INVISIBLE);

                    countPosts = (int) dataSnapshot.getChildrenCount();
                    PostsTen.setText(Integer.toString(countPosts));




//                    if (countPosts > 9) {
//                        PostsOne.setVisibility(View.INVISIBLE);
//                        PostsTen.setVisibility(View.VISIBLE);
//                        PostsTen.setText(Integer.toString(countPosts));
//
//                        PostsTen.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                Intent intent = new Intent(MyProfileActivity.this, PostsActivity.class);
//                                startActivity(intent);
//
//                            }
//                        });
//                    }
                    if (countPosts > 99) {
                        PostsOne.setVisibility(View.INVISIBLE);
                        PostsTen.setVisibility(View.INVISIBLE);
                        PostsHundred.setVisibility(View.VISIBLE);
                        PostsHundred.setText(Integer.toString(countPosts));

                    }
                    if (countPosts > 999) {

                        PostsOne.setVisibility(View.INVISIBLE);
                        PostsTen.setVisibility(View.INVISIBLE);
                        PostsHundred.setVisibility(View.INVISIBLE);
                        PostsThousand.setVisibility(View.VISIBLE);
                        PostsThousand.setText(Integer.toString(countPosts));


                    }

                    if (countPosts > 9999) {
                        PostsOne.setVisibility(View.INVISIBLE);
                        PostsTen.setVisibility(View.INVISIBLE);
                        PostsHundred.setVisibility(View.INVISIBLE);
                        PostsThousand.setVisibility(View.INVISIBLE);
                        PostsTenThousand.setVisibility(View.VISIBLE);
                        PostsTenThousand.setText(Integer.toString(countPosts));



                    }



                } else {

                    zero_posts.setVisibility(View.VISIBLE);
                    PostsTen.setText("0");
                    PostsTen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(UserData.this, "0 Posts, sorry", Toast.LENGTH_SHORT).show();
                        }
                    });
                    posts_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(UserData.this, "0 Posts, sorry", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        FriendsRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists())
                {

                    countFriends = (int)dataSnapshot.getChildrenCount();
                    FriendsTen.setText(Integer.toString(countFriends));
                    FriendsTen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserData.this, UserFriendsActivity.class);
                            startActivity(intent);

                        }
                    });
//                            if (countFriends > 9) {
//                                FriendsOne.setVisibility(View.INVISIBLE);
//                                FriendsTen.setVisibility(View.VISIBLE);
//                                FriendsTen.setText(Integer.toString(countFriends));
//                                FriendsTen.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        Intent intent = new Intent(MyProfileActivity.this, FriendsActivity.class);
//                                        startActivity(intent);
//
//                                    }
//                                });
//                            }
                    if (countFriends > 99) {
                        FriendsOne.setVisibility(View.INVISIBLE);
                        FriendsTen.setVisibility(View.INVISIBLE);
                        FriendsHundred.setVisibility(View.VISIBLE);

                        FriendsHundred.setText(Integer.toString(countFriends));
                        FriendsHundred.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UserData.this, FriendsActivity.class);
                                startActivity(intent);

                            }
                        });
                    }
                    if (countFriends > 999) {

                        FriendsOne.setVisibility(View.INVISIBLE);
                        FriendsTen.setVisibility(View.INVISIBLE);
                        FriendsHundred.setVisibility(View.INVISIBLE);
                        FriendsThousand.setVisibility(View.VISIBLE);
                        FriendsThousand.setText(Integer.toString(countFriends));
                        FriendsThousand.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UserData.this, FriendsActivity.class);
                                startActivity(intent);
                            }
                        });
                    }
                    if (countFriends > 9999) {
                        FriendsOne.setVisibility(View.INVISIBLE);
                        FriendsTen.setVisibility(View.INVISIBLE);
                        FriendsHundred.setVisibility(View.INVISIBLE);
                        FriendsThousand.setVisibility(View.INVISIBLE);
                        FriendsTenThousand.setVisibility(View.VISIBLE);
                        FriendsTenThousand.setText(Integer.toString(countFriends));
                        FriendsTenThousand.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(UserData.this, FriendsActivity.class);
                                startActivity(intent);

                            }
                        });
                    }

                    friends_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserData.this, FriendsActivity.class);
                            startActivity(intent);
                        }
                    });

                    FriendsTen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(UserData.this, FriendsActivity.class);
                            startActivity(intent);
                        }
                    });

                }

                else
                {
                    FriendsTen.setText("0");
                    friends_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(UserData.this, "0 Friends, sorry", Toast.LENGTH_SHORT).show();


                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
