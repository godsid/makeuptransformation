package com.webmanagement.startransform;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class Step1Activity extends Activity {

    AQuery aq;
    ArrayList<JSONObject> itemsArrayList;
    ItemsAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        GridView chooseGridView = (GridView) findViewById(R.id.chooseGridView);

        aq = new AQuery(getApplicationContext());
        itemsArrayList = new ArrayList<JSONObject>();
        itemAdapter = new ItemsAdapter(getApplicationContext(),itemsArrayList);
        chooseGridView.setAdapter(itemAdapter);

        chooseGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intentStep2 = new Intent(getApplicationContext(),Step2Activity.class);
                try {
                    intentStep2.putExtra("imageMask",itemsArrayList.get(position).getString("mask"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(intentStep2);
            }
        });

        aq.ajax("http://192.168.1.5/app/mtf/items.php", JSONObject.class,3600,new AjaxCallback<JSONObject>(){
            @Override
            public void callback(String url, JSONObject object, AjaxStatus status) {
                JSONArray objectArray;
                if(object!=null){
                    try {
                        objectArray = object.getJSONArray("items");
                        for(int i=0,j=objectArray.length();i<j;i++){
                            Log.d("d", objectArray.getJSONObject(i).getString("cover"));
                            itemsArrayList.add(objectArray.getJSONObject(i));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                itemAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.choose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
