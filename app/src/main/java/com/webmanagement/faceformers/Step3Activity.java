package com.webmanagement.faceformers;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.androidquery.AQuery;

import java.io.File;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class Step3Activity extends Activity {

    AQuery aq;
    String outputName;
    String imageTitle;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        ImageButton btFBShare = (ImageButton)findViewById(R.id.btFBShare);
        ImageButton btTWShare = (ImageButton)findViewById(R.id.btTWShare);
        aq = new AQuery(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        outputName = bundle.getString("outputName");
        imageTitle = bundle.getString("imageTitle");

        Bitmap outputBitmap = Image.decodeFile(new File(path,outputName));

        imageView.setMaxWidth(outputBitmap.getWidth());
        imageView.setMaxHeight(outputBitmap.getHeight());

        //imageView.setImageBitmap(outputBitmap);
        aq.id(imageView).image(new File(path,outputName),0);

        btFBShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareFacebook();
            }
        });

        btTWShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareTwitter();
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
        if (id == R.id.action_share) {
            shareMore();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareFacebook(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TITLE, imageTitle);

        File file = new File(path, outputName);
        Uri uri = Uri.fromFile(file);

        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);
        Log.d("tui",uri.toString());

        //startActivity(Intent.createChooser(shareIntent, "Share"));

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
        }
    }
    private void shareTwitter(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");

        File file = new File(path, outputName);
        Uri uri = Uri.fromFile(file);

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TITLE, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList){
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

    private void shareMore(){
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TEXT, imageTitle);
        shareIntent.putExtra(Intent.EXTRA_TITLE, imageTitle);

        File file = new File(path, outputName);
        Uri uri = Uri.fromFile(file);

        shareIntent.putExtra(Intent.EXTRA_STREAM,uri);


        startActivity(Intent.createChooser(shareIntent, "Share"));
    }
}
