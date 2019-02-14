package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.net.UnknownHostException;

public class Gesture extends Activity{
    private GestureDetector GestureControler;
    private TextView gestureInfo;
    public SurfaceView tmp_video;
    private dynamicBackground background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_control);
        gestureInfo=(TextView)findViewById(R.id.directionInfo);
        GestureControler = new GestureDetector(this, new GestureListener());
        tmp_video= (SurfaceView)findViewById(R.id.videoView) ;
        try {
            background = new dynamicBackground(tmp_video.getHolder(),this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("create background.....");


    }
    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sendMessage('S');
        //System.exit(0);
        finishAfterTransition();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return GestureControler.onTouchEvent(event);
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onDoubleTap(MotionEvent e)
        {
            // TODO Auto-generated method stub
            Log.i("TEST", "onDoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDown(MotionEvent e)
        {
            // TODO Auto-generated method stub
            System.out.println("");
            Log.i("TEST", "onDown");
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY)
        {
            // TODO Auto-generated method stub
            Log.i("TEST", "onFling:velocityX = " + velocityX + " velocityY" + velocityY);
            float minMove = 120;         //最小滑动距离
            float maxMove = 300;         //最大滑动距离
            float minVelocity = 0;      //最小滑动速度
            float beginX = e1.getX();
            float endX = e2.getX();
            float beginY = e1.getY();
            float endY = e2.getY();

            if(beginX-endX>maxMove&&Math.abs(velocityX)>minVelocity){   //左滑
                gestureInfo.setText("Forwarding: Fast");
                sendMessage('A');//L
            }else if(endX-beginX>maxMove&&Math.abs(velocityX)>minVelocity){   //右滑
                gestureInfo.setText("Backwarding: Fast");
                sendMessage('B');//R
            }else if(beginY-endY>maxMove&&Math.abs(velocityY)>minVelocity){   //上滑
                gestureInfo.setText("Turning Right: Fast");
                sendMessage('R');//A
            }else if(endY-beginY>maxMove&&Math.abs(velocityY)>minVelocity){   //下滑
                gestureInfo.setText("Turning Left: Fast");
                sendMessage('L');//B
            }else if(beginX-endX>minMove&&Math.abs(velocityX)>minVelocity){   //左滑
                gestureInfo.setText("Forwarding: Slow");
                sendMessage('a');//l
            }else if(endX-beginX > minMove && Math.abs(velocityX)>minVelocity){   //右滑
                gestureInfo.setText("Backwarding: Slow");
                sendMessage('b');//r
            }else if(beginY-endY>minMove&&Math.abs(velocityY)>minVelocity){   //上滑
                 gestureInfo.setText("Turning Right: Slow");
                sendMessage('r');//a
            }else if(endY-beginY>minMove&&Math.abs(velocityY)>minVelocity){   //下滑
                gestureInfo.setText("Turning Left: Slow");
                sendMessage('l');//b
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e)
        {
            // TODO Auto-generated method stub
            Log.i("TEST", "onLongPress");
            gestureInfo.setText("Waiting");
            sendMessage('S');
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY)
        {
            // TODO Auto-generated method stub
            Log.i("TEST", "onScroll:distanceX = " + distanceX + " distanceY = " + distanceY);
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e)
        {
            // TODO Auto-generated method stub
            Log.i("TEST", "onSingleTapUp");
            return super.onSingleTapUp(e);
        }

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
    @SuppressLint("NewApi")
    public void returnBack(View view){
        sendMessage('S');
        Intent intent = new Intent();
        intent.setClass(Gesture.this,Login.class);
        startActivity(intent);
        //System.exit(0);
    }


}
