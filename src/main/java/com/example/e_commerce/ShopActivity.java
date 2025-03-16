package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

public class ShopActivity extends AppCompatActivity {



    private RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    private EditText InputText;
    private ImageView SearchBtn;
    private String SearchInput, from_go_back_to_shop;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private String user_image, uid;
    private CardView categories;
    private CircleImageView circleImageView;
    private DatabaseReference Likes;


    int number = 0;
    Boolean LikeChecker = false;
    int countLikes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_shop);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

        from_go_back_to_shop = getIntent().getStringExtra("visit_user");
        circleImageView = findViewById(R.id.summon_demon);



        InputText = findViewById(R.id.search_edit_text);
        SearchBtn = findViewById(R.id.search_btn);


        categories = findViewById(R.id.categories);

        recyclerView = findViewById(R.id.recycler_menu);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, NavigationActivity.class);
                startActivity(intent);
            }
        });

        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopActivity.this, CategoriesForShop.class);
                startActivity(intent);
            }
        });


        SearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SearchInput = InputText.getText().toString();
                onStart();


            }

        });


        DatabaseReference root = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user_image = dataSnapshot.child("image").getValue().toString();
                Picasso.get().load(user_image).into(circleImageView);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Paper.init(this);


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

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Products");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(reference.orderByChild("pname").startAt(SearchInput).endAt(SearchInput + "\uf8ff"), Products.class)
                        .build();

        final FirebaseRecyclerAdapter<Products, product_view_holder> adapter =
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
                        } else {
                            holder.cart.setVisibility(View.VISIBLE);
                        }

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
                                                            Toast.makeText(ShopActivity.this, "Service was added successfully", Toast.LENGTH_SHORT).show();
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
                                                        Toast.makeText(ShopActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();

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


                        holder.cart.setOnClickListener(new View.OnClickListener() {
                                                           @Override
                                                           public void onClick(View v) {

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
                                                                                           Toast.makeText(ShopActivity.this, "Service was added successfully", Toast.LENGTH_SHORT).show();
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
                                                                                       Toast.makeText(ShopActivity.this, "Product was added successfully", Toast.LENGTH_SHORT).show();

                                                                                   }
                                                                               });
                                                                           }
                                                                       }
                                                                   }

                                                                   @Override
                                                                   public void onCancelled(DatabaseError databaseError)
                                                                       {

                                                                       }

                                                                   });
                                                           }
                                                               });

                                                               holder.go_to_profile.setOnClickListener(new View.OnClickListener() {
                                                                   @Override
                                                                   public void onClick(View v) {

                                                                       final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
                                                                       databaseReference.child(Details).addValueEventListener(new ValueEventListener() {
                                                                           @Override
                                                                           public void onDataChange(DataSnapshot dataSnapshot) {
                                                                               if (dataSnapshot.exists()) {
                                                                                   uid = dataSnapshot.child("uid").getValue().toString();
                                                                               } else {

                                                                               }

                                                                               if (uid.equals(CurrentUserID))
                                                                               {
                                                                                   Intent intent1 = new Intent(ShopActivity.this, MyProfileActivity.class);
                                                                                   startActivity(intent1);
                                                                               }
                                                                               else
                                                                               {

                                                                                   Intent intent = new Intent(ShopActivity.this, UserData.class);
                                                                                   intent.putExtra("uid", uid);
                                                                                   startActivity(intent);
                                                                               }




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
                                                                           Intent intent = new Intent(ShopActivity.this, SolarPanelDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Combiner Box")) {

                                                                           Intent intent = new Intent(ShopActivity.this, CombinerBoxDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Solar Charge Controller")) {
                                                                           Intent intent = new Intent(ShopActivity.this, SolarChargeControllerDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Battery")) {
                                                                           Intent intent = new Intent(ShopActivity.this, ProductDetailsActivity.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Inverter")) {
                                                                           Intent intent = new Intent(ShopActivity.this, SolarInverterDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("DC and AC Disconnects")) {
                                                                           Intent intent = new Intent(ShopActivity.this, DCandACdetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Wires")) {
                                                                           Intent intent = new Intent(ShopActivity.this, BrakesDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Generator")) {
                                                                           Intent intent = new Intent(ShopActivity.this, GeneratorDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Electrical Motor")) {
                                                                           Intent intent = new Intent(ShopActivity.this, ElectricalMotorDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Wind Turbine Controller")) {
                                                                           Intent intent = new Intent(ShopActivity.this, SolarChargeControllerDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Gear Box")) {
                                                                           Intent intent = new Intent(ShopActivity.this, GearBoxDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Power Converter")) {
                                                                           Intent intent = new Intent(ShopActivity.this, PowerConverterDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getCategory().equals("Other Details")) {
                                                                           Intent intent = new Intent(ShopActivity.this, BrakesDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getJob().equals("Job Offer")) {
                                                                           Intent intent = new Intent(ShopActivity.this, SolarPanelInstallationDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       } else if (model.getJob().equals("Job Search")) {
                                                                           Intent intent = new Intent(ShopActivity.this, SolarPanelInstallationDetails.class);
                                                                           intent.putExtra("visit_user", from_go_back_to_shop);
                                                                           intent.putExtra("p_uid", Details);
                                                                           startActivity(intent);
                                                                       }


                                                                   }
                                                               });


                                    }
                                };
        recyclerView.setAdapter(adapter);
        adapter.startListening();
        updateUserStatus("online");
    }



    @Override
    protected void onStop() {
        super.onStop();

        if (CurrentUserID!=null)
        {
            updateUserStatus("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (CurrentUserID!=null)
        {
            updateUserStatus("offline");
        }

    }
}
