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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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

public class WindTurbineInstallation extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private ImageView InputProductImage;
    private String service_name1, job1, wage_or_salary1, schedule1, education1, experience1, company_name1, short_description1, address1, phone_num1;
    private static final int GalleryPick = 1;
    private Uri ImageUri;
    private String ProductRandomKey, DownloadImageUri, ProductUID, saveCurrentDate, saveCurrentTime, CategoryName;
    private EditText service_name, wage_or_salary, schedule, education, experience, company_name, short_description, address, phone_num;
    private StorageReference StorageImagesRef;
    private DatabaseReference ProductsRef, UsersRef;
    private ProgressDialog loadingBar;
    private FirebaseAuth mAuth;
    private long countAds = 0;
    private String CurrentUserID, user_name, user_image;
    private ImageView Add_a_New_Product, image;
    private Spinner dropdown1;
    private String schedule_zero;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wind_turbine_installation);

        dropdown1 = findViewById(R.id.drop_down1_wind_job);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(WindTurbineInstallation.this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.schedule));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown1.setAdapter(myAdapter1);
        dropdown1.setOnItemSelectedListener(this);


        mAuth = FirebaseAuth.getInstance();
        CurrentUserID = mAuth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        CategoryName = getIntent().getExtras().get("category").toString();
        StorageImagesRef = FirebaseStorage.getInstance().getReference().child("Product Images");
        ProductsRef = FirebaseDatabase.getInstance().getReference().child("Products");

        service_name = findViewById(R.id.wind_job_name);
        wage_or_salary = findViewById(R.id.wind_job_wage);
        education = findViewById(R.id.wind_job_edu);
        experience = findViewById(R.id.wind_job_experience);
        company_name = findViewById(R.id.wind_job_company_name);
        short_description = findViewById(R.id.wind_job_descr);
        address = findViewById(R.id.wind_job_address);
        phone_num = findViewById(R.id.wind_job_phone_num);
        InputProductImage = findViewById(R.id.wind_job_image);
        Add_a_New_Product = findViewById(R.id.publish_wind_job);
        image = findViewById(R.id.wind_job_image1);

        loadingBar = new ProgressDialog(this);


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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        schedule_zero = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
            if (requestCode ==  GalleryPick && resultCode == RESULT_OK && data != null)
            {
                ImageUri = data.getData();
                image.setImageURI(ImageUri);
            }


        }
    }

    private void ValidateProductData() {

        service_name1 = service_name.getText().toString();
        wage_or_salary1 = wage_or_salary.getText().toString();
        education1 = education.getText().toString();
        experience1 = experience.getText().toString();
        company_name1 = company_name.getText().toString();
        short_description1 = short_description.getText().toString();
        address1 = address.getText().toString();
        phone_num1 = phone_num.getText().toString();



        if (ImageUri == null) {
            Toast.makeText(this, "Service Image is required.", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(service_name1)) {
            Toast.makeText(this, "Please, write your service's name", Toast.LENGTH_SHORT).show();

        } else if (TextUtils.isEmpty(wage_or_salary1)) {
            Toast.makeText(this, "What about money?", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(experience1)) {
            Toast.makeText(this, "Experience?", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(short_description1)) {
            Toast.makeText(this, "Please, write Short Description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address1)) {
            Toast.makeText(this, "Please, write Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone_num1)) {
            Toast.makeText(this, "Please, write your Phone Number", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(WindTurbineInstallation.this, "Error. " + message, Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }

        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(WindTurbineInstallation.this, "Product Image uploaded successfully", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(WindTurbineInstallation.this, "Product Image has been saved Successfully!", Toast.LENGTH_SHORT).show();


                            SaveProductImageToDatabase();

                        }
                    }
                });
            }
        });

        mAuth = FirebaseAuth.getInstance();
        ProductUID = mAuth.getCurrentUser().getUid();
        ProductRandomKey = saveCurrentDate + saveCurrentTime;



    }

    private void SaveProductImageToDatabase() {
        HashMap<String, Object> ProductMap = new HashMap<>();
        ProductMap.put("counter", countAds);
        ProductMap.put("date", saveCurrentDate);
        ProductMap.put("time", saveCurrentTime);
        ProductMap.put("description", short_description1);
        ProductMap.put("image", DownloadImageUri);
        ProductMap.put("category", CategoryName);
        ProductMap.put("education", education1);
        if (!company_name1.equals(""))
        {
            ProductMap.put("company_name", company_name1);
        }
        else
        {

        }
        ProductMap.put("experience", experience1);
        ProductMap.put("pname", service_name1);
        ProductMap.put("schedule", schedule_zero);
        ProductMap.put("wage_salary", wage_or_salary1);
        ProductMap.put("job", "Job Search");
        ProductMap.put("paddress", address1);
        ProductMap.put("phone", phone_num1);
        ProductMap.put("user_image", user_image);
        ProductMap.put("user_name", user_name);
        ProductMap.put("uid", ProductUID);
        ProductMap.put("location", ProductUID + ProductRandomKey);
        ProductsRef.child(ProductUID + ProductRandomKey).updateChildren(ProductMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Intent intent = new Intent(WindTurbineInstallation.this, ShopActivity.class);
                            intent.putExtra("uid", ProductUID);
                            startActivity(intent);

                            loadingBar.dismiss();

                            Toast.makeText(WindTurbineInstallation.this, "Product is added successfully", Toast.LENGTH_SHORT).show();

                        } else {
                            loadingBar.dismiss();
                            String message = task.getException().toString();
                            Toast.makeText(WindTurbineInstallation.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                        }


                    }
                });






    }

}