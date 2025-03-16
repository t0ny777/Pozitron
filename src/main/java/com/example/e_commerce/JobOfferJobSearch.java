package com.example.e_commerce;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class JobOfferJobSearch extends AppCompatActivity {

    private RelativeLayout l1,l2;
    private String CategoryName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_offer_job_search);

        CategoryName = getIntent().getExtras().get("category").toString();

        l1 = findViewById(R.id.job_offer);
        l2 = findViewById(R.id.job_search);

        l1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(JobOfferJobSearch.this, SolarPanelInstallation.class);
                intent.putExtra("category", CategoryName);
                startActivity(intent);

            }
        });

        l2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(JobOfferJobSearch.this, WindTurbineInstallation.class);
                intent.putExtra("category", CategoryName);
                startActivity(intent);
            }
        });

    }
}
