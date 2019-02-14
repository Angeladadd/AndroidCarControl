package com.sunchenge.clientapp;


import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.Surface;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

public class FaceControl extends Activity{
    private String TAG = "fuck";
    private Camera mCamera;
    private CameraPreview mPreview;
    //public Button returnbutton;
    private int count = 0;
    private int y = 0, h = 0;
    private int delay;
    private Button facebutton;
    private TextView directionInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.face_control);

        facebutton = (Button)findViewById(R.id.button);
        directionInfo=(TextView)findViewById(R.id.directionInfo);
        facebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Create an instance of Camera
        mCamera = getCameraInstance();
        mCamera.setFaceDetectionListener(faceDetectionListener);
        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    FaceDetectionListener faceDetectionListener = new FaceDetectionListener(){
        @Override
        public void onFaceDetection(Face[] faces, Camera camera){
            if(faces.length == 0){
                //Log.d("fuck", "No Face Detected!");
            }else{
                ++count;
                if(count >= 20){
                    y = (int)faces[0].rect.exactCenterX();

                    h = faces[0].rect.height();
                    System.out.println("Faceloc:" + String.valueOf(h));
                    System.out.println("Faceloc:" + String.valueOf(y));
                    //Log.d("fuck", "face: X: "+ faces[0].rect.centerX() + " Y: " + y+" height: " +h);

                    if(Math.abs(y) > 450){
                        delay = Math.abs(y) / 3;
                        if(y>0) {sendMessage('L');directionInfo.setText("Turning Left");}
                        else {sendMessage('R');directionInfo.setText("Turning Right");}
                    }
                    else if(h<900){
                        delay = (h-700) * 2;
                        sendMessage('B');
                        directionInfo.setText("Going back");
                    }
                    else if(h>1200){
                        delay = (700-h) * 2;;
                        sendMessage('A');
                        directionInfo.setText("Going Forward");
                    }
                    else
                    {
                        sendMessage('S');
                        directionInfo.setText("Stop");
                    }
                    /*new Thread(new Runnable(){
                        public void run(){
                            try{
                                Thread.sleep(delay);
                            } catch(InterruptedException e){
                                e.printStackTrace();
                            }
                            directionInfo.setText("Stop");
                            sendMessage('S');
                        }
                    }).start();*/
                    count = 0;
                }
            }
        }
    };
    /** A safe way to get an instance of the Camera object. */
    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(CameraInfo.CAMERA_FACING_FRONT);
            //c.setDisplayOrientation(90);
            //c.autoFocus(null);// attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
        return super.onOptionsItemSelected(item);
    }

    public boolean sendMessage(char m)
    {
        try{
            char msg = m;
            ((MainActivity)getApplication()).mmOutputStream.write(msg);
            System.out.println("Data Sent!");
            return true;
        }catch (Exception e){
            return false;
        }
    }

    /** A basic Camera preview class */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
        private SurfaceHolder mHolder;
        private Camera mCamera;
        @SuppressWarnings("deprecation")
        public CameraPreview(Context context, Camera camera) {
            super(context);
            mCamera = camera;
            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);
            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException e) {
                Log.d(TAG, "Error setting camera preview: " + e.getMessage());
            }
        }
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
            mCamera.stopFaceDetection();
            mCamera.stopPreview();
            mCamera.release();
        }
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.
            if (mHolder.getSurface() == null){
                // preview surface does not exist
                return;
            }
            // stop preview before making changes
            try {
                mCamera.stopFaceDetection();
                mCamera.stopPreview();
            } catch (Exception e){
                // ignore: tried to stop a non-existent preview
            }
            // set preview size and make any resize, rotate or
            // reformatting changes here
            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
                mCamera.startPreview();
                mCamera.startFaceDetection();
            } catch (Exception e){
                Log.d(TAG, "Error starting camera preview: " + e.getMessage());
            }
        }
    }
    @SuppressLint("NewApi")
    public void onBackPressed() {
        super.onBackPressed();
        sendMessage('S');
        finishAfterTransition();
        //System.exit(0);
    }
    public void returnBack(View view){
        sendMessage('S');
        Intent intent = new Intent();
        intent.setClass(FaceControl.this,Login.class);
        //finishAfterTransition();
        startActivity(intent);
        //System.exit(0);
    }
}


