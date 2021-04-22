package com.example.adcureapplication;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CollectionAdapter extends PagerAdapter {
private Context context;
private LayoutInflater layoutInflater;
private Integer[] images={R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3};

    public CollectionAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
       layoutInflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       View view=layoutInflater.inflate(R.layout.custom_layout,null);
        ImageView imageView=(ImageView)view.findViewById(R.id.imageView);
        imageView.setImageResource(images[position]);

        //ViewPager vp=(ViewPager)container;

        container.addView(view);
                return view;
    }


    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
      //  ViewPager vp=(ViewPager)container;
      //  View view=(View)object;
       container.removeView((LinearLayout)object);
       // super.destroyItem(container, position, object);
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(LinearLayout)object;
    }
}
