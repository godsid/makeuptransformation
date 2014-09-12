package com.webmanagement.faceformers;

import android.app.*;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;



public class Step1Activity extends Activity {
    public MyGoogleAnalytics googleAnalytics;
    AQuery aq;
    ArrayList<JSONObject> itemsArrayList;
    ItemsAdapter itemAdapter;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step1);
        GridView chooseGridView = (GridView) findViewById(R.id.chooseGridView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        //Google Analytic Tracking
        googleAnalytics = new MyGoogleAnalytics(this);
        googleAnalytics.trackPage("choose photo");

        aq = new AQuery(getApplicationContext());
        itemsArrayList = new ArrayList<JSONObject>();
        itemAdapter = new ItemsAdapter(getApplicationContext(),itemsArrayList);
        chooseGridView.setAdapter(itemAdapter);

        chooseGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final int clickPosition = position;
                final Dialog dialog = new Dialog(Step1Activity.this);
                dialog.setContentView(R.layout.choose_dialog);
                dialog.setTitle(R.string.choose_dialog_title);
                dialog.show();

                ImageView oneThree = (ImageView)dialog.findViewById(R.id.oneThree);
                ImageView threeOne = (ImageView)dialog.findViewById(R.id.threeOne);

                oneThree.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intentStep2 = new Intent(getApplicationContext(),Step2_1Activity.class);
                            intentStep2.putExtra("imageMask",itemsArrayList.get(clickPosition).getString("mask"));
                            intentStep2.putExtra("imageCover",itemsArrayList.get(clickPosition).getString("cover"));
                            intentStep2.putExtra("imageTitle",itemsArrayList.get(clickPosition).getString("title"));
                            startActivity(intentStep2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });

                threeOne.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intentStep2 = new Intent(getApplicationContext(),Step2Activity.class);
                            intentStep2.putExtra("imageMask",itemsArrayList.get(clickPosition).getString("mask"));
                            intentStep2.putExtra("imageCover",itemsArrayList.get(clickPosition).getString("cover"));
                            intentStep2.putExtra("imageTitle",itemsArrayList.get(clickPosition).getString("title"));
                            startActivity(intentStep2);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        aq.progress(progressBar)
                .ajax("http://review.edtguide.com/ftm/items.php", JSONObject.class,3600,new AjaxCallback<JSONObject>(){
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

    private void loaditemList(){
        aq.progress(progressBar)
            .ajax("http://review.edtguide.com/ftm/items.php", JSONObject.class, 0, new AjaxCallback<JSONObject>() {
                @Override
                public void callback(String url, JSONObject object, AjaxStatus status) {
                    JSONArray objectArray;
                    if (object != null) {
                        try {
                            objectArray = object.getJSONArray("items");
                            for (int i = 0, j = objectArray.length(); i < j; i++) {
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
}
