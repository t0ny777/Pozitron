package com.example.e_commerce;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.e_commerce.Model.Products;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageViewerInDetails extends AppCompatActivity {

    private ImageView imageView;
    private String ImageUrl, image;
    private TextView product_name, product_price, product_date;
    private String productID;
    private String send_to_seller;
    private CircleImageView circleImageView;
    private RelativeLayout l1;
    private FirebaseAuth mAuth;
    private String CurrentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer_in_details);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();

        l1 = findViewById(R.id.image_view_rel);

        productID = getIntent().getStringExtra("p_uid");

        imageView = findViewById(R.id.image_details);
        ImageUrl = getIntent().getStringExtra("url");
        image = getIntent().getStringExtra("image");

        Picasso.get().load(ImageUrl).into(imageView);
        Picasso.get().load(image).into(imageView);

        circleImageView = findViewById(R.id.image_view_image);
        product_name = (TextView) findViewById(R.id.image_view_name);
        product_price = (TextView) findViewById(R.id.image_view_price);
        product_date = findViewById(R.id.image_view_date);
//        product_message =  findViewById(R.id.image_view_apply);
//        go_to_seller = findViewById(R.id.image_view_go_to_seller);

//        product_message.setVisibility(View.VISIBLE);


        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ImageViewerInDetails.this, UserData.class);
                intent.putExtra("uid", send_to_seller);
                startActivity(intent);
            }
        });


        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(imageView.getContext(), ImageViewerActivity.class);
                intent.putExtra("image", image);
                startActivity(intent);
            }
        });

        GetProductDetails();

    }

    private void GetProductDetails() {

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Products");
        databaseReference.child(productID).addValueEventListener(new ValueEventListener() {
                                                                     @Override
                                                                     public void onDataChange(DataSnapshot dataSnapshot) {

                                                                         final Products products = dataSnapshot.getValue(Products.class);
                                                                         if (dataSnapshot.exists()) {

                                                                             product_name.setText(products.getUser_name());
                                                                             product_price.setText(products.getPname());
                                                                             product_date.setText(products.getDate() + ", " + products.getTime());
                                                                             Picasso.get().load(products.getUser_image()).into(circleImageView);
                                                                             send_to_seller = products.getUid();

                                                                             if (products.getUid().equals(CurrentUserID)) {
                                                                                 l1.setOnClickListener(new View.OnClickListener() {
                                                                                     @Override
                                                                                     public void onClick(View view) {
                                                                                         Intent intent = new Intent(ImageViewerInDetails.this, MyProfileActivity.class);
                                                                                         startActivity(intent);
                                                                                     }
                                                                                 });
                                                                             }
                                                                         }


                                                                             final String string = dataSnapshot.child("uid").getValue().toString();


//                                                                             product_message.setOnClickListener(new View.OnClickListener() {
//                                                                                 @Override
//                                                                                 public void onClick(View v) {
//                                                                                     Intent intent = new Intent(ImageViewerInDetails.this, ChatActivity.class);
//                                                                                     intent.putExtra("visit_user", string);
//                                                                                     intent.putExtra("p_uid", productID);
//                                                                                     startActivity(intent);
//                                                                                 }
//                                                                             });


//                    go_to_seller.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                          Intent intent = new Intent(ImageViewerInDetails.this, UserData.class);
//                          intent.putExtra("uid", string);
//                          startActivity(intent);
//                        }
//                    });
//
//                    if (CurrentUserID.equals(string)) {
//                        product_message.setVisibility(View.INVISIBLE);
//                        go_to_seller.setVisibility(View.INVISIBLE);
//
//
//                            }
//                    else
//                    {
//
//                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
