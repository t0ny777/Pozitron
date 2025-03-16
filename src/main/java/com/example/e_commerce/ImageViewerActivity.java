package com.example.e_commerce;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ImageViewerActivity extends AppCompatActivity {

    private ImageView imageView;
    private String ImageUrl, image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        imageView = findViewById(R.id.view_image);
        ImageUrl = getIntent().getStringExtra("url");
        image = getIntent().getStringExtra("image");

        Picasso.get().load(ImageUrl).into(imageView);
        Picasso.get().load(image).into(imageView);

    }
}
