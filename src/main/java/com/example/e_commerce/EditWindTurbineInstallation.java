package com.example.e_commerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.HashMap;

public class EditWindTurbineInstallation extends AppCompatActivity {

    private ImageView ApplyChanges;
    private EditText name, wage_salary, description, address, phone_number, experience, company_name, education;
    private ImageView imageView;
    private Uri imageUri;
    private DatabaseReference productDel;
    private String productPID = "";
    private String checker = "";
    private String myUrl = "";
    private StorageTask uploadTask;
    private StorageReference storageProfilePrictureRef;
    private static final int GalleryPick = 1;
    private String comp_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wind_turbine_installation);

        productPID = getIntent().getStringExtra("p_uid");
        productDel = FirebaseDatabase.getInstance().getReference().child("Products").child(productPID);
        storageProfilePrictureRef = FirebaseStorage.getInstance().getReference().child("Product Images");


        ApplyChanges = findViewById(R.id.wind_job_apply_changes);
        name = findViewById(R.id.wind_job_edit_ad_name);
        description = findViewById(R.id.wind_job_edit_ad_description);
        address = findViewById(R.id.wind_job_edit_ad_address);
        phone_number = findViewById(R.id.wind_job_edit_ad_phone_number);
        imageView = findViewById(R.id.wind_job_edit_ad_image);
        wage_salary = findViewById(R.id.wind_job_edit_ad_wage);
        experience = findViewById(R.id.wind_job_edit_ad_experience);
        company_name = findViewById(R.id.wind_job_edit_ad_company_name);
        education = findViewById(R.id.wind_job_edit_ad_education);


        DisplaySpecificProductInfo(name, description, wage_salary, address, phone_number, imageView, education, experience, company_name);

        ApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checker.equals("clicked")) {
                    ProductInfoSaved();
                } else {
                    updateOnlyProductInfo();
                }
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checker = "clicked";

                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GalleryPick);
            }

        });

    }

    private void DisplaySpecificProductInfo(final EditText name, final EditText description, final EditText wage_salary, final EditText address, final EditText phone_number, final ImageView imageView, final EditText education, final EditText experience, final EditText company_name) {
        final DatabaseReference productDel = FirebaseDatabase.getInstance().getReference().child("Products").child(productPID);
        productDel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String Pname = (String) dataSnapshot.child("pname").getValue();
                    String Pprice = (String) dataSnapshot.child("experience").getValue();
                    String Pdescription = (String) dataSnapshot.child("description").getValue();
                    String Paddress = (String) dataSnapshot.child("paddress").getValue();
                    String Pphone_number = (String) dataSnapshot.child("phone").getValue();
                    String Pimage = (String) dataSnapshot.child("image").getValue();
                    String Pcapacity = (String) dataSnapshot.child("wage_salary").getValue();
                    String Pcompatibility = (String) dataSnapshot.child("education").getValue();
                    String Pcompany_name = (String) dataSnapshot.child("company_name").getValue();


                    name.setText(Pname);
                    experience.setText(Pprice);
                    description.setText(Pdescription);
                    address.setText(Paddress);
                    phone_number.setText(Pphone_number);
                    wage_salary.setText(Pcapacity);
                    education.setText(Pcompatibility);
                    company_name.setText(Pcompany_name);
                    Picasso.get().load(Pimage).into(imageView);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    private void updateOnlyProductInfo() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products");

        final String name1 = name.getText().toString();
        final String wage_salary1 = wage_salary.getText().toString();
        final String education1 = education.getText().toString();
        final String experience1 = experience.getText().toString();
        final String company_name1 = company_name.getText().toString();
        final String description1 = description.getText().toString();
        final String address1 = address.getText().toString();
        final String phone_num = phone_number.getText().toString();

        if (TextUtils.isEmpty(name1)) {
            Toast.makeText(this, "Please, write down Service Name", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(wage_salary1)) {
            Toast.makeText(this, "Please, write down Wage/Salary", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(education1)) {
            Toast.makeText(this, "Please, write down Education", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(experience1)) {
            Toast.makeText(this, "Please, write down Experience", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(description1)) {
            Toast.makeText(this, "Please, write down Description", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(address1)) {
            Toast.makeText(this, "Please, write down Address", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(phone_num)) {
            Toast.makeText(this, "Please, write down Phone Number", Toast.LENGTH_SHORT).show();
        } else {

            HashMap<String, Object> userMap = new HashMap<>();
            userMap.put("pname", name.getText().toString());
            userMap.put("description", description.getText().toString());
            userMap.put("wage_salary", wage_salary.getText().toString());
            userMap.put("paddress", address.getText().toString());
            userMap.put("phone", phone_number.getText().toString());
            userMap.put("education", education.getText().toString());
            userMap.put("experience", experience.getText().toString());
            if(TextUtils.isEmpty(company_name1))
            {

            }
            else
            {
                userMap.put("company_name", company_name.getText().toString());
            }



            ref.child(productPID).updateChildren(userMap);

            startActivity(new Intent(EditWindTurbineInstallation.this, ShopActivity.class));
            Toast.makeText(EditWindTurbineInstallation.this, "Changes have been applied successfully!", Toast.LENGTH_SHORT).show();
            finish();

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();

            imageView.setImageURI(imageUri);
        } else {
            Toast.makeText(this, "Error, Try Again.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }



    private void ProductInfoSaved() {


        comp_name = company_name.getText().toString();

        if (name.equals("")) {
            Toast.makeText(this, "Please, write down Service Name", Toast.LENGTH_SHORT).show();
        } else if (wage_salary.equals("")) {
            Toast.makeText(this, "Please, write down Wage/Salary", Toast.LENGTH_SHORT).show();
        } else if (education.equals("")) {
            Toast.makeText(this, "Please, write down Education", Toast.LENGTH_SHORT).show();
        } else if (experience.equals("")) {
            Toast.makeText(this, "Please, write down Experience", Toast.LENGTH_SHORT).show();
        } else if (description.equals("")) {
            Toast.makeText(this, "Please, write down Description", Toast.LENGTH_SHORT).show();
        } else if (address.equals("")) {
            Toast.makeText(this, "Please, write down Address", Toast.LENGTH_SHORT).show();
        } else if (phone_number.equals("")) {
            Toast.makeText(this, "Please, write down Phone Number", Toast.LENGTH_SHORT).show();
        } else if (checker.equals("clicked")) {
            UploadImage();
        }


    }

    private void UploadImage() {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait, while we are updating your account information");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if (imageUri != null) {
            final StorageReference fileRef = storageProfilePrictureRef
                    .child(productPID + ".jpg");

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

                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Products").child(productPID);
                                HashMap<String, Object> ProductMap = new HashMap<>();
                                ProductMap.put("description", description.getText().toString());
                                ProductMap.put("wage_salary", wage_salary.getText().toString());
                                ProductMap.put("pname", name.getText().toString());
                                ProductMap.put("paddress", address.getText().toString());
                                ProductMap.put("phone", phone_number.getText().toString());
                                ProductMap.put("education", education.getText().toString());
                                ProductMap.put("experience", experience.getText().toString());

                                if (TextUtils.isEmpty(comp_name))
                                {

                                }
                                else

                                {
                                    ProductMap.put("company_name", company_name.getText().toString());
                                }
                                ProductMap.put("image", myUrl);
                                ref.updateChildren(ProductMap)


                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(EditWindTurbineInstallation.this, "Changes have been applied successfully", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(EditWindTurbineInstallation.this, ShopActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {

                                                    String message = task.getException().toString();
                                                    Toast.makeText(EditWindTurbineInstallation.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                        });
                            } else {
                                Toast.makeText(EditWindTurbineInstallation.this, "Image is not selected.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }



}