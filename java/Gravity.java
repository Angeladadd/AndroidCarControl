package com.sunchenge.clientapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import java.net.UnknownHostException;

public class Gravity extends Activity {

    private final int FORWARDING_SLOW = 1;
    private final int FORWARDING_FAST = 5;
    private final int LEFT_SLOW = 2;
    private final int LEFT_FAST = 6;
    private final int RIGHT_SLOW = 3;
    private final int RIGHT_FAST = 7;
    private final int BACK_FAST = 4;
    private final int BACK_SLOW = 8;
    private int flag = 0;
    private TextView gsensorInfo;
    private SensorManager sensorMag;
    private Sensor gravitySensor;
    private long lastUpdateTime;

    public SurfaceView tmp_video;
    private dynamicBackground background;

    // 两次检测的时间间隔
    private static final int UPTATE_INTERVAL_TIME = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gravity_control);

        gsensorInfo=(TextView)findViewById(R.id.gsensorInfo);
        sensorMag = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gravitySensor = sensorMag.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorMag.registerListener(sensorLis, gravitySensor,
                SensorManager.SENSOR_DELAY_UI);
        tmp_video= (SurfaceView)findViewById(R.id.videoView) ;
        try {
            background = new dynamicBackground(tmp_video.getHolder(),this);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        System.out.println("create background.....");

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
        //System.exit(0);
        sensorMag.unregisterListener(sensorLis);
        flag=0;
        finishAfterTransition();
        Intent intent = new Intent();
        intent.setClass(Gravity.this,Login.class);
        startActivity(intent);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sensorMag.unregisterListener(sensorLis);
        gsensorInfo.setText("Stop");
        sendMessage('S');
        flag=0;
        finishAfterTransition();
        //System.exit(0);
    }

    private SensorEventListener sensorLis = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        public void onSensorChanged(SensorEvent event) {

            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) {
                return;
            }
            long currentUpdateTime = System.currentTimeMillis();
            long timeInterval = currentUpdateTime - lastUpdateTime;
            // 判断是否达到了检测时间间隔
            if (timeInterval < UPTATE_INTERVAL_TIME)
                return;
            lastUpdateTime = currentUpdateTime;

            // 获取加速度数值，以下三个值为重力分量在设备坐标的分量大小(已修改DATA_X,因为它被弃用（鸽鸽）)
            float x = event.values[0];
            float y = event.values[1];

            int dirX = (int)x;
            int dirY = (int)y;

            switch (getDirecation(dirX, dirY)) {

                case LEFT_FAST: {
                    gsensorInfo.setText("Turning Left: Fast");
                    if(flag!=1)
                        sendMessage('L');
                    flag = 1;

                    break;
                }
                case BACK_FAST: {
                    gsensorInfo.setText("Backwarding: Fast");
                    if(flag!=2)
                        sendMessage('B');//B
                    flag = 2;
                    break;
                }

                case FORWARDING_FAST: {
                    gsensorInfo.setText("Forwarding: Fast");
//                    sendMessage('A');
                    if(flag!=3)
                        sendMessage('A');//
                    flag = 3;
                    break;
                }

                case RIGHT_FAST: {
                    gsensorInfo.setText("Turning Right: Fast");
                    if(flag!=4)
                        sendMessage('R');//R
                    flag=4;
                    break;
                }
                case LEFT_SLOW: {
                    gsensorInfo.setText("Turning Left: Slow");
                    if(flag!=1)
                        sendMessage('l');//l
                    flag = 1;

                    break;
                }
                case BACK_SLOW: {
                    gsensorInfo.setText("Backwarding: Slow");
                    if(flag!=2)
                        sendMessage('b');//b
                    flag = 2;
                    break;
                }

                case FORWARDING_SLOW: {
                    gsensorInfo.setText("Forwarding: Slow");
                    if(flag!=3)
                        sendMessage('a');//a
                    flag = 3;
                    break;
                }

                case RIGHT_SLOW: {
                    gsensorInfo.setText("Turning Right: Slow");
                    if(flag!=4)
                        sendMessage('r');//r
                    flag=4;
                    break;
                }

                default:
                    gsensorInfo.setText("Stop");
                    if(flag!=5)
                        sendMessage('S');
                    flag=5;
                    break;
            }

        }
    };

    public int getDirecation(float x, float y) {
        if (-1 < x && x < 1) {
            if (y > -4 && y < 0) {
                return BACK_SLOW;
            } else if (y <= -4 ) {
                return BACK_FAST;
            } else if (y > 4) {
                return FORWARDING_FAST;
            } else if (y > 1) {
                return FORWARDING_SLOW;
            }
        } else if (x >= 4) {
            return RIGHT_FAST;
        }else if (x > 0 && x < 4) {
            return RIGHT_SLOW;
        } else if (x <= -4) {
            return LEFT_FAST;
        } else if (x > -4 && x < 0) {
            return LEFT_SLOW;
        }
        return -1;
    }


}