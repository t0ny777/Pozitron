package com.example.e_commerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class PopUpWindow extends AppCompatActivity {

    private CircleImageView user_image;
    private EditText user_name, user_status;
    private FirebaseAuth mAuth;
    private String CurrentUserID;
    private String checker = "";
    private TextView save;
    private Uri imageUri;
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_window);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();


        user_image = findViewById(R.id.set_user_image_pop);
        user_name = findViewById(R.id.edit_user_name_pop);
        user_status = findViewById(R.id.edit_user_status_pop);
        save = findViewById(R.id.save_me_pop);



        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Profile pictures");

        userInfoDisplay(user_image, user_name, user_status);


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checker.equals("clicked")) {
                    userInfoSaved();
                } else {

                }
            }
        });


        user_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checker = "clicked";

                CropImage.activity(imageUri)
                        .setAspectRatio(1, 1)
                        .setFixAspectRatio(true)
                        .start(PopUpWindow.this);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {

            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                imageUri = result.getUri();

                user_image.setImageURI(imageUri);

            } else {
                Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();

            }

        }
    }

    private void userInfoSaved() {
        if (TextUtils.isEmpty(user_name.getText().toString())) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
        }
        else if (imageUri.equals(null))
        {
            Toast.makeText(this, "Profile Image is required", Toast.LENGTH_SHORT).show();
        }
            else if (TextUtils.isEmpty(user_status.getText().toString())) {
            Toast.makeText(this, "Status is required", Toast.LENGTH_SHORT).show();

        } else if (checker.equals("clicked")) {
            uploadImage();
        }
    }


    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(CurrentUserID + ".jpg");

            uploadTask = fileRef.putFile(imageUri);

            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    return fileRef.getDownloadUrl();
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrl = task.getResult();
                                myUrl = downloadUrl.toString();

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);

                                HashMap<String, Object> userMap = new HashMap<>();
                                userMap.put("name", user_name.getText().toString());
                                userMap.put("address", user_status.getText().toString());
                                userMap.put("image", myUrl);
                                ref.updateChildren(userMap);

                                progressDialog.dismiss();

                                startActivity(new Intent(PopUpWindow.this, ShopActivity.class));
                                Toast.makeText(PopUpWindow.this, "Profile Info update successfully.", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(PopUpWindow.this, "Error.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "image is not selected.", Toast.LENGTH_SHORT).show();
        }
    }


    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullNameEditText, final EditText addressEditText) {
        final DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);

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
                        } else {

                        }


                        fullNameEditText.setText(name);
                        addressEditText.setText(address);


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
