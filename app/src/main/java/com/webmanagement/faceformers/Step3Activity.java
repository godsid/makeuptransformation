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
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.io.File;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class Step3Activity extends Activity {
    public InterstitialAd interstitialAds;
    public AdRequest adRequest;
    MyGoogleAnalytics googleAnalytics;
    static final int SHARE_REQUEST_CODE = 1;
    AQuery aq;
    String outputName;
    String imageTitle;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step3);
        ImageView imageView = (ImageView)findViewById(R.id.oneThree);
        ImageView btFBShare = (ImageView)findViewById(R.id.btFBShare);

        googleAnalytics = new MyGoogleAnalytics(this);
        googleAnalytics.trackPage("take photo");

        aq = new AQuery(getApplicationContext());
        Bundle bundle = getIntent().getExtras();
        outputName = bundle.getString("outputName");
        imageTitle = bundle.getString("imageTitle");

        Bitmap outputBitmap = Image.decodeFile(new File(path,outputName));

        imageView.setMaxWidth(outputBitmap.getWidth());
        imageView.setMaxHeight(outputBitmap.getHeight());

        //imageView.setImageBitmap(outputBitmap);
        aq.id(imageView).image(new File(path,outputName),0);
        requestAds();
        btFBShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               shareFacebook();
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

        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        Boolean shared = false;
        for (final ResolveInfo app : activityList){
            Log.d("tui", app.activityInfo.name);
            if ((app.activityInfo.name).contains("com.facebook.katana")){
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivityForResult(shareIntent, SHARE_REQUEST_CODE);
                shared = true;
                break;
            }
        }
        if(shared==false){
            startActivityForResult(Intent.createChooser(shareIntent, "Share to other App"), SHARE_REQUEST_CODE);
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
        Boolean isFacebook = false;
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList){
            if ((app.activityInfo.name).contains("twitter")){
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                startActivityForResult(shareIntent, SHARE_REQUEST_CODE);
                isFacebook = true;
                break;
            }
        }
        if(!isFacebook){
            shareMore();
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
        startActivityForResult(Intent.createChooser(shareIntent, "Share"), SHARE_REQUEST_CODE);
    }
    private void requestAds(){
        /*******************************************************/
        //Create the interstitial Ads.
        interstitialAds = new InterstitialAd(this);
        interstitialAds.setAdUnitId(Config.adsInterstitialUnitIDFullPage);
        interstitialAds.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                //super.onAdLoaded();
                Log.d("tui","interstitial Ads Loaded");
            }
            @Override
            public void onAdClosed() {
                Log.d("tui","interstitial Ads Close");
                interstitialAds.loadAd(adRequest);
                //super.onAdClosed();
            }
        });
        // Create ad request.
        adRequest = new AdRequest.Builder()
                .addKeyword(Config.adsInterstitialKeyword)
                .build();
        // Begin loading your interstitial.
        interstitialAds.loadAd(adRequest);

        /*******************************************************/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SHARE_REQUEST_CODE){
            if(interstitialAds.isLoaded()) {
                interstitialAds.show();
            }
        }

    }


}
