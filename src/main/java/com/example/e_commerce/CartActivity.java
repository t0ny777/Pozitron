package com.example.e_commerce;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class CartActivity extends AppCompatActivity {


    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private int OverTotalPrice = 0;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private TextView calculator_text;
    private CardView ac, equals;
    private RelativeLayout cart_ser;
    private String category;
    int number = 0;
    Boolean LikeChecker = false;
    int countLikes;
    private DatabaseReference Likes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Likes = FirebaseDatabase.getInstance().getReference().child("Likes");

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();


        calculator_text = findViewById(R.id.calculator_text);
        cart_ser = findViewById(R.id.go_to_cart_ser);
        ac = findViewById(R.id.ac);
        equals = findViewById(R.id.equals);


        recyclerView = (RecyclerView) findViewById(R.id.cart_list);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(CartActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        cart_ser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CartActivity.this, CartForServices.class);
                startActivity(intent);
            }
        });

        equals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculator_text.setText("$" + OverTotalPrice);

            }

        });

        ac.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculator_text.setText("000000");
            }
        });


    }


    @Override
    protected void onStart() {
        super.onStart();


        final DatabaseReference cartListRef = FirebaseDatabase.getInstance().getReference().child("Cart List");
        FirebaseRecyclerOptions<Products> options =
                new FirebaseRecyclerOptions.Builder<Products>()
                        .setQuery(cartListRef
                                .child("User View").child(CurrentUserID).child("Products"), Products.class)
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

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products").child(Details);
                        databaseReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {



                                        holder.txtProductName.setText(model.getPname());
                                        holder.date.setText(model.getDate() + ", " + model.getTime());
                                        holder.txtUserName.setText(model.getUser_name());
                                        Picasso.get().load(model.getUser_image()).into(holder.circleImageView);
                                        Picasso.get().load(model.getImage()).into(holder.imageView);
                                        holder.cart.setVisibility(View.INVISIBLE);
                                        holder.txt_cart.setVisibility(View.INVISIBLE);
                                        holder.setLikeButtonStatus(Details);


                                        int oneTypeProductPrice = (Integer.parseInt(model.getPrice()));
                                        OverTotalPrice = OverTotalPrice + oneTypeProductPrice;


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

                                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                CharSequence options[] = new CharSequence[]
                                                        {
                                                                "Details",
                                                                "Remove"
                                                        };
                                                AlertDialog.Builder builder = new AlertDialog.Builder(CartActivity.this);
                                                builder.setTitle("Cart Options: ");


                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, final int which) {

                                                        if (which == 0) {
                                                            if (model.getCategory().equals("Solar Panel")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarPanelDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Combiner Box")) {
                                                                Intent intent = new Intent(CartActivity.this, CombinerBoxDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Solar Charge Controller")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarChargeControllerDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Battery")) {
                                                                Intent intent = new Intent(CartActivity.this, ProductDetailsActivity.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Inverter")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarInverterDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Gear Box")) {
                                                                Intent intent = new Intent(CartActivity.this, GearBoxDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Power Converter")) {
                                                                Intent intent = new Intent(CartActivity.this, PowerConverterDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("DC and AC Disconnects")) {
                                                                Intent intent = new Intent(CartActivity.this, DCandACdetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Wires")) {
                                                                Intent intent = new Intent(CartActivity.this, BrakesDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Generator")) {
                                                                Intent intent = new Intent(CartActivity.this, GeneratorDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Electrical Motor")) {
                                                                Intent intent = new Intent(CartActivity.this, ElectricalMotorDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);

                                                            } else if (model.getCategory().equals("Wind Turbine Controller")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarChargeControllerDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Gear Box")) {
                                                                Intent intent = new Intent(CartActivity.this, GearBoxDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Power Converter")) {
                                                                Intent intent = new Intent(CartActivity.this, PowerConverterDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Other Details")) {
                                                                Intent intent = new Intent(CartActivity.this, BrakesDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Solar_Panel Installation")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarPanelInstallationDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else if (model.getCategory().equals("Wind_Turbine Installation")) {
                                                                Intent intent = new Intent(CartActivity.this, SolarPanelInstallationDetails.class);
                                                                intent.putExtra("p_uid", Details);
                                                                startActivity(intent);
                                                            } else {

                                                            }
                                                        }

                                                        if (which == 1) {
                                                            cartListRef.child("User View").child(CurrentUserID)
                                                                    .child("Products")
                                                                    .child(Details)
                                                                    .removeValue()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            if (task.isSuccessful()) {
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                                builder.show();
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

    @Override
    protected void onStop() {
        super.onStop();
        OverTotalPrice = 0;
        calculator_text.setText("000000");
    }
}



