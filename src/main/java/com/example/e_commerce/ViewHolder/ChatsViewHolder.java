package com.example.e_commerce.ViewHolder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.e_commerce.Interface.itemClickListener;
import com.example.e_commerce.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public TextView ProductName, date;
    private itemClickListener itemClickListener;
    public View v;
    public ImageView n;
    public ImageView chat_image;

    public ChatsViewHolder(@NonNull View itemView) {
        super(itemView);

        ProductName = (TextView) itemView.findViewById(R.id.chats_product_name);
        date = (TextView) itemView.findViewById(R.id.chats_date);
        n = itemView.findViewById(R.id.chat_next);
        chat_image = itemView.findViewById(R.id.chat_image);
    }


    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(), false );
    }

    public void setItemClickListener(itemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }
}
