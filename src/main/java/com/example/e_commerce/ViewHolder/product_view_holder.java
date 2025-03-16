package com.example.e_commerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.e_commerce.ShopActivity;
import com.like.LikeButton;

import com.example.e_commerce.Interface.itemClickListener;
import com.example.e_commerce.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class product_view_holder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView txtProductName, txtProductDescr, txtProductPrice, getTxtProductAddress, seller_nick, txtUserName, date, time;
    public CircleImageView circleImageView;
    public ImageView imageView, cart;
    public itemClickListener listener;
    int countLikes;
    String CurrentUserID;
    public LikeButton like;
    DatabaseReference LikeRef;
    public TextView display_num_of_likes, txt_cart;
    public RelativeLayout go_to_profile;

    public product_view_holder(@NonNull View itemView) {
        super(itemView);


        imageView = (ImageView) itemView.findViewById(R.id.product_image);
        txtProductName = (TextView) itemView.findViewById(R.id.product_name);
//        txtProductPrice = (TextView) itemView.findViewById(R.id.product_price_);
//        getTxtProductAddress = (TextView) itemView.findViewById(R.id.product_address_);
        seller_nick = (TextView) itemView.findViewById(R.id.battery_seller_nick);
        circleImageView = (CircleImageView)itemView.findViewById(R.id.show_user_circleimage);
        txtUserName = (TextView)itemView.findViewById(R.id.user_name_under_ad);
        date = (TextView)itemView.findViewById(R.id.ad_date);
        like = itemView.findViewById(R.id.like);
        display_num_of_likes = itemView.findViewById(R.id.display_num_of_likes);
        cart = itemView.findViewById(R.id.put_in_cart);
        txt_cart = itemView.findViewById(R.id.txt_cart);
        go_to_profile = itemView.findViewById(R.id.go_to_profile);


        LikeRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        CurrentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();


    }

    public void setItemClickListener(itemClickListener listener)

    {
this.listener = listener;
    }

    @Override
    public void onClick(View v) {
listener.onClick(v, getAdapterPosition(),false);
    }

    public void setLikeButtonStatus(final String AdKey) {

        LikeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(AdKey).hasChild(CurrentUserID))
                {
                    countLikes = (int)dataSnapshot.child(AdKey).getChildrenCount();
                    display_num_of_likes.setText(Integer.toString(countLikes));
                    like.setLiked(true);
                }
                else
                {
                    countLikes = (int)dataSnapshot.child(AdKey).getChildrenCount();
                    display_num_of_likes.setText(Integer.toString(countLikes));
                    like.setLiked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
