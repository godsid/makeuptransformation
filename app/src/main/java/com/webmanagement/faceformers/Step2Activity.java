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
import android.util.DisplayMetrics;
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
    BitmapFactory.Options options;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    String outputName = "faceformers.jpg";
    AQuery aq;;
    String imageMask;
    String imageTitle;
    Bitmap bitmapCover;
    File coverFile;
    Bitmap preset;
    ImageView makerImageView;
    DisplayMetrics displayScreen = new DisplayMetrics();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        makerImageView = (ImageView)findViewById(R.id.makerImageView);
        ImageView cameraImageView = (ImageView)findViewById(R.id.cameraImageView);

        Bundle bundle = getIntent().getExtras();
        imageMask = bundle.getString("imageMask");
        imageTitle = bundle.getString("imageTitle");
        aq = new AQuery(getApplicationContext());


        /*aq.download(imageMask,new File(path,imageMask),new AjaxCallback<File>(){
            @Override
            public void callback(String url, File object, AjaxStatus status) {
                coverFile = object;
            }
        });*/

        aq.id(makerImageView)
                .image(imageMask, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK);

        makerImageView.setOnClickListener(new View.OnClickListener() {
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

    protected void startCameraActivity() {
        //String picTime = String.valueOf(new Date().getTime());
        //File takePhotoFile = new File(Environment.getExternalStoragePublicDirectory(
        //        Environment.DIRECTORY_PICTURES),"xxxx.jpg");
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
            Bitmap maker = ((BitmapDrawable)makerImageView.getDrawable()).getBitmap();
            //Bitmap maker = Image.decodeFile(new File(Environment.getExternalStoragePublicDirectory(
            //        Environment.DIRECTORY_DOWNLOADS),"1.png"));
            Bitmap cover =  Image.decodeFile(new File(path, outputName));

            int width = maker.getWidth()/2;
            int height = maker.getHeight()/2;


            cover =  Image.scaleCropToFit(cover,width,height);
            //cover = Bitmap.createScaledBitmap(cover,width,height,false);

            //Log.d("tui",String.valueOf(maker.getWidth()));
            Bitmap cs = Bitmap.createBitmap(maker.getWidth(), maker.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas comboImage = new Canvas(cs);

            comboImage.drawBitmap(maker,new Matrix(),null);
            comboImage.drawBitmap(cover,(maker.getWidth()/2),(maker.getHeight()/2),null);

            //comboImage.drawBitmap(cover, new Rect(250,250,250,250),new Rect(0,0,250,250),null);

            OutputStream os = null;

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
