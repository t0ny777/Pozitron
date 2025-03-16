package com.example.e_commerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.ViewHolder.product_view_holder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SolarChargeController extends AppCompatActivity {

    private ImageView InputProductImage;
    private String pname, compatible_with, special_features, description, paddress, price, phone;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey, DownloadImageUri, ProductUID,  saveCurrentDate, saveCurrentTime, Details, CategoryName;
    private EditText InputProductName, InputProductCompatible_with, InputProductSpecFeatures, InputProductDescr, InputProductPrice, InputProductAddress, InputProductPhoneNumber, SolarType, SolarPower;
    private StorageReference StorageImagesRef;
    private DatabaseReference ProductsRef, UsersRef;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private long countAds = 0;
    private String CurrentUserID, user_name, user_image;
    private ImageView Add_a_New_Product, image;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solar_charge_controller);

        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        CategoryName = getIntent().getExtras().get("category").toString();
        StorageImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        Add_a_New_Product = findViewById(R.id.publish_solar_charge);
        InputProductImage = (ImageView) findViewById(R.id.solar_charge_image);
        InputProductPhoneNumber = (EditText) findViewById(R.id.solar_charge_phone_num);
        InputProductName = (EditText) findViewById(R.id.solar_charge_name);
        InputProductDescr = (EditText) findViewById(R.id.solar_charge_description);
        InputProductPrice = (EditText) findViewById(R.id.solar_charge_price);
        InputProductAddress = (EditText) findViewById(R.id.solar_charge_address);
        SolarPower = findViewById(R.id.solar_charge_power);
        SolarType = findViewById(R.id.solar_charge_type);
        loadingBar = new ProgressDialog(this);
        image = findViewById(R.id.solar_charge_image1);


        InputProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenGallery();

            }


        });

        Add_a_New_Product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ValidateProductData();
            }
        });


    }


    private void OpenGallery() {

        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GalleryPick);



    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null);
        {

            if (requestCode == GalleryPick && resultCode == RESULT_OK && data!= null)
            {
                ImageUri = data.getData();

                image.setImageURI(ImageUri);
            }

        }
    }

    private void ValidateProductData() {
        description = InputProductDescr.getText().toString();
        price = InputProductPrice.getText().toString();
        pname = InputProductName.getText().toString();
        paddress = InputProductAddress.getText().toString();
        phone = InputProductPhoneNumber.getText().toString();
        compatible_with = SolarType.getText().toString();
        special_features = SolarPower.getText().toString();


        if (ImageUri == null) {
            Toast.makeText(this, "Product Image is required.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(pname)) {
            Toast.makeText(this, "Please, write name for your product", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(compatible_with)) {
            Toast.makeText(this, "what is it compatible with?", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(special_features)) {
            Toast.makeText(this, "Write Special Features Please", Toast.LENGTH_SHORT).show();


        } else if (TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Please, write description for your product", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(price)) {
            Toast.makeText(this, "How much does it cost?", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(paddress)) {
            Toast.makeText(this, "Where is it located?", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone)) {
            Toast.makeText(this, "Please, write your phone number", Toast.LENGTH_SHORT).show();

        } else {

            StoreProductInformation();

        }

        UserAccountData();
    }

    private void UserAccountData() {
        UsersRef.child(CurrentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                countAds = dataSnapshot.getChildrenCount();

                if (dataSnapshot.hasChild("name") || dataSnapshot.hasChild("image")){
                    user_name = dataSnapshot.child("name").getValue().toString();
                    user_image = dataSnapshot.child("image").getValue().toString();
                }
                else{
                    countAds = 0;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void StoreProductInformation() {

        loadingBar.setTitle("Publishing new Product ");
        loadingBar.setMessage("Dear User, please wait, while we are publishing a new product.");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calendar.getTime());




        final StorageReference filePath = StorageImagesRef.child(ImageUri.getLastPathSegment() + ProductUID);

        final UploadTask uploadTask = filePath.putFile(ImageUri);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                String message = e.toString();
                Toast.makeText(SolarChargeController.this, "Error. " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()) {
                            throw task.getException();

                        }

                        DownloadImageUri = filePath.getDownloadUrl().toString();
                        return filePath.getDownloadUrl();

                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        if (task.isSuccessful()) {
                            DownloadImageUri = task.getResult().toString();



                            SaveProductImageToDatabase();

                        }
                    }
                });
            }
        });

        mAuth = FirebaseAuth.getInstance();
        ProductUID = mAuth.getCurrentUser().getUid();
        ProductRandomKey = saveCurrentDate + saveCurrentTime;

        UsersList();


    }

    private void UsersList() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(CurrentUserID);
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(reference, Users.class)
                        .build();

        FirebaseRecyclerAdapter<Users, product_view_holder> adapter =
                new FirebaseRecyclerAdapter<Users, product_view_holder>(options) {
                    @NonNull
                    @Override
                    public product_view_holder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.products_items_layout, viewGroup, false);
                        product_view_holder holder = new product_view_holder(view);
                        return holder;
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull product_view_holder holder, int position, @NonNull final Users model) {

                        Details = getRef(position).getKey();


                        holder.txtUserName.setText(model.getName());
                        Picasso.get().load(model.getImage()).into(holder.circleImageView);

                    }
                };
    }


    private void SaveProductImageToDatabase() {
        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("counter", countAds);
        ProductMap.put("date", saveCurrentDate);
        ProductMap.put("time", saveCurrentTime);
        ProductMap.put("description", description);
        ProductMap.put("image", DownloadImageUri);
        ProductMap.put("category", CategoryName);
        ProductMap.put("price", price);
        ProductMap.put("compatible_with", compatible_with);
        ProductMap.put("spec_features", special_features);
        ProductMap.put("pname", pname);
        ProductMap.put("paddress", paddress);
        ProductMap.put("phone", phone);
        ProductMap.put("user_image", user_image);
        ProductMap.put("user_name", user_name);
        ProductMap.put("uid", ProductUID);
        ProductMap.put("location", ProductUID + ProductRandomKey);
        ProductsRef.child(ProductUID + ProductRandomKey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(SolarChargeController.this, ShopActivity.class);
                            intent.putExtra("uid", ProductUID);
                            startActivity(intent);

                            loadingBar.dismiss();

                            Toast.makeText(SolarChargeController.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(SolarChargeController.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }


                    }
                });








    }

}
