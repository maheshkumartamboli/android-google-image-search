package com.hellotext.googleimagesearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
    
    private Context mContext;
    //public TreeMap<Integer, String> images = new TreeMap<String, String>();
    
    
    
    private ArrayList<ImageResult> resultsBuffer = new ArrayList();
    private ArrayList<ImageResult> results = new ArrayList();

    public ImageAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return results.size();
    }

    public Object getItem(int position) {
        return results.get(position);
    }

    public long getItemId(int position) {
        //not implemented
        return 0;
    }
    
    public void addResults(ArrayList<ImageResult> reuslts){
        resultsBuffer.addAll(results);
        ImageResult next;
        while((next=getNextBuffered())!=null){
            results.add(next);
        }
    }
    
    private ImageResult getNextBuffered(){
        ImageResult lastPopped = results.get(results.size()-1);
        
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        
        final ImageView imageView;
        
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(85, 85));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
            
        AQuery aq = new AQuery(imageView);
        
        for (Entry<String, String> entry : images.entrySet()) {
            
        }
        
        String imageUrl = "https://encrypted-tbn3.gstatic.com/images?q=tbn:ANd9GcS_-P9g3nyz3zJoho6jyDozyySPN61RMxhu68hOqT7mvRpG1xA024BcX0XI"; 
        
        aq.ajax(imageUrl, Bitmap.class, new AjaxCallback<Bitmap>() {

            @Override
            public void callback(String url, Bitmap bitmap, AjaxStatus status) {
                imageView.setImageBitmap(bitmap);
            }
        });
        
        return imageView;
    }
}