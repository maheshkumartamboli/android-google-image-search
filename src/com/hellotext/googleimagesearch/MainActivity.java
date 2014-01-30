package com.hellotext.googleimagesearch;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        //set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(16);
        
        //set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(0);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        final EditText searchInput = (EditText) findViewById(R.id.search_input);
        
        //searchInput.setOn

        searchInput.addTextChangedListener(
            new DebouncedTextWatcher(1000) {
                @Override
                public void onDebouncedTextChanged(CharSequence s, int start, int before, int count) {
                    Log.i("GoogleImageSearch", "debounced text changed");
                }
            }
        );
        
    }
    
    public void buildUI(View view){
        GridView gridview = (GridView) findViewById(R.id.grid_view);
        gridview.setAdapter(new ImageAdapter(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

