package com.example.demo1;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

public class ImagePreview extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String url = intent.getStringExtra("Url");
        setContentView(R.layout.imagepreview);
        ImageView preview = findViewById(R.id.preview);
        Picasso.with(ImagePreview.this)
                .load(url)
                .placeholder(R.drawable.image)
                .fit()
                .centerCrop()
                .into(preview);
    }
}
