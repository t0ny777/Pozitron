package com.example.e_commerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.e_commerce.Interface.itemClickListener;
import com.example.e_commerce.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private itemClickListener itemClickListener;
    public CircleImageView imageView, imageView2;
    public TextView name, send_req, state, name2;
    public CardView profile, delete, accept, decline, send, view_profile, view_profile_huge_version, delete_once;
    public ImageView green;


    public FriendsViewHolder(@NonNull View itemView) {
        super(itemView);

        imageView = itemView.findViewById(R.id.friends_image);
        name = itemView.findViewById(R.id.friends_user_name);
        imageView2 = itemView.findViewById(R.id.friends_image2);
        name2 = itemView.findViewById(R.id.friends_user_name2);
        profile = itemView.findViewById(R.id.friend_profile);
        delete = itemView.findViewById(R.id.friend_delete);
        accept = itemView.findViewById(R.id.friend_accept);
        decline = itemView.findViewById(R.id.friend_decline);
        send = itemView.findViewById(R.id.friend_send);
        view_profile = itemView.findViewById(R.id.friend_profile1);
        send_req = itemView.findViewById(R.id.send_req_txt);
        view_profile_huge_version = itemView.findViewById(R.id.friend_view_profile_huge_version);
        delete_once = itemView.findViewById(R.id.friend_delete_once);
//        state = itemView.findViewById(R.id.state);
//        green = itemView.findViewById(R.id.online);

    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false );

    }

    public void setItemClickListener(itemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
