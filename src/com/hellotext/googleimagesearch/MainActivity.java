package com.hellotext.googleimagesearch;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.callback.AjaxStatus;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
    private final int MAX_RESULT_COUNT = 50;
    private final int MAX_CONCURRENT_REQUESTS = 4;
    private final int PAGE_SIZE = 8;
    private final int INSTANT_SEARCH_DELY = 300;
    private final String SEARCH_ENDPOINT = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=" + PAGE_SIZE;
    
    private ImageAdapter resultsImageAdapter;
    private GridView resultsGrid;
    public LruCache<String, Bitmap> imageCache;
    
    public MainActivity(){
        super();
    }

    protected void asyncJson(String url, final int start, final ImageAdapter searchResultsAdapter){
        
        Log.i("GoogleImageSearch", "About to send request for: " + url);
        
        // TODO: Set cache to be big enough to handle 50 images at a time
        // TODO: Figure out if and how that will fail if it exceeds max memory for the app
        //tutorial default cache size - 8th of allowed memory size
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;

        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };        
        
        final AQuery aq = new AQuery(findViewById(R.id.mainLayoutRoot));
        aq.ajax(url, JSONObject.class, new AjaxCallback<JSONObject>() {
            @Override
            public void callback(String url, JSONObject json, AjaxStatus status) {
                
                /*
                 * TODO: Extend AjaxCallback so this line written once and can be reused for other AJAX
                 * queries that should only continue if they are results for the "current" search
                 */ 
                if(!isAttached(searchResultsAdapter)) return;
                
                if(json != null){
                    try{
                        JSONObject responseData = json.getJSONObject("responseData");
                        JSONArray results = responseData.getJSONArray("results");
                        for(int i=0; i<results.length(); i++){
                            Log.i("GoogleImageSearch", "Got this image as a result: " + results.getJSONObject(i).getString("tbUrl"));
                            final ImageResult imageResult = new ImageResult();
                            imageResult.imgUrl = results.getJSONObject(i).getString("tbUrl");
                            imageResult.resultIndex = start + i;
                            
                            //Load the image for this result in background before adding to adaptor
                            Log.i("GoogleImageSearch", "About to request image and cache it");
                            aq.ajax(imageResult.imgUrl, Bitmap.class, new AjaxCallback<Bitmap>() {
                                @Override
                                public void callback(String url, Bitmap bitmap, AjaxStatus status) {
                                    
                                    if(!isAttached(searchResultsAdapter)) return;
                                    
                                    Log.i("GoogleImageSearch", "Received image data, adding to cache");
                                    
                                    imageCache.put(url, bitmap);
                                    resultsImageAdapter.addResult(imageResult);
                                    resultsGrid.invalidateViews();
                                }
                            });                            
                        }
                    }catch (JSONException e){
                        //TODO: handle errors
                    }
                }else{
                    //TODO: handle errors
                }
            }
        });
    }
    
    protected boolean isAttached(ImageAdapter adapter){
        return resultsGrid.getAdapter()==adapter;
    }
    
    protected void runSearch(String query){

        //Perform the search
        for(int i=0;i<MAX_RESULT_COUNT;i+=PAGE_SIZE){
            try {
                asyncJson(SEARCH_ENDPOINT + "&q=" + URLEncoder.encode(query, "UTF-8") + "&start=" + i, i, resultsImageAdapter);
            } catch (UnsupportedEncodingException e) {
                // TODO: handle error
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        final MainActivity self = this;

        //set the max number of concurrent network connections, default is 4
        AjaxCallback.setNetworkLimit(16);

        //set the max size of the memory cache, default is 1M pixels (4MB)
        BitmapAjaxCallback.setMaxPixelLimit(0);
        
        BitmapAjaxCallback.setCacheLimit(50);
        BitmapAjaxCallback.setIconCacheLimit(50);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsGrid = (GridView) findViewById(R.id.grid_view);

        final EditText searchInput = (EditText) findViewById(R.id.search_input);
        searchInput.addTextChangedListener(
            new DebouncedTextWatcher(1000) {
                @Override
                public void onDebouncedTextChanged(CharSequence s, int start, int before, int count) {
                    String query = searchInput.getText().toString();
                    //runSearch(query);
                    
                    //Trash the last query results if there was one
                    resultsImageAdapter = new ImageAdapter(self);
                    resultsGrid.setAdapter(resultsImageAdapter);
                    resultsGrid.invalidateViews();                    
                    
                    new SearchTask(self).execute(searchInput.getText().toString());
                }
            }
        );
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}

class SearchTask extends AsyncTask<String, Void, Void> {
    
    private MainActivity context;
    
    public SearchTask(MainActivity context){
        this.context = context;
    }
    
    protected Void doInBackground(String... args) {
        Log.i("GoogleImageSearch", "starting doInBackground");
        context.runSearch(args[0]);
        return null;
    }

    protected void onProgressUpdate(Integer... progress) {}
    protected void onPostExecute(Long result) {
        Log.i("GoogleImageSearch", "onPostExecute called");
    }
}

