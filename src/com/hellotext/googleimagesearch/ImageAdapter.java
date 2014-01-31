package com.hellotext.googleimagesearch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.SimpleArrayMap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.androidquery.AQuery;
import com.androidquery.callback.*;

public class ImageAdapter extends BaseAdapter {
    
    private MainActivity context;
    private int contiguousResults = 0; //Contiguous results form front of results array
    private SimpleArrayMap<Integer, ImageResult> results = new SimpleArrayMap<Integer, ImageResult>();

    public ImageAdapter(MainActivity context) {
        this.context = context;
    }

    public int getCount() {
        return contiguousResults;
    }

    public Object getItem(int position) {
        return results.valueAt(position);
    }

    public long getItemId(int position) {
        //not implemented
        return 0;
    }
    
    public void addResult(ImageResult result){
       this.results.put(result.resultIndex, result);
       
       //recalculate contiguous result count
       int i = contiguousResults;
       while(results.get(i) != null){
           i++;
       }
       
       if(i!=contiguousResults){
           contiguousResults = i;
           notifyDataSetChanged();
       }
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageView imageView;
        
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
            imageView.setImageResource(android.R.color.transparent);
        }
        
        String imageUrl = results.get(position).imgUrl;
        imageView.setImageBitmap(context.imageCache.get(imageUrl));
                
        return imageView;
    }
}