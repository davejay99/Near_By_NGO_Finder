package com.example.demo1;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.demo1.DataClass.NgoDetails;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class galleryAdapt extends BaseAdapter {
    private Context context;
    private ArrayList<String> imageArray;

    public galleryAdapt(Context context, ArrayList<String> imageArray) {
        this.context = context;
        this.imageArray = imageArray;
    }

    @Override
    public int getCount() {

        return imageArray.size();
    }

    @Override
    public Object getItem(int position) {
        return imageArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String imgurl = imageArray.get(position);
        ImageView imageView = new ImageView(context);
        Picasso.with(context)
                .load(imgurl)
                .placeholder(R.drawable.image)
                .fit()
                .centerCrop()
                .into(imageView);
        imageView.setLayoutParams(new GridView.LayoutParams(340,350));
        return imageView;
    }
}
