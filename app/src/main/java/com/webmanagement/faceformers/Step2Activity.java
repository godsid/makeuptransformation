package com.webmanagement.faceformers;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import com.androidquery.AQuery;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static android.os.Environment.*;


public class Step2Activity extends Activity {
    MyGoogleAnalytics googleAnalytics;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    String outputName ;
    AQuery aq;;
    String imageMask;
    String imageTitle;
    ImageView makerImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        makerImageView = (ImageView)findViewById(R.id.makerImageView);

        googleAnalytics = new MyGoogleAnalytics(this);
        googleAnalytics.trackPage("take photo");

        outputName = "faceformers_"+DateTime.getCurrentTimeStamp("yyyy-MM-dd_HHmmss")+".jpg";
        Log.d("tui",outputName);
        outputName = "faceformers_.jpg";
        Bundle bundle = getIntent().getExtras();
        imageMask = bundle.getString("imageMask");
        imageTitle = bundle.getString("imageTitle");
        aq = new AQuery(getApplicationContext());

        aq.id(makerImageView)
                .image(imageMask, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK);

        makerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
        //Google Analytic Tracking
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.step2, menu);
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

    protected void startCameraActivity() {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile( new File(path,outputName)));
        startActivityForResult(intentCamera, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==-1){
            mergeImage();
        }else{
            Toast.makeText(getApplicationContext(),"Cancelled",Toast.LENGTH_LONG);
        }
        Log.d("tui",String.valueOf(resultCode));
    }

    private void mergeImage(){
        try {
            OutputStream os = null;
            Bitmap maker = ((BitmapDrawable)makerImageView.getDrawable()).getBitmap();
            Bitmap cover =  Image.decodeFile(new File(path, outputName));
            int width = maker.getWidth()/2;
            int height = maker.getHeight()/2;
            cover =  Image.scaleCropToFit(cover,width,height);
            Bitmap cs = Bitmap.createBitmap(maker.getWidth(), maker.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);

            comboImage.drawBitmap(maker,new Matrix(),null);
            comboImage.drawBitmap(cover,(maker.getWidth()/2),(maker.getHeight()/2),null);
            //Draw logo

            Bitmap waterMask = BitmapFactory.decodeResource(getResources(),R.drawable.watermask);
            waterMask = Bitmap.createScaledBitmap(waterMask
                        ,waterMask.getWidth()/2,waterMask.getHeight()/2,false);
            Log.d("tui",String.valueOf(width));

            comboImage.drawBitmap(waterMask,5,(maker.getHeight()- waterMask.getHeight()),null);

            os = new FileOutputStream(new File(path,outputName));
            cs.compress(Bitmap.CompressFormat.JPEG, 80, os);

            //makerImageView.setImageURI(Uri.fromFile(new File(path,outputName)));
            Intent intentStep3 = new Intent(getApplicationContext(),Step3Activity.class);
            intentStep3.putExtra("outputName",outputName);
            intentStep3.putExtra("imageTitle",imageTitle);
            startActivity(intentStep3);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
