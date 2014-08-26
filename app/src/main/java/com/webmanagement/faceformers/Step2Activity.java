package com.webmanagement.faceformers;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import static android.os.Environment.*;


public class Step2Activity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.ShutterCallback{
    MyGoogleAnalytics googleAnalytics;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    String outputName ;
    AQuery aq;
    String imageMask;
    String imageTitle;
    ImageView makerImageView;
    Camera mCamera;
    boolean saveState = false;
    SurfaceView cameraImageView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2);
        makerImageView = (ImageView)findViewById(R.id.makerImageView);
        cameraImageView = (SurfaceView)findViewById(R.id.cameraImageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        //cameraImageView.setMinimumHeight(makerImageView.getHeight()/2);
        //cameraImageView.setMinimumWidth(makerImageView.getWidth() / 2);

        //setLayoutParams(new LayoutParams(width,height));

        //android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(width, height);

        googleAnalytics = new MyGoogleAnalytics(this);
        googleAnalytics.trackPage("take photo");

        outputName = "faceformers_"+DateTime.getCurrentTimeStamp("yyyy-MM-dd_HHmmss")+".jpg";
        Log.d("tui",outputName);
        //outputName = "faceformers_.jpg";
        Bundle bundle = getIntent().getExtras();
        imageMask = bundle.getString("imageMask");
        imageTitle = bundle.getString("imageTitle");
        aq = new AQuery(getApplicationContext());

        aq.id(makerImageView)
                .progress(R.id.progressBar)
                .image(imageMask, true, true, 0, 0, null, AQuery.FADE_IN_NETWORK, AQuery.RATIO_PRESERVE);

        cameraImageView.getHolder().addCallback(this);
        cameraImageView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (!saveState) {
                    saveState = true;
                    mCamera.takePicture(Step2Activity.this, null, null, Step2Activity.this);
                }
            }
        });
        /*
        makerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCameraActivity();
            }
        });
        */
        //Google Analytic Tracking
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.step2, menu);
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
            //Bitmap cover =  Image.decodeFile(new File(path, outputName));
            Bitmap rotatedBitmap = Image.decodeFile(new File(path, outputName));
            Matrix matrix = new Matrix();
            matrix.preScale(1,-1);
            matrix.postRotate(270);
            Bitmap cover = Bitmap.createBitmap(rotatedBitmap, 0, 0, rotatedBitmap.getWidth(), rotatedBitmap.getHeight(), matrix, true);


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

    public void onResume() {
        Log.d("System","onResume");
        super.onResume();
        Log.d("tui",String.valueOf(Camera.getNumberOfCameras()));
        if(Camera.getNumberOfCameras()==1){
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        }else{
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }

        mCamera.setDisplayOrientation(90);
    }

    public void onPause() {
        Log.d("System","onPause");
        super.onPause();
        mCamera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) cameraImageView.getLayoutParams();
        layout.setMargins(makerImageView.getWidth()/2,makerImageView.getWidth()/2,0,0);
        layout.height = layout.width = makerImageView.getWidth()/2;
        cameraImageView.setLayoutParams(layout);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();

        params.setPictureSize(pictureSize.get(0).width,pictureSize.get(0).height);
        params.setPreviewSize(previewSize.get(0).width,previewSize.get(0).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        try {
            mCamera.setPreviewDisplay(cameraImageView.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        File file = new File(outputName);
        Uri outputFileUri = Uri.fromFile( file );

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, outputFileUri);
        //File imagesFolder = new File(path, "CameraSnap");
        //imagesFolder.mkdirs();
        File output = new File(path, outputName);

        //while (output.exists()){
        //    output = new File(path, outputName);
        //}

        Uri uri = Uri.fromFile(output);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);

        ContentValues image = new ContentValues();
        String dateTaken = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        image.put(MediaStore.Images.Media.TITLE, output.toString());
        image.put(MediaStore.Images.Media.DISPLAY_NAME, output.toString());
        image.put(MediaStore.Images.Media.DATE_ADDED, dateTaken);
        image.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        image.put(MediaStore.Images.Media.DATE_MODIFIED, dateTaken);
        image.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        image.put(MediaStore.Images.Media.ORIENTATION, 0);
        String path =  output.getParentFile().toString().toLowerCase();
        String name =  output.getParentFile().getName().toLowerCase();
        image.put(MediaStore.Images.ImageColumns.BUCKET_ID, path.hashCode());
        image.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, name);
        image.put(MediaStore.Images.Media.SIZE, output.length());
        image.put(MediaStore.Images.Media.DATA, output.getAbsolutePath());

        OutputStream os;

        try {
            os = getContentResolver().openOutputStream(uri);
            os.write(data);
            os.flush();
            os.close();
            Toast.makeText(Step2Activity.this, "ถ่ายรูปเสร็จแล้วค่ะ", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
        } catch (IOException e) { }

        Log.d("Camera","Restart Preview");
        mCamera.stopPreview();
        mergeImage();
        //mCamera.startPreview();
        saveState = false;
    }

    @Override
    public void onShutter() {

    }
}
