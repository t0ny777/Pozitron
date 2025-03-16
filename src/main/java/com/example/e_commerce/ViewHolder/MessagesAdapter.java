package com.example.e_commerce.ViewHolder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.ChatActivity;
import com.example.e_commerce.ImageViewerActivity;
import com.example.e_commerce.Model.Messages;
import com.example.e_commerce.Model.Users;
import com.example.e_commerce.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {
    private FirebaseAuth mAuth;
    private DatabaseReference UsersReference;



    private List<Messages>UserMessagesList;

    public MessagesAdapter (List<Messages> UserMessagesList)
    {
        this.UserMessagesList = UserMessagesList;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{

        public TextView SenderMessageText, ReceiverMessageText;
        public CircleImageView UserProfileImage;
        public ImageView MessageReceiverPicture, MessageSenderPicture;
        public TextView seen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);


            SenderMessageText = itemView.findViewById(R.id.sender_message);
            ReceiverMessageText = itemView.findViewById(R.id.receiver_message);
            UserProfileImage = itemView.findViewById(R.id.message_profile_image);
            MessageReceiverPicture = itemView.findViewById(R.id.image_receiver);
            MessageSenderPicture = itemView.findViewById(R.id.image_sender);
//            seen = itemView.findViewById(R.id.seen);
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_layout, viewGroup, false);
        mAuth = FirebaseAuth.getInstance();
        return new MessageViewHolder(v);



    }

    @Override
    public void onBindViewHolder(@NonNull final MessageViewHolder messageViewHolder, final int i) {


        String SenderMessageID = mAuth.getCurrentUser().getUid();
        Messages messages = UserMessagesList.get(i);
        String FromUserID = messages.getFrom();
        String FromMessageType = messages.getType();



        UsersReference = FirebaseDatabase.getInstance().getReference().child("Users").child(FromUserID);
        UsersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("image")) {

                    String receiverImage = dataSnapshot.child("image").getValue().toString();
                    Picasso.get().load(receiverImage).placeholder(R.drawable.profile).into(messageViewHolder.UserProfileImage);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        messageViewHolder.ReceiverMessageText.setVisibility(View.GONE);
        messageViewHolder.UserProfileImage.setVisibility(View.GONE);
        messageViewHolder.SenderMessageText.setVisibility(View.GONE);
        messageViewHolder.MessageSenderPicture.setVisibility(View.GONE);
        messageViewHolder.MessageReceiverPicture.setVisibility(View.GONE);

//        if (messages.isIs_seen())
//        {
//
//            messageViewHolder.seen.setText("Seen");
//
//        }
//        else
//        {
//            messageViewHolder.seen.setText("Delivered");
//        }

        if (FromMessageType.equals("text")) {
            if (FromUserID.equals(SenderMessageID)) {
                messageViewHolder.SenderMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.SenderMessageText.setBackgroundResource(R.drawable.sender_message_text_background);
                messageViewHolder.SenderMessageText.setTextColor(Color.WHITE);
                messageViewHolder.SenderMessageText.setGravity(Gravity.LEFT);
                messageViewHolder.SenderMessageText.setText(messages.getMessage());
                messageViewHolder.MessageSenderPicture.setVisibility(View.GONE);
                messageViewHolder.MessageReceiverPicture.setVisibility(View.GONE);

            } else {
                messageViewHolder.ReceiverMessageText.setVisibility(View.VISIBLE);
                messageViewHolder.UserProfileImage.setVisibility(View.INVISIBLE);
                messageViewHolder.ReceiverMessageText.setBackgroundResource(R.drawable.receiver_message_text);
                messageViewHolder.ReceiverMessageText.setTextColor(Color.BLACK);
                messageViewHolder.ReceiverMessageText.setGravity(Gravity.LEFT);
                messageViewHolder.ReceiverMessageText.setText(messages.getMessage());
            }

        } else if (FromMessageType.equals("image")) ;
        {

            if (FromUserID.equals(SenderMessageID)) {
                messageViewHolder.MessageSenderPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.MessageSenderPicture);
            } else {
                messageViewHolder.UserProfileImage.setVisibility(View.VISIBLE);
                messageViewHolder.MessageReceiverPicture.setVisibility(View.VISIBLE);
                Picasso.get().load(messages.getMessage()).into(messageViewHolder.MessageReceiverPicture);
            }

        }
        if (FromUserID.equals(SenderMessageID) || FromUserID!=(SenderMessageID)) {
            messageViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (UserMessagesList.get(i).getType().equals("image")) {

                        CharSequence options[] = new CharSequence[]
                                {
                                        "View",
                                        "Cancel"
                                };
                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
                        builder.setTitle("Options: ");
                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which == 2) {

//                                    DeleteSentMessages(i, messageViewHolder);
//                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ChatActivity.class);
//                                    messageViewHolder.itemView.getContext().startActivity(intent);

                                }
                                else if (which == 0) {

                                    Intent intent = new Intent(messageViewHolder.itemView.getContext(), ImageViewerActivity.class);
                                    intent.putExtra("url", UserMessagesList.get(i).getMessage());
                                    intent.putExtra("image", UserMessagesList.get(i).getMessage());
                                    messageViewHolder.itemView.getContext().startActivity(intent);


                                } else if (which == 1) {
                                    dialog.cancel();

                                }


                            }
                        });
                        builder.show();

//                    } else if (UserMessagesList.get(i).getType().equals("text")) {
//                        CharSequence options[] = new CharSequence[]
//                                {
//                                        "Delete",
//                                        "Cancel"
//
//
//                                };
//                        AlertDialog.Builder builder = new AlertDialog.Builder(messageViewHolder.itemView.getContext());
//                        builder.setTitle("Options: ");
//                        builder.setItems(options, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                                if (which == 0) {
//
//                                    DeleteSentMessages(i, messageViewHolder);
//
//
//                                } else if (which == 1) {
//
//                                }
//                            }
//                        });
//                        builder.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return UserMessagesList.size();
    }
    private void DeleteSentMessages(final int i, final MessageViewHolder holder) {

        final DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")

                .child(UserMessagesList.get(i).getP_id())
                .child(UserMessagesList.get(i).getMessage_id())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Message was deleted", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(holder.itemView.getContext(), ChatActivity.class);
                    holder.itemView.getContext().startActivity(intent);



                } else {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void DeleteReceivedMessages(final int position, final MessageViewHolder holder)
    {

        DatabaseReference RootRef = FirebaseDatabase.getInstance().getReference();
        RootRef.child("Messages")
                .child(UserMessagesList.get(position).getTo())
                .child(UserMessagesList.get(position).getFrom())
                .child(UserMessagesList.get(position).getMessage())
                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    Toast.makeText(holder.itemView.getContext(), "Message was deleted", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(holder.itemView.getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (UserMessagesList.get(position).equals(UserMessagesList.size()-1))
        {

        }

    }
}