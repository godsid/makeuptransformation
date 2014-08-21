package com.webmanagement.startransform;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.*;


public class Step2Activity extends Activity {
    BitmapFactory.Options options;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    AQuery aq;
    Uri takePhotoFile,outputFile;
    String imageMask;

    File coverFile;
    Bitmap preset;
    ImageView makerImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        makerImageView = (ImageView)findViewById(R.id.makerImageView);
        ImageView cameraImageView = (ImageView)findViewById(R.id.cameraImageView);

        Bundle bundle = getIntent().getExtras();
        imageMask = bundle.getString("imageMask");
        aq = new AQuery(getApplicationContext());

        preset = aq.getCachedImage(imageMask);


        /*aq.download(imageMask,new File(path,takePhotoFile.toString()),new AjaxCallback<File>(){
            @Override
            public void callback(String url, File object, AjaxStatus status) {
                coverFile = object;
            }
        });
        */

        aq.id(makerImageView)
                .image(imageMask,true,true,0,0,null,AQuery.FADE_IN_NETWORK);

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
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
    // decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f) {
        Bitmap result = null;
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 150;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            result = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            return result;
        } catch (FileNotFoundException e) {
            //Log.e(TAG, "ERROR", e);
        }
        return result;
    }

    protected void startCameraActivity() {
        String picTime = String.valueOf(new Date().getTime());
        File takePhotoFile = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"xxxx.jpg");


        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile( takePhotoFile ) );
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
        //super.onActivityResult(requestCode, resultCode, data);
    }

    private void mergeImage(){
       // Log.d("tui",takePhotoFile.toString());
        Bitmap maker = decodeFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS),"1.png"));

        Bitmap cover = decodeFile(new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"xxxx.jpg"));

        Log.d("tui",String.valueOf(maker.getWidth()));
        Bitmap cs = Bitmap.createBitmap(maker.getWidth(), maker.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas comboImage = new Canvas(cs);

        comboImage.drawBitmap(maker,new Matrix(),null);
        comboImage.drawBitmap(cover,125,125,null);
        //comboImage.drawBitmap(cover, new Rect(250,250,250,250),new Rect(0,0,250,250),null);

        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES),"tui.png"));
            cs.compress(Bitmap.CompressFormat.PNG, 50, os);
        } catch(IOException e) {
            Log.v("error saving", "error saving");
            e.printStackTrace();
        }

        makerImageView.setImageBitmap(cs);


        Intent intentStep3 = new Intent(getApplicationContext(),Step3Activity.class);
        //intentStep3.putExtra("imageoutput",)
        startActivity(intentStep3);

    }
}
