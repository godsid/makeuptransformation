package com.webmanagement.faceformers;

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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;


public class Step2_1Activity extends Activity implements SurfaceHolder.Callback, Camera.PictureCallback, Camera.ShutterCallback{
    final public static int TOPLEFT = 1;
    final public static int TOPRIGHT = 2;
    final public static int BOTTOMLEFT = 3;
    final public static String imageCoverName = "cover.png";
    final public static String takePictureName = "take.png";

    MyGoogleAnalytics googleAnalytics;
    File path = getExternalStoragePublicDirectory(
            DIRECTORY_PICTURES);
    String outputName ;
    AQuery aq;
    String imageMaskUrl,imageCoverUrl;
    String imageTitle;
    ImageView makerImageView;
    Camera mCamera;
    int actionState = TOPLEFT;
    SurfaceView cameraImageView;
    ProgressBar progressBar;
    int loadDataStatus = 0;
    Canvas comboImage;
    Bitmap cs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tui","onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step2_1);
        makerImageView = (ImageView)findViewById(R.id.makerImageView);
        cameraImageView = (SurfaceView)findViewById(R.id.cameraImageView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        actionState = TOPLEFT;
        outputName = "faceformers_"+DateTime.getCurrentTimeStamp("yyyy-MM-dd_HHmmss")+".jpg";

        //cameraImageView.setMinimumHeight(makerImageView.getHeight()/2);
        //cameraImageView.setMinimumWidth(makerImageView.getWidth() / 2);
        //setLayoutParams(new LayoutParams(width,height));
        //android.widget.FrameLayout.LayoutParams params = new android.widget.FrameLayout.LayoutParams(width, height);

        googleAnalytics = new MyGoogleAnalytics(this);
        googleAnalytics.trackPage("take photo 3 to 1");

        Bundle bundle = getIntent().getExtras();
        imageMaskUrl = bundle.getString("imageMask");
        imageCoverUrl = bundle.getString("imageCover");
        imageTitle = bundle.getString("imageTitle");
        aq = new AQuery(getApplicationContext());

        aq.download(imageCoverUrl,new File(path, imageCoverName),new AjaxCallback<File>(){
            @Override
            public void callback(String url, File object, AjaxStatus status) {
                super.callback(url, object, status);
                    mergeCover();
                    progressBar.setVisibility(View.INVISIBLE);
                    cameraImageView.setVisibility(View.VISIBLE);
                //aq.id(makerImageView)
                //   .image(new File(path,outputName).getPath());
            }
        });
        cameraImageView.getHolder().addCallback(this);
        cameraImageView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        cameraImageView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mCamera.takePicture(Step2_1Activity.this,null,null,Step2_1Activity.this);
            }
        });
        //Google Analytic Tracking
    }

    public void mergeCover(){
        //OutputStream os = null;
        Bitmap cover = aq.getCachedImage(R.drawable.step2_1_cover);
        int size = (int)Math.ceil(cover.getWidth()/2);
        cs = Bitmap.createBitmap(cover.getWidth(), cover.getHeight(), Bitmap.Config.ARGB_8888);
        comboImage = new Canvas(cs);
        comboImage.drawBitmap(cover,0,0,null);
        Bitmap item = aq.getCachedImage(new File(path,imageCoverName).getPath());
        item = Image.scaleCropToFit(item,size,size);

        comboImage.drawBitmap(item,size,size,null);
        aq.id(makerImageView)
                .image(cs,AQuery.RATIO_PRESERVE);

        /*
        try {
            os = new FileOutputStream(new File(path, outputName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        cs.compress(Bitmap.CompressFormat.JPEG, 100, os);
        */
    }
    public void onResume() {
        Log.d("tui","onResume");
        super.onResume();

        if (Camera.getNumberOfCameras() == 1) {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
        } else {
            mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        }
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(cameraImageView.getHolder());
            mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onPause() {
        Log.d("tui","onPause");
        super.onPause();
        mCamera.release();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("tui","surfaceCreated");
        setCameraView();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d("tui","surfaceChanged");
        Camera.Parameters params = mCamera.getParameters();
        List<Camera.Size> previewSize = params.getSupportedPreviewSizes();
        List<Camera.Size> pictureSize = params.getSupportedPictureSizes();

        params.setPictureSize(pictureSize.get(0).width,pictureSize.get(0).height);
        params.setPreviewSize(previewSize.get(0).width,previewSize.get(0).height);
        params.setJpegQuality(100);
        mCamera.setParameters(params);
        try {
            mCamera.setPreviewDisplay(cameraImageView.getHolder());
            //mCamera.startPreview();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d("tui","surfaceDestroyed");
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.d("tui","onPictureTaken");


        File file = new File(path,takePictureName);
        Uri outputFileUri = Uri.fromFile( file );

        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, outputFileUri);
        //File imagesFolder = new File(path, "CameraSnap");
        //imagesFolder.mkdirs();
        File output = new File(path, takePictureName);

        Uri uri = Uri.fromFile(output);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        //Uri uri = Uri.fromFile(output);
        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

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
            os = getContentResolver().openOutputStream(outputFileUri);
            os.write(data);
            os.flush();
            os.close();
            Toast.makeText(this, "ถ่ายรูปเสร็จแล้วค่ะ", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
        } catch (IOException e) { }

        Log.d("Camera","Restart Preview");
        //mCamera.stopPreview();
        mergeImage();

        if(actionState<BOTTOMLEFT){
            actionState++;
            setCameraView();
        }else {
            finishStep();
        }
    }

    @Override
    public void onShutter() {
        Log.d("tui","onShutter");
    }
    private void mergeImage(){
        Log.d("tui","mergeImage");
        try {
            OutputStream os = null;
            //Bitmap maker = ((BitmapDrawable)makerImageView.getDrawable()).getBitmap();
            //Bitmap outputBitmap = Image.decodeFile(new File(path, outputName));
            int size = (int)Math.ceil(cs.getWidth()/2);
            Matrix matrix = new Matrix();
            matrix.preScale(1,-1);
            matrix.postRotate(270);
            //cs = Bitmap.createBitmap(outputBitmap, 0, 0, outputBitmap.getWidth(), outputBitmap.getHeight(), matrix, true);
            Canvas comboImage = new Canvas(cs);
            //comboImage.drawBitmap(cs,0,0,null);
            Bitmap take = Image.decodeFile(new File(path, takePictureName));
            take = Image.scaleCropToFit(take,size,size);
            take = Bitmap.createBitmap(take, 0, 0, take.getWidth(), take.getHeight(), matrix, true);

            //int width = maker.getWidth()/2;
            //int height = maker.getHeight()/2;
            //cover =  Image.scaleCropToFit(cover,width,height);
            //Bitmap cs = Bitmap.createBitmap(maker.getWidth(), maker.getHeight(), Bitmap.Config.ARGB_8888);
           // Canvas comboImage = new Canvas(cs);

            //comboImage.drawBitmap(maker,new Matrix(),null);
            if(actionState == TOPLEFT){
                comboImage.drawBitmap(take,(0),0,null);
            }else if(actionState == TOPRIGHT){
                comboImage.drawBitmap(take,size,0,null);
            }else if(actionState == BOTTOMLEFT){
                comboImage.drawBitmap(take,0,size,null);
            }
            aq.id(makerImageView)
                    .image(cs,AQuery.RATIO_PRESERVE);
            //makerImageView.setImageBitmap(cs);

            if(actionState == BOTTOMLEFT) {
                //Draw logo
                Bitmap waterMask = BitmapFactory.decodeResource(getResources(), R.drawable.watermask);
                waterMask = Bitmap.createScaledBitmap(waterMask
                        , waterMask.getWidth(), waterMask.getHeight(), false);
                comboImage.drawBitmap(waterMask, 5, -3, null);
                //makerImageView.setImageURI(Uri.fromFile(new File(path,outputName)));
                os = new FileOutputStream(new File(path, outputName));
                cs.compress(Bitmap.CompressFormat.JPEG, 100, os);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setCameraView(){
        Log.d("tui","setCameraView");
        Log.d("tui","Action: "+String.valueOf(actionState));
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) cameraImageView.getLayoutParams();
        int size = (int) Math.ceil(makerImageView.getWidth() / 2);
        layout.height = layout.width = size;

        Log.d("tui",String.valueOf(size));
        if(actionState == TOPLEFT){
            layout.setMargins(0,0,0,0);
        }else if(actionState == TOPRIGHT){
            layout.setMargins(size,0,0,0);

        }else if(actionState == BOTTOMLEFT){
            layout.setMargins(0,size,0,0);
        }
        cameraImageView.setLayoutParams(layout);
        mCamera.startPreview();
    }

    private void finishStep(){
        actionState = TOPLEFT;
        Log.d("tui","finishStep");
        Intent intentStep3 = new Intent(getApplicationContext(),Step3Activity.class);
        intentStep3.putExtra("outputName",outputName);
        intentStep3.putExtra("imageTitle",imageTitle);
        startActivity(intentStep3);
    }
}
