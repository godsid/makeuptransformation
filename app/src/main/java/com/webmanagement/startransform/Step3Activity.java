package com.webmanagement.startransform;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.io.File;
import java.util.List;


public class Step3Activity extends Activity {

    AQuery aq;
    String imageMask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        ImageButton btFBShare = (ImageButton)findViewById(R.id.btFBShare);
        ImageButton btTWShare = (ImageButton)findViewById(R.id.btTWShare);
        aq = new AQuery(getApplicationContext());
        //Bundle bundle = getIntent().getExtras();
        //imageMask = bundle.getString("imageMask");

        aq.id(imageView).image(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"tui.png"),0);


        //aq.id(imageView)
                //.image(imageMask, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK);


        btFBShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                

                shareFacebook("tui.png");
            }
        });

        btTWShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTwitter(imageMask);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.step3, menu);
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

    private void shareFacebook(String imageMask){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Coming soon ON Android");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "eample");
        shareIntent.putExtra(Intent.EXTRA_TITLE, "example");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "example");


        File path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);
        File file = new File(path, imageMask);
        Uri uri = Uri.fromFile(file);

        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        Log.d("tui",uri.toString());

        startActivity(Intent.createChooser(shareIntent, "Share"));

        /*


        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);

        for (final ResolveInfo app : activityList){
            Log.d("tui", app.activityInfo.name);
            if ((app.activityInfo.name).contains("facebook.composer")){
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivity(shareIntent);
                break;
            }
        }*/
    }
    private void shareTwitter(String imageMask){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Coming soon On Android");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "eample");
        shareIntent.putExtra(Intent.EXTRA_TITLE, "example");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "example");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageMask);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList){
            Log.d("tui", app.activityInfo.name);
            if ((app.activityInfo.name).contains("twitter")){
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivity(shareIntent);
                break;
            }
        }
    }
}
