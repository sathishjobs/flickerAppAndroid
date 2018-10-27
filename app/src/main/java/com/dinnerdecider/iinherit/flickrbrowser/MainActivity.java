package com.dinnerdecider.iinherit.flickrbrowser;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements GetFlickrjsonData.OnDataAvailable,RecyclerItemClickListener.OnRecyclerClickListener{
    private static final String TAG = "MainActivity";
    private  FlickrRecyclerViewAdapter mFlickrRecyclerViewAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: Starts");
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);

        activateToolbar(true);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this,recyclerView,this));

        mFlickrRecyclerViewAdapter = new FlickrRecyclerViewAdapter(this, new ArrayList<Photo>());
        recyclerView.setAdapter(mFlickrRecyclerViewAdapter);
//        GetRawData getRawData = new GetRawData(this);
//        getRawData.execute("https://api.flickr.com/services/feeds/photos_public.gne?tags=android,nouget,sdk&tagmode=any&format=json&nojsoncallback=1");

        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    protected void onResume() {
        Log.d(TAG,"onResume starts");
        super.onResume();

        //check if sharedpreference search query is find.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String queryResult = sharedPreferences.getString(FLICKR_QUERY,"");

        if(queryResult.length() > 0){
        GetFlickrjsonData getFlickrjsonData = new GetFlickrjsonData("https://api.flickr.com/services/feeds/photos_public.gne","en-us",true,this);
//        getFlickrjsonData.executeOnSameThread("android,nougat");
        getFlickrjsonData.execute(queryResult);
        } else {
            GetFlickrjsonData getFlickrjsonData = new GetFlickrjsonData("https://api.flickr.com/services/feeds/photos_public.gne","en-us",true,this);
//        getFlickrjsonData.executeOnSameThread("android,nougat");
            getFlickrjsonData.execute(queryResult);
        }

        Log.d(TAG,"onResume ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d(TAG, "onCreateOptionsMenu() returned: " + true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        if(id == R.id.action_search){
            Intent intent = new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDataAvailable(List<Photo> data, DownloadStatus status){
        if(status == DownloadStatus.OK){
            Log.d(TAG,"onDataAvailable: data is" + data);
            mFlickrRecyclerViewAdapter.loadNewData(data);
        } else {
            // download or processing failed
            Log.e(TAG, "onDataAvailable failed with status" + status);
        }
        Log.d(TAG,"onDataAvailable : ends");
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG,"onItemClick:starts");
        Toast.makeText(MainActivity.this,"Normal tap at position" + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG,"onItemLongClick: starts");
//        Toast.makeText(MainActivity.this,"Long tap at position" + position, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this,PhotoDetailActivity.class);
        intent.putExtra(PHOTO_TRANSFER,mFlickrRecyclerViewAdapter.getPhoto(position));
        startActivity(intent);
    }
}
